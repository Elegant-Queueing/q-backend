package com.careerfair.q.workflow.queue.physical.implementation;

import com.careerfair.q.model.redis.Employee;
import com.careerfair.q.model.redis.Student;
import com.careerfair.q.model.redis.StudentQueueStatus;
import com.careerfair.q.service.database.StudentFirebase;
import com.careerfair.q.service.queue.response.EmployeeQueueData;
import com.careerfair.q.service.queue.response.QueueStatus;
import com.careerfair.q.util.enums.QueueType;
import com.careerfair.q.util.exception.FirebaseException;
import com.careerfair.q.util.exception.InvalidRequestException;
import com.careerfair.q.workflow.queue.AbstractQueueWorkflow;
import com.careerfair.q.workflow.queue.physical.PhysicalQueueWorkflow;
import com.google.cloud.Timestamp;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.util.List;

import static com.careerfair.q.service.queue.implementation.QueueServiceImpl.EMPLOYEE_CACHE_NAME;
import static com.careerfair.q.service.queue.implementation.QueueServiceImpl.STUDENT_CACHE_NAME;

@Component
public class PhysicalQueueWorkflowImpl extends AbstractQueueWorkflow
        implements PhysicalQueueWorkflow {

    @Autowired private RedisTemplate<String, String> employeeRedisTemplate;
    @Autowired private RedisTemplate<String, Student> queueRedisTemplate;
    @Autowired private RedisTemplate<String, String> studentRedisTemplate;

    @Autowired private StudentFirebase studentFirebase;

    @Override
    public QueueStatus joinQueue(String employeeId, Student student) {
        Employee employee = getEmployeeWithId(employeeId);
        StudentQueueStatus studentQueueStatus = getStudentQueueStatus(student.getId());

        if (!employeeId.equals(studentQueueStatus.getEmployeeId())) {
            throw new InvalidRequestException("Mismatch between employee id=" + employeeId +
                    " and assigned employee id=" + studentQueueStatus.getEmployeeId());
        }

        String physicalQueueId = checkQueueAssociated(employee);
        List<Student> studentsInPhysicalQueue = queueRedisTemplate.opsForList()
                .range(physicalQueueId, 0L, -1L);
        assert studentsInPhysicalQueue != null;

        if (getStudentPosition(student.getId(), studentsInPhysicalQueue) != -1) {
            throw new InvalidRequestException("Student with student id=" + student.getId() +
                    " is already present in the queue of the employee with employee id=" +
                    employeeId);
        }

        updateStudentQueueStatus(studentQueueStatus, employee);
        studentRedisTemplate.opsForHash().put(STUDENT_CACHE_NAME, student.getId(),
                studentQueueStatus);
        queueRedisTemplate.opsForList().rightPush(physicalQueueId, student);

        return createQueueStatus(studentQueueStatus, employee);
    }

    @Override
    public void leaveQueue(String employeeId, String studentId) {
        removeStudent(getEmployeeWithId(employeeId), studentId, false);
    }

    @Override
    public Employee addQueue(String employeeId) {
        Employee employee = getEmployeeWithId(employeeId);

        if (employee.getPhysicalQueueId() != null) {
            throw new InvalidRequestException("Employee with employee id=" + employeeId +
                    " is associated with physical queue with id=" + employee.getPhysicalQueueId());
        }

        employee.setPhysicalQueueId(generateRandomId());
        employeeRedisTemplate.opsForHash().put(EMPLOYEE_CACHE_NAME, employeeId, employee);
        return employee;
    }

    @Override
    public Employee removeQueue(String employeeId, boolean isEmpty) {
        Employee employee = getEmployeeWithId(employeeId);
        String physicalQueueId = checkQueueAssociated(employee);

        Long size = queueRedisTemplate.opsForList().size(physicalQueueId);
        assert size != null;

        if (isEmpty && size != 0) {
            throw new InvalidRequestException("Physical queue with id=" + physicalQueueId +
                    " is not empty");
        }

        List<Student> studentsInPhysicalQueue = queueRedisTemplate.opsForList()
                .range(physicalQueueId, 0L, -1L);
        assert studentsInPhysicalQueue != null;

        for (int i = 0; i < studentsInPhysicalQueue.size(); i++) {
            queueRedisTemplate.opsForList().leftPop(physicalQueueId);
        }

        employee.setPhysicalQueueId(null);
        employeeRedisTemplate.opsForHash().put(EMPLOYEE_CACHE_NAME, employeeId, employee);
        return employee;
    }

    @Override
    public EmployeeQueueData registerStudent(String employeeId, String studentId) {
        Employee employee = getEmployeeWithId(employeeId);
        StudentQueueStatus studentQueueStatus = removeStudent(employee, studentId, true);

        if (!studentFirebase.registerStudent(studentId, employeeId)) {
            throw new FirebaseException("Unexpected error in firebase. Student failed to register");
        }

        updateRedisEmployee(employee, studentQueueStatus);
        employeeRedisTemplate.opsForHash().put(EMPLOYEE_CACHE_NAME, employeeId, employee);

        return getEmployeeQueueData(employeeId);
    }

    @Override
    public EmployeeQueueData removeStudentFromQueue(String employeeId, String studentId) {
        Employee employee = getEmployeeWithId(employeeId);
        removeStudent(employee, studentId, true);
        return getEmployeeQueueData(employeeId);
    }

    @Override
    public EmployeeQueueData getEmployeeQueueData(String employeeId) {
        Employee employee = getEmployeeWithId(employeeId);
        List<Student> students = queueRedisTemplate.opsForList()
                .range(employee.getPhysicalQueueId(), 0L, -1L);

        return new EmployeeQueueData(students, employee.getNumRegisteredStudents(),
                calcEmployeeAverageTime(employee));
    }

    @Override
    public Long size(String employeeId) {
        Employee employee = getEmployeeWithId(employeeId);
        return queueRedisTemplate.opsForList().size(employee.getPhysicalQueueId());
    }

    /**
     * Removes the first student in the employee's queue
     *
     * @param employee employee from whose queue the student is to be removed
     * @param studentId id of the student to be removed
     * @param isFirst whether the student to be removed should be the first in the queue
     * @return StudentQueueStatus
     * @throws InvalidRequestException throws if the student is not present in the employee's
     *      queue or the student is not the first in queue if isFirst flag is set
     */
    private StudentQueueStatus removeStudent(Employee employee, String studentId, boolean isFirst)
            throws InvalidRequestException{
        List<Student> studentsInPhysicalQueue = queueRedisTemplate.opsForList()
                .range(employee.getPhysicalQueueId(), 0L, -1L);
        assert studentsInPhysicalQueue != null;

        int position = getStudentPosition(studentId, studentsInPhysicalQueue);

        if (position == -1) {
            throw new InvalidRequestException("Student with student id=" + studentId +
                    " is not present in the queue of employee with employee id=" +
                    employee.getId());
        } else if (isFirst && position != 0) {
            throw new InvalidRequestException("Student with student id=" + studentId +
                    " is not at the head of the queue of employee with employee id=" +
                    employee.getId());
        }

        StudentQueueStatus studentQueueStatus = (StudentQueueStatus) studentRedisTemplate
                .opsForHash().get(STUDENT_CACHE_NAME, studentId);
        studentRedisTemplate.opsForHash().delete(STUDENT_CACHE_NAME, studentId);

        if (isFirst) {
            queueRedisTemplate.opsForList().leftPop(employee.getPhysicalQueueId());
        } else {
            queueRedisTemplate.opsForList().remove(employee.getPhysicalQueueId(), 1L,
                    studentsInPhysicalQueue.get(position));
        }

        return studentQueueStatus;
    }

    /**
     * Calculates and returns the average time spent by the employee talking to a student
     *
     * @param employee The employee whose average time is to be calculated
     * @return double Average time spent by the employee talking to a student
     */
    private double calcEmployeeAverageTime(Employee employee) {
        return employee.getTotalTimeSpent() * 1. / Math.max(employee.getNumRegisteredStudents(), 1);
    }

    /**
     * Creates and returns QueueStatus based on the given studentQueueStatus and employee
     *
     * @param studentQueueStatus student's queue status
     * @param employee employee in whose queue the student is present
     * @return QueueStatus
     */
    private QueueStatus createQueueStatus(StudentQueueStatus studentQueueStatus,
                                          Employee employee) {
        Long positionInPhysicalQueue = size(employee.getId());
        int waitTime = (int) (calcEmployeeAverageTime(employee) * positionInPhysicalQueue);

        QueueStatus queueStatus = new QueueStatus(studentQueueStatus.getQueueId(),
                studentQueueStatus.getQueueType(), studentQueueStatus.getRole(),
                positionInPhysicalQueue.intValue(), waitTime);
        queueStatus.setEmployee(employee);
        return queueStatus;
    }

    /**
     * Updates and returns an employee to be stored in Redis
     *
     * @param employee employee to update
     * @param studentQueueStatus student's queue status
     */
    private void updateRedisEmployee(Employee employee, StudentQueueStatus studentQueueStatus) {
        long timeSpent = Timestamp.now().getSeconds() -
                studentQueueStatus.getJoinedPhysicalQueueAt().getSeconds();
        employee.setNumRegisteredStudents(employee.getNumRegisteredStudents() + 1);
        employee.setTotalTimeSpent(employee.getTotalTimeSpent() + timeSpent);
    }

    /**
     * Updates the student's queue status based on the given employee
     *
     * @param studentQueueStatus the status to update
     * @param employee the employee to update
     */
    private void updateStudentQueueStatus(StudentQueueStatus studentQueueStatus,
                                          Employee employee) {
        studentQueueStatus.setEmployeeId(employee.getId());
        studentQueueStatus.setQueueId(employee.getPhysicalQueueId());
        studentQueueStatus.setQueueType(QueueType.PHYSICAL);
        studentQueueStatus.setJoinedPhysicalQueueAt(Timestamp.now());
    }

    /**
     * Checks whether a physical queue is associated with the given employee
     *
     * @param employee employee to validate
     * @return String id of the physical queue associated with the given employee
     * @throws InvalidRequestException throws the exception if no physical queue is associated with
     *      the employee
     */
    private String checkQueueAssociated(Employee employee) throws InvalidRequestException {
        String physicalQueueId = employee.getPhysicalQueueId();
        if (physicalQueueId == null) {
            throw new InvalidRequestException("Employee with employee id=" + employee.getId() +
                    " is not associated with any physical queue");
        }
        return physicalQueueId;
    }
}

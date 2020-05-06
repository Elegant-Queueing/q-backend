package com.careerfair.q.workflow.queue.employee.physical.implementation;

import com.careerfair.q.model.redis.Employee;
import com.careerfair.q.model.redis.Student;
import com.careerfair.q.model.redis.StudentQueueStatus;
import com.careerfair.q.service.database.StudentFirebase;
import com.careerfair.q.service.queue.response.EmployeeQueueData;
import com.careerfair.q.service.queue.response.QueueStatus;
import com.careerfair.q.util.enums.QueueType;
import com.careerfair.q.util.exception.FirebaseException;
import com.careerfair.q.util.exception.InvalidRequestException;
import com.careerfair.q.workflow.queue.employee.AbstractEmployeeQueueWorkflow;
import com.careerfair.q.workflow.queue.employee.physical.PhysicalQueueWorkflow;
import com.google.cloud.Timestamp;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.util.List;

import static com.careerfair.q.service.queue.implementation.QueueServiceImpl.*;

@Component
public class PhysicalQueueWorkflowImpl extends AbstractEmployeeQueueWorkflow
        implements PhysicalQueueWorkflow {

    @Autowired private RedisTemplate<String, String> employeeRedisTemplate;
    @Autowired private RedisTemplate<String, Student> queueRedisTemplate;

    @Autowired private StudentFirebase studentFirebase;

    @Override
    public QueueStatus joinQueue(String employeeId, Student student,
                                 StudentQueueStatus studentWindowQueueStatus) {
        if (!employeeId.equals(studentWindowQueueStatus.getEmployeeId())) {
            throw new InvalidRequestException("Mismatch between employee id=" + employeeId +
                    " and assigned employee id=" + studentWindowQueueStatus.getEmployeeId());
        }
        if (studentWindowQueueStatus.getJoinedWindowQueueAt().getSeconds() + WINDOW <
                Timestamp.now().getSeconds()) {
            throw new InvalidRequestException("Student with student id=" + student.getId() +
                    " did not scan the code od employee with employee id=" + employeeId +
                    " in time");
        }

        Employee employee = getEmployeeWithId(employeeId);
        StudentQueueStatus studentQueueStatus = addStudent(employee, student,
                studentWindowQueueStatus);

        long currentPosition = size(employeeId);
        studentQueueStatus.setPositionWhenJoinedPhysicalQueue(currentPosition);
        return createQueueStatus(studentQueueStatus, employee, currentPosition);
    }

    @Override
    public void leaveQueue(String employeeId, String studentId) {
        Employee employee = getEmployeeWithId(employeeId);
        removeStudent(employeeId, checkQueueAssociated(employee), studentId, false);
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
        removeQueue(employee, isEmpty);
        employee.setPhysicalQueueId(null);
        employeeRedisTemplate.opsForHash().put(EMPLOYEE_CACHE_NAME, employeeId, employee);
        return employee;
    }

    @Override
    public EmployeeQueueData registerStudent(String employeeId, String studentId) {
        Employee employee = getEmployeeWithId(employeeId);
        StudentQueueStatus studentQueueStatus = removeStudent(employeeId,
                checkQueueAssociated(employee), studentId, true);

        if (!studentFirebase.registerStudent(studentId, employeeId)) {
            throw new FirebaseException("Unexpected error in firebase. Student failed to register");
        }

        updateRedisEmployee(employee, studentQueueStatus);
        employeeRedisTemplate.opsForHash().put(EMPLOYEE_CACHE_NAME, employeeId, employee);

        return getEmployeeQueueData(employeeId);
    }

    @Override
    public EmployeeQueueData skipStudent(String employeeId, String studentId) {
        Employee employee = getEmployeeWithId(employeeId);
        removeStudent(employeeId, employee.getPhysicalQueueId(), studentId, true);
        return getEmployeeQueueData(employeeId);
    }

    @Override
    public EmployeeQueueData getEmployeeQueueData(String employeeId) {
        Employee employee = getEmployeeWithId(employeeId);
        List<Student> students = queueRedisTemplate.opsForList()
                .range(checkQueueAssociated(employee), 0L, -1L);

        return new EmployeeQueueData(students, employee.getNumRegisteredStudents(),
                calcEmployeeAverageTime(employee));
    }

    @Override
    public Long size(String employeeId) {
        return super.size(employeeId);
    }

    @Override
    protected void updateStudentQueueStatus(StudentQueueStatus studentQueueStatus,
                                            Employee employee) {
        studentQueueStatus.setQueueId(employee.getPhysicalQueueId());
        studentQueueStatus.setQueueType(QueueType.PHYSICAL);
        studentQueueStatus.setJoinedPhysicalQueueAt(Timestamp.now());
    }

    @Override
    protected String checkQueueAssociated(Employee employee) throws InvalidRequestException {
        String physicalQueueId = employee.getPhysicalQueueId();
        if (physicalQueueId == null) {
            throw new InvalidRequestException("Employee with employee id=" + employee.getId() +
                    " is not associated with any physical queue");
        }
        return physicalQueueId;
    }

    /**
     * Updates and returns an employee to be stored in Redis
     *
     * @param employee employee to update
     * @param studentQueueStatus student's queue status
     */
    private void updateRedisEmployee(Employee employee, StudentQueueStatus studentQueueStatus) {
        long timeSpent = (Timestamp.now().getSeconds() -
                studentQueueStatus.getJoinedPhysicalQueueAt().getSeconds()) /
                studentQueueStatus.getPositionWhenJoinedPhysicalQueue();
        employee.setNumRegisteredStudents(employee.getNumRegisteredStudents() + 1);
        employee.setTotalTimeSpent(employee.getTotalTimeSpent() + timeSpent);
    }
}

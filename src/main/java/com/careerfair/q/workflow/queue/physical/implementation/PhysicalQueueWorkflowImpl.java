package com.careerfair.q.workflow.queue.physical.implementation;

import com.careerfair.q.model.redis.Student;
import com.careerfair.q.service.database.StudentFirebase;
import com.careerfair.q.util.enums.QueueType;
import com.careerfair.q.util.enums.Role;
import com.careerfair.q.model.redis.Employee;
import com.careerfair.q.model.redis.Company;
import com.careerfair.q.service.queue.response.EmployeeQueueData;
import com.careerfair.q.service.queue.response.QueueStatus;
import com.careerfair.q.util.exception.FirebaseException;
import com.careerfair.q.util.exception.InvalidRequestException;
import com.careerfair.q.workflow.queue.AbstractQueueWorkflow;
import com.careerfair.q.workflow.queue.physical.PhysicalQueueWorkflow;

import com.google.cloud.Timestamp;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.util.*;

import static com.careerfair.q.service.queue.implementation.QueueServiceImpl.EMPLOYEE_CACHE_NAME;

@Component
public class PhysicalQueueWorkflowImpl extends AbstractQueueWorkflow
        implements PhysicalQueueWorkflow {

    @Autowired private RedisTemplate<String, Role> companyRedisTemplate;
    @Autowired private RedisTemplate<String, String> employeeRedisTemplate;
    @Autowired private RedisTemplate<String, Student> queueRedisTemplate;

    @Autowired private StudentFirebase studentFirebase;

    @Override
    public QueueStatus joinQueue(String employeeId, Student student) {
        Employee employee = getEmployeeWithId(employeeId);
        String physicalQueueId = employee.getPhysicalQueueId();
        List<Student> studentsInPhysicalQueue = queueRedisTemplate.opsForList()
                .range(physicalQueueId, 0L, -1L);
        assert studentsInPhysicalQueue != null;

        if (getStudentIndexInQueue(student.getId(), studentsInPhysicalQueue) != -1) {
            throw new InvalidRequestException("Student with student id=" + student.getId() +
                    " is already present in the queue of the employee with employee id=" +
                    employeeId);
        }

        student.setJoinedPhysicalQueueAt(Timestamp.now());
        queueRedisTemplate.opsForList().rightPush(physicalQueueId, student);

        Long positionInPhysicalQueue = size(employeeId);
        int waitTime = (int) (calcEmployeeAverageTime(employee) * positionInPhysicalQueue);

        return new QueueStatus(QueueType.PHYSICAL, positionInPhysicalQueue.intValue(), waitTime,
                student.getJoinedPhysicalQueueAt(), employee);
    }

    @Override
    public void leaveQueue(String employeeId, String studentId) {
        removeStudentInQueue(getEmployeeWithId(employeeId), studentId, false);
    }

    @Override
    public EmployeeQueueData addQueue(String companyId, String employeeId, Role role) {
        if (employeeRedisTemplate.opsForHash().hasKey(EMPLOYEE_CACHE_NAME, employeeId)) {
            throw new InvalidRequestException("Employee with employee id=" + employeeId +
                    " already has a queue.");
        }

        employeeRedisTemplate.opsForHash().put(EMPLOYEE_CACHE_NAME, employeeId,
                createRedisEmployee(companyId, employeeId, role));

        Company company = (Company) companyRedisTemplate.opsForHash().get(companyId, role);
        if (company == null) {
            company = createRedisCompany();
        }
        company.getEmployeeIds().add(employeeId);
        companyRedisTemplate.opsForHash().put(companyId, role, company);

        return createEmployeeQueueData();
    }

    @Override
    public EmployeeQueueData pauseQueue(String employeeId) {
        // TODO
        return null;
    }

    @Override
    public EmployeeQueueData registerStudent(String employeeId, String studentId) {
        Employee employee = getEmployeeWithId(employeeId);
        Student student = removeStudentInQueue(employee, studentId, true);

        if (!studentFirebase.registerStudent(student, employee)) {
            throw new FirebaseException("Unexpected error in firebase");
        }

        updateRedisEmployee(employee, student);
        employeeRedisTemplate.opsForHash().put(EMPLOYEE_CACHE_NAME, employeeId, employee);

        List<Student> studentsLeftInQueue = queueRedisTemplate.opsForList()
                .range(employee.getPhysicalQueueId(), 0L, -1L);
        return new EmployeeQueueData(studentsLeftInQueue, employee.getNumRegisteredStudents(),
                calcEmployeeAverageTime(employee));
    }

    @Override
    public EmployeeQueueData removeStudent(String employeeId, String studentId) {
        Employee employee = getEmployeeWithId(employeeId);
        removeStudentInQueue(employee, studentId, true);

        List<Student> studentsLeftInQueue = queueRedisTemplate.opsForList()
                .range(employee.getPhysicalQueueId(), 0L, -1L);

        return new EmployeeQueueData(studentsLeftInQueue, employee.getNumRegisteredStudents(),
                calcEmployeeAverageTime(employee));
    }

    @Override
    public EmployeeQueueData getEmployeeQueueData(String employeeId) {
        Employee employee = getEmployeeWithId(employeeId);

        double averageTimePerStudent = calcEmployeeAverageTime(employee);
        int numRegisteredStudents = employee.getNumRegisteredStudents();
        List<Student> students = queueRedisTemplate.opsForList()
                .range(employee.getPhysicalQueueId(), 0L, -1L);

        return new EmployeeQueueData(students, numRegisteredStudents, averageTimePerStudent);
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
     * @return Student
     * @throws InvalidRequestException throws if the student is not present in the employee's
     *      queue or the student is not the first in queue if isFirst flag is set
     */
    private Student removeStudentInQueue(Employee employee, String studentId, boolean isFirst)
            throws InvalidRequestException{
        List<Student> studentsInPhysicalQueue = queueRedisTemplate.opsForList()
                .range(employee.getPhysicalQueueId(), 0L, -1L);
        assert studentsInPhysicalQueue != null;

        int position = getStudentIndexInQueue(studentId, studentsInPhysicalQueue);

        if (position == -1) {
            throw new InvalidRequestException("Student with student id=" + studentId +
                    " is not present in the queue of employee with employee id=" +
                    employee.getId());
        } else if (isFirst && position != 0) {
            throw new InvalidRequestException("Student with student id=" + studentId +
                    " is not at the head of the queue of employee with employee id=" +
                    employee.getId());
        }

        return queueRedisTemplate.opsForList().leftPop(employee.getPhysicalQueueId());
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
     * Creates and returns an employee to be stored in Redis
     *
     * @param employeeId id of the newly created employee
     * @return Employee
     */
    private Employee createRedisEmployee(String companyId, String employeeId, Role role) {
        return new Employee(employeeId, companyId, role, generateRandomId(), generateRandomId());
    }

    /**
     * Updates and returns an employee to be stored in Redis
     *
     * @param employee employee to update
     * @param studentRegistered student to register with the employee
     */
    private void updateRedisEmployee(Employee employee, Student studentRegistered) {
        int timeSpent = (int) (Timestamp.now().getSeconds() -
                studentRegistered.getJoinedWindowQueueAt().getSeconds());
        employee.setNumRegisteredStudents(employee.getNumRegisteredStudents() + 1);
        employee.setTotalTimeSpent(employee.getTotalTimeSpent() + timeSpent);
    }

    /**
     * Creates and returns a representation of a company to be stored in Redis
     *
     * @return Company
     */
    private Company createRedisCompany() {
        return new Company(generateRandomId(), new HashSet<>());
    }

    /**
     * Creates and returns a snapshot of employee's queue
     *
     * @return EmployeeQueueData
     */
    private EmployeeQueueData createEmployeeQueueData() {
        return new EmployeeQueueData(new ArrayList<>(), 0, 0);
    }

    /**
     * Generates and returns a unique random id
     *
     * @return String representing the unique id
     */
    private String generateRandomId() {
        return UUID.randomUUID().toString();
    }
}

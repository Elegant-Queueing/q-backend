package com.careerfair.q.workflow.queue.physical.implementation;

import com.careerfair.q.model.redis.Student;
import com.careerfair.q.util.enums.QueueType;
import com.careerfair.q.util.enums.Role;
import com.careerfair.q.model.redis.Employee;
import com.careerfair.q.model.redis.Company;
import com.careerfair.q.service.queue.response.EmployeeQueueData;
import com.careerfair.q.service.queue.response.QueueStatus;
import com.careerfair.q.util.exception.InvalidRequestException;
import com.careerfair.q.workflow.queue.physical.PhysicalQueueWorkflow;

import com.google.cloud.Timestamp;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.stream.Collectors;

@Component
public class PhysicalQueueWorkflowImpl implements PhysicalQueueWorkflow {

    @Autowired private RedisTemplate<String, Role> companyRedisTemplate;
    @Autowired private RedisTemplate<String, String> employeeRedisTemplate;
    @Autowired private RedisTemplate<String, Student> queueRedisTemplate;

    private static final String EMPLOYEE_CACHE_NAME = "employees";
    private static final int WINDOW = 300;  // in seconds

    @Override
    public QueueStatus joinQueue(String companyId, String employeeId, String studentId, Role role) {
        Employee employee = getEmployeeWithId(employeeId);
        checkEmployeeHasQueueOpen(companyId, employeeId, role);

        List<Student> studentsInWindowQueue = queueRedisTemplate.opsForList()
                .range(employee.getWindowQueueId(), 0L, -1L);
        assert studentsInWindowQueue != null;

        int positionInWindowQueue = getStudentIndexInQueue(employeeId, studentId,
                studentsInWindowQueue);

        Student student = studentsInWindowQueue.get(positionInWindowQueue);
        queueRedisTemplate.opsForList().remove(employee.getWindowQueueId(), 1, student);

        if (student.getJoinedWindowQueueAt().getSeconds() + WINDOW < Timestamp.now().getSeconds()) {
            throw new InvalidRequestException("Student with student id=" + studentId +
                    " did not scan QR code in time. Student has been removed from queue");
        }

        queueRedisTemplate.opsForList().rightPush(employee.getPhysicalQueueId(), student);
        Long positionInPhysicalQueue = queueRedisTemplate.opsForList()
                .size(employee.getPhysicalQueueId());
        assert positionInPhysicalQueue != null;
        int waitTime = (int) (calcEmployeeAverageTime(employee) * positionInPhysicalQueue);

        return new QueueStatus(employee.getPhysicalQueueId(), QueueType.PHYSICAL, role,
                positionInPhysicalQueue.intValue(), waitTime, Timestamp.now(), employee);
    }

    @Override
    public QueueStatus leaveQueue(String companyId, String employeeId, String studentId) {
        // TODO
        return null;
    }

    @Override
    public EmployeeQueueData addQueue(String companyId, String employeeId, Role role) {
        if (employeeRedisTemplate.opsForHash().hasKey(EMPLOYEE_CACHE_NAME, employeeId)) {
            throw new InvalidRequestException("Employee with employee id=" + employeeId +
                    " already has a queue.");
        }

        employeeRedisTemplate.opsForHash().put(EMPLOYEE_CACHE_NAME, employeeId,
                createRedisEmployee(employeeId));

        Company company = (Company) companyRedisTemplate.opsForHash().get(companyId, role);
        if (company == null) {
            company = createRedisCompany();
        }
        company.getEmployeeIds().add(employeeId);
        companyRedisTemplate.opsForHash().put(companyId, role, company);

        return createEmployeeQueueData();
    }

    @Override
    public EmployeeQueueData pauseQueue(String companyId, String employeeId) {
        // TODO
        return null;
    }

    @Override
    public EmployeeQueueData registerStudent(String employeeId, String studentId) {
        // TODO
        return null;
    }

    @Override
    public EmployeeQueueData removeStudent(String employeeId, String studentId) {
        removeFirstStudentInQueue(employeeId, studentId);

        Employee employee = (Employee) employeeRedisTemplate.opsForHash().get(EMPLOYEE_CACHE_NAME,
                employeeId);
        assert employee != null;

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

    /**
     * Removes the first student in the employee's queue
     *
     * @param employeeId id of the employee from whose queue the student is to be removed
     * @param studentId id of the student to be removed
     */
    private void removeFirstStudentInQueue(String employeeId, String studentId) {
        Employee employee = getEmployeeWithId(employeeId);

        List<Student> studentsInPhysicalQueue = queueRedisTemplate.opsForList()
                .range(employee.getPhysicalQueueId(), 0L, -1L);
        assert studentsInPhysicalQueue != null;

        int position = getStudentIndexInQueue(employeeId, studentId, studentsInPhysicalQueue);

        if (position != 0) {
            throw new InvalidRequestException("Student with student id=" + studentId +
                    " is not at the head of the queue of employee with employee id=" +
                    employee.getId());
        }

        queueRedisTemplate.opsForList().leftPop(employee.getPhysicalQueueId());
    }

    /**
     * Checks and returns whether the employee is present at the career fair
     *
     * @param employeeId id of the employee to check for
     * @return Employee
     * @throws InvalidRequestException throws the exception if the employee is not present at the
     *      career fair
     */
    private Employee getEmployeeWithId(String employeeId) throws InvalidRequestException {
        Employee employee = (Employee) employeeRedisTemplate.opsForHash().get(EMPLOYEE_CACHE_NAME,
                employeeId);
        if (employee == null) {
            throw new InvalidRequestException("No such employee with employee id=" + employeeId +
                    " exists");
        }
        return employee;
    }

    /**
     * Checks if the given employee is associated with the given company for the given role
     *
     * @param companyId id of the company associated with the employee
     * @param employeeId id of the employee
     * @param role role for which the employee is recruiting
     * @throws InvalidRequestException throws the exception if the company is not associated with
     *      employee for the given role
     */
    private void checkEmployeeHasQueueOpen(String companyId, String employeeId, Role role)
            throws InvalidRequestException {
        Company company = (Company) companyRedisTemplate.opsForHash().get(companyId, role);
        if (company == null || !company.getEmployeeIds().contains(employeeId)) {
            throw new InvalidRequestException("No company with company id=" + companyId +
                    " is associated with employee with employee id=" + employeeId +
                    " for role=" + role);
        }
    }

    /**
     * Gets and returns index of the given student in the given queue
     *
     * @param employeeId id of the employee in whose queue the student is present
     * @param studentId id of the student to find in the queue
     * @param queue queue to find the student in
     * @return int position of the student in the queue. -1 if not present
     * @throws InvalidRequestException throws the exception if the student is not present in the
     *      employee's queue
     */
    private int getStudentIndexInQueue(String employeeId, String studentId, List<Student> queue)
            throws InvalidRequestException {
        List<String> studentIdsInWindowQueue = queue.stream()
                .map(Student::getId)
                .collect(Collectors.toList());
        int index = studentIdsInWindowQueue.indexOf(studentId);
        
        if (index == -1) {
            throw new InvalidRequestException("Student with student id=" + studentId +
                    " is not present in the queue of employee with employee id=" + employeeId);
        }
        
        return index;
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
    private Employee createRedisEmployee(String employeeId) {
        return new Employee(employeeId, generateRandomId(), generateRandomId(), 0, 0);
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

package com.careerfair.q.workflow.queue.virtual.implementation;

import com.careerfair.q.model.redis.Employee;
import com.careerfair.q.model.redis.Student;
import com.careerfair.q.model.redis.StudentQueueStatus;
import com.careerfair.q.model.redis.VirtualQueueData;
import com.careerfair.q.service.queue.response.EmployeeQueueData;
import com.careerfair.q.util.enums.QueueType;
import com.careerfair.q.util.enums.Role;
import com.careerfair.q.service.queue.response.QueueStatus;
import com.careerfair.q.util.exception.InvalidRequestException;
import com.careerfair.q.workflow.queue.AbstractQueueWorkflow;
import com.careerfair.q.workflow.queue.virtual.VirtualQueueWorkflow;
import com.google.cloud.Timestamp;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static com.careerfair.q.service.queue.implementation.QueueServiceImpl.EMPLOYEE_CACHE_NAME;
import static com.careerfair.q.service.queue.implementation.QueueServiceImpl.STUDENT_CACHE_NAME;

@Component
public class VirtualQueueWorkflowImpl extends AbstractQueueWorkflow
        implements VirtualQueueWorkflow {

    @Autowired private RedisTemplate<String, Role> companyRedisTemplate;
    @Autowired private RedisTemplate<String, String> employeeRedisTemplate;
    @Autowired private RedisTemplate<String, Student> queueRedisTemplate;
    @Autowired private RedisTemplate<String, String> studentRedisTemplate;

    @Override
    public StudentQueueStatus joinQueue(String companyId, String studentId, Role role, String studentName) {
        // virtual queue should not be responsible for creating student objects
        StudentQueueStatus studentQueueStatus = getStudentQueueStatus(studentId);
        if(studentQueueStatus.getQueueType() != QueueType.NONE) {
            throw new InvalidRequestException("Student with id: " + studentId + " already in a queue");
        }

        VirtualQueueData virtualQueueData = (VirtualQueueData) companyRedisTemplate.opsForHash().get(companyId, role);
        if (virtualQueueData == null || virtualQueueData.getEmployeeIds().size() == 0) {
            throw new InvalidRequestException("No employee with companyId: " + companyId + " is currently " +
                    "taking students for role: " + role);
        }

        String virtualQueueId = virtualQueueData.getVirtualQueueId();
        queueRedisTemplate.opsForList().rightPush(virtualQueueId, new Student(studentId, studentName));

        studentQueueStatus.setCompanyId(companyId);
        studentQueueStatus.setRole(role);
        studentQueueStatus.setQueueId(virtualQueueId);
        studentQueueStatus.setQueueType(QueueType.VIRTUAL);
        studentQueueStatus.setJoinedVirtualQueueAt(Timestamp.now());
        studentRedisTemplate.opsForHash().put(STUDENT_CACHE_NAME, studentId, studentQueueStatus);
        return studentQueueStatus;
    }

    @Override
    public StudentQueueStatus leaveQueue(String companyId, String studentId, Role role) {
        StudentQueueStatus studentQueueStatus = getStudentQueueStatus(studentId);
        if(studentQueueStatus.getQueueType() != QueueType.VIRTUAL) {
            throw new InvalidRequestException("Student with id: " + studentId + " is not present in any virtual queue");
        }

        VirtualQueueData virtualQueueData = (VirtualQueueData) companyRedisTemplate.opsForHash().get(companyId, role);
        // if the virtual queue was stopped, students should have been cleared form the queue
        if (virtualQueueData == null || virtualQueueData.getEmployeeIds().size() == 0) {
            throw new InvalidRequestException("No virtual queue present for companyId: " + companyId +
                    " and role: " + role);
        }

        String virtualQueueId = studentQueueStatus.getQueueId();
        assert virtualQueueId.equals(virtualQueueData.getVirtualQueueId());

        List<Student> studentsInQueue = queueRedisTemplate.opsForList().range(virtualQueueId, 0L, -1L);
        assert studentsInQueue != null;

        Student student = null;
        for(Student s : studentsInQueue) {
            if(s.getId().equals(studentId)) {
                student = s;
            }
        }
        assert student != null;
        queueRedisTemplate.opsForList().remove(virtualQueueId, 0L, student);
        studentQueueStatus = new StudentQueueStatus(studentId);
        studentRedisTemplate.opsForHash().put(STUDENT_CACHE_NAME, studentId, studentQueueStatus);
        return studentQueueStatus;
    }

    @Override
    public String addQueue(String companyId, String employeeId, Role role) {
        Employee employee = getEmployeeWithId(employeeId);
        if(!isFalsy(employee.getVirtualQueueId())) {
            throw new InvalidRequestException("Employee with id: " + employeeId + " already has a queue");
        }

        VirtualQueueData virtualQueueData = (VirtualQueueData) companyRedisTemplate.opsForHash().get(companyId, role);
        if (virtualQueueData == null) {
            virtualQueueData = createRedisVirtualQueue();
        }
        virtualQueueData.getEmployeeIds().add(employeeId);
        companyRedisTemplate.opsForHash().put(companyId, role, virtualQueueData);

        String virtualQueueId = virtualQueueData.getVirtualQueueId();
        employee.setVirtualQueueId(virtualQueueId);
        employeeRedisTemplate.opsForHash().put(EMPLOYEE_CACHE_NAME, employeeId, employee);

        return virtualQueueId;
    }

    @Override
    public void pauseQueueForEmployee(String employeeId) {
        Employee employee = getEmployeeWithId(employeeId);
        if (isFalsy(employee.getVirtualQueueId())) {
            throw new InvalidRequestException("Employee with employeeId: " + employeeId + "is not associated" +
                    " with a virtual queue");
        }
        String companyId = employee.getCompanyId();
        Role role = employee.getRole();

        VirtualQueueData virtualQueueData = (VirtualQueueData) companyRedisTemplate.opsForHash().get(companyId, role);
        Set<String> employees = virtualQueueData.getEmployeeIds();
        employees.remove(employeeId);

        companyRedisTemplate.opsForHash().put(companyId, role,
                new VirtualQueueData(virtualQueueData.getVirtualQueueId(), employees));

        employee.setVirtualQueueId(null);
        employeeRedisTemplate.opsForHash().put(EMPLOYEE_CACHE_NAME, employeeId, employee);
    }

    @Override
    public EmployeeQueueData removeQueue() {
        // TODO
        return null;
    }

    @Override
    public Long size(String queueId) {
        // TODO: check if this gives a null pointer
        if (queueRedisTemplate.keys(queueId).size() == 0) {
            return -1L;
        }

        return queueRedisTemplate.opsForList().size(queueId);
    }

    /**
     * Checks if a given string is falsy
     * @param str the given string
     * @return true if the given string is falsy, false otherwise
     */
    private boolean isFalsy(String str) {
        return str == null || str.equals("");
    }

    /**
     * Creates and returns an employee to be stored in Redis
     *
     * @param employeeId id of the newly created employee
     * @return Employee
     */
    private Employee createRedisEmployee(String companyId, String employeeId, Role role) {
        return new Employee(employeeId, companyId, role);
    }

    /**
     * Creates and returns a representation of a company to be stored in Redis
     *
     * @return Company
     */
    private VirtualQueueData createRedisVirtualQueue() {
        return new VirtualQueueData(generateRandomId(), new HashSet<>());
    }

    /**
     * Creates and returns a snapshot of employee's queue
     *
     * @return EmployeeQueueData
     */
    private EmployeeQueueData createEmployeeQueueData() {
        return new EmployeeQueueData(new ArrayList<>(), 0, 0);
    }

}

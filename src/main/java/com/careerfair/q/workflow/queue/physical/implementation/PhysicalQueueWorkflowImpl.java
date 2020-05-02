package com.careerfair.q.workflow.queue.physical.implementation;

import com.careerfair.q.model.redis.Student;
import com.careerfair.q.util.enums.Role;
import com.careerfair.q.model.redis.Employee;
import com.careerfair.q.model.redis.VirtualQueue;
import com.careerfair.q.service.queue.response.EmployeeQueueData;
import com.careerfair.q.service.queue.response.QueueStatus;
import com.careerfair.q.util.exception.InvalidRequestException;
import com.careerfair.q.workflow.queue.physical.PhysicalQueueWorkflow;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.UUID;

@Component
public class PhysicalQueueWorkflowImpl implements PhysicalQueueWorkflow {

    @Autowired private RedisTemplate<String, Role> redisCompanyTemplate;
    @Autowired private RedisTemplate<String, String> redisEmployeeTemplate;
    @Autowired private RedisTemplate<String, Student> redisQueueTemplate;

    private static final String EMPLOYEE_CACHE_NAME = "employees";

    @Override
    public QueueStatus joinQueue(String companyId, String employeeId, String studentId, Role role) {
        // TODO
        return null;
    }

    @Override
    public QueueStatus leaveQueue(String companyId, String studentId, Role role) {
        // TODO
        return null;
    }

    @Override
    public EmployeeQueueData addQueue(String companyId, String employeeId, Role role) {
        if (redisEmployeeTemplate.opsForHash().hasKey(EMPLOYEE_CACHE_NAME, employeeId)) {
            // TODO: Add SL4J Logger instead of println
            System.out.println("EmployeeId: " + employeeId + " already has a queue.");
            throw new InvalidRequestException("Employee with employee id: " + employeeId + " already has a queue.");
        }

        redisEmployeeTemplate.opsForHash().put(EMPLOYEE_CACHE_NAME, employeeId, createRedisEmployee(employeeId));

        VirtualQueue virtualQueue = (VirtualQueue) redisCompanyTemplate.opsForHash().get(companyId, role);
        if (virtualQueue == null) {
            virtualQueue = createRedisVirtualQueue();
        }
        virtualQueue.getEmployeeIds().add(employeeId);
        redisCompanyTemplate.opsForHash().put(companyId, role, virtualQueue);
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
        // TODO
        return null;
    }

    @Override
    public EmployeeQueueData getEmployeeQueueData(String employeeId) {
        if (!redisEmployeeTemplate.opsForHash().hasKey(EMPLOYEE_CACHE_NAME, employeeId)) {
            throw new InvalidRequestException("No employee found with employee id: " + employeeId);
        }

        Employee employee = (Employee) redisEmployeeTemplate.opsForHash().get(EMPLOYEE_CACHE_NAME, employeeId);
        assert employee != null;

        int numRegisteredStudents = employee.getNumRegisteredStudents();
        double averageTimePerStudent = employee.getTotalTimeSpent() * 1. / Math.max(numRegisteredStudents, 1);
        List<Student> students = redisQueueTemplate.opsForList().range(employee.getPhysicalQueueId(), 0L, -1L);

        return new EmployeeQueueData(students, numRegisteredStudents, averageTimePerStudent);
    }

    private Employee createRedisEmployee(String employeeId) {
        return new Employee(employeeId, generateRandomId(), generateRandomId(), 0, 0);
    }

    private VirtualQueue createRedisVirtualQueue() {
        return new VirtualQueue(generateRandomId(), new HashSet<>());
    }

    private EmployeeQueueData createEmployeeQueueData() {
        return new EmployeeQueueData(new ArrayList<>(), 0, 0);
    }

    private String generateRandomId() {
        return UUID.randomUUID().toString();
    }
}

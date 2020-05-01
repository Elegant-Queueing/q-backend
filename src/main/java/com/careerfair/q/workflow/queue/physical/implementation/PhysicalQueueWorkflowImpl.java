package com.careerfair.q.workflow.queue.physical.implementation;

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
import java.util.UUID;

@Component
public class PhysicalQueueWorkflowImpl implements PhysicalQueueWorkflow {

    @Autowired private RedisTemplate<String, Role> redisVirtualQueueTemplate;
    @Autowired private RedisTemplate<String, String> redisEmployeeTemplate;

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
        if (redisEmployeeTemplate.opsForHash().hasKey("employees", employeeId)) {
            // TODO: Add SL4J Logger instead of println
            System.out.println("EmployeeId: " + employeeId + " already has a queue.");
            throw new InvalidRequestException();
        }

        redisEmployeeTemplate.opsForHash().put("employees", employeeId, createRedisEmployee(employeeId));

        VirtualQueue virtualQueue = (VirtualQueue) redisVirtualQueueTemplate.opsForHash().get(companyId, role);
        if (virtualQueue == null) {
            virtualQueue = createRedisVirtualQueue();
        }
        virtualQueue.getEmployeeIds().add(employeeId);
        redisVirtualQueueTemplate.opsForHash().put(companyId, role, virtualQueue);
        return createEmployeeQueueData();
    }

    private Employee createRedisEmployee(String employeeId) {
        return new Employee(employeeId, UUID.randomUUID(), UUID.randomUUID(), 0, 0);
    }

    private VirtualQueue createRedisVirtualQueue() {
        return new VirtualQueue(UUID.randomUUID(), new HashSet<>());
    }

    private EmployeeQueueData createEmployeeQueueData() {
        return new EmployeeQueueData(new ArrayList<>(), 0, 0);
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
        // TODO
        return null;
    }
}

package com.careerfair.q.workflow.queue.virtual.implementation;

import com.careerfair.q.model.redis.Employee;
import com.careerfair.q.model.redis.VirtualQueueData;
import com.careerfair.q.util.enums.Role;
import com.careerfair.q.service.queue.response.QueueStatus;
import com.careerfair.q.workflow.queue.AbstractQueueWorkflow;
import com.careerfair.q.workflow.queue.virtual.VirtualQueueWorkflow;
import org.springframework.stereotype.Component;

import java.util.HashSet;

@Component
public class VirtualQueueWorkflowImpl extends AbstractQueueWorkflow implements VirtualQueueWorkflow {

    @Override
    public QueueStatus joinQueue(String companyId, String studentId, Role role) {
        // TODO
        return null;
    }

    @Override
    public QueueStatus leaveQueue(String companyId, String studentId, Role role) {
        // TODO
        return null;
    }

    @Override
    public void addQueue() {

    }

    @Override
    public void removeQueue() {

    }

    @Override
    public Long size() {
        return null;
    }

    //@Override
    public QueueStatus removeFromQueue(String companyId, String studentId, Role role) {
        return null;
    }

    /**
     * Creates and returns an employee to be stored in Redis
     *
     * @param employeeId id of the newly created employee
     * @return Employee
     */
    private Employee createRedisEmployee(String companyId, String employeeId, Role role) {
        return new Employee(employeeId, companyId, role);//, generateRandomId(), generateRandomId());
    }

    /**
     * Creates and returns a representation of a company to be stored in Redis
     *
     * @return Company
     */
    private VirtualQueueData createRedisCompany() {
        return new VirtualQueueData(generateRandomId(), new HashSet<>());
    }
}

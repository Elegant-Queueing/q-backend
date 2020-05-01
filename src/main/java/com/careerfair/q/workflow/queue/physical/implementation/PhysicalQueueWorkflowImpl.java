package com.careerfair.q.workflow.queue.physical.implementation;

import com.careerfair.q.enums.Role;
import com.careerfair.q.service.queue.response.EmployeeQueueData;
import com.careerfair.q.service.queue.response.QueueStatus;
import com.careerfair.q.workflow.queue.physical.PhysicalQueueWorkflow;
import org.springframework.stereotype.Component;

@Component
public class PhysicalQueueWorkflowImpl implements PhysicalQueueWorkflow {

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
        // TODO
        return null;
    }

    @Override
    public EmployeeQueueData pauseQueue(String companyId, String employeeId) {
        // TODO
        return null;
    }

    @Override
    public EmployeeQueueData removeStudent(String employeeId, String studentId) {
        // TODO
        return null;
    }

    @Override
    public EmployeeQueueData skipStudent(String employeeId, String studentId) {
        // TODO
        return null;
    }

    @Override
    public EmployeeQueueData getEmployeeQueueData(String employeeId) {
        // TODO
        return null;
    }
}

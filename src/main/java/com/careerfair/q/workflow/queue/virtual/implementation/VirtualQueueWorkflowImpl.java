package com.careerfair.q.workflow.queue.virtual.implementation;

import com.careerfair.q.service.queue.response.EmployeeQueueData;
import com.careerfair.q.util.enums.Role;
import com.careerfair.q.service.queue.response.QueueStatus;
import com.careerfair.q.workflow.queue.virtual.VirtualQueueWorkflow;
import org.springframework.stereotype.Component;

@Component
public class VirtualQueueWorkflowImpl implements VirtualQueueWorkflow {

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
    public EmployeeQueueData addQueue(String companyId, String employeeId, Role role) {
        // TODO
        return null;
    }
}

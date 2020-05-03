package com.careerfair.q.workflow.queue.window.implementation;

import com.careerfair.q.util.enums.Role;
import com.careerfair.q.workflow.queue.window.WindowQueueWorkflow;

public class WindowQueueWorkflowImpl implements WindowQueueWorkflow {

    @Override
    public boolean addToQueue(String employeeId, String studentId, Role role) {
        return false;
    }

    @Override
    public boolean removeFromQueue(String employeeId, String studentId) {
        return false;
    }

    @Override
    public int size(String employeeId) {
        return 0;
    }
}

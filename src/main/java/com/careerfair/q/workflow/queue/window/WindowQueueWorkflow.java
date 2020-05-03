package com.careerfair.q.workflow.queue.window;

import com.careerfair.q.model.redis.Student;
import com.careerfair.q.service.queue.response.QueueStatus;
import com.careerfair.q.util.enums.Role;

public interface WindowQueueWorkflow {
    /**
     *
     * @param employeeId
     * @param studentId
     * @param role
     * @return
     */
    boolean addToQueue(String employeeId, String studentId, Role role);

    /**
     *
     * @param employeeId
     * @param studentId
     * @return
     */
    Student removeFromQueue(String employeeId, String studentId);

    /**
     *
     * @param employeeId
     * @return
     */
    Long size(String employeeId);
}

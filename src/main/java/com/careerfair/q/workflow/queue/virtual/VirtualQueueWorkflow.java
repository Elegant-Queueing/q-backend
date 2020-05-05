package com.careerfair.q.workflow.queue.virtual;

import com.careerfair.q.util.enums.Role;
import com.careerfair.q.service.queue.response.QueueStatus;

public interface VirtualQueueWorkflow {

    /**
     *
     * @param companyId
     * @param studentId
     * @param role
     * @return
     */
    QueueStatus joinQueue(String companyId, String studentId, Role role);

    /**
     *
     * @param companyId
     * @param studentId
     * @param role
     * @return
     */
    QueueStatus leaveQueue(String companyId, String studentId, Role role);

    void addQueue();

    void removeQueue();

    Long size();
}

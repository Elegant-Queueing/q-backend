package com.careerfair.q.workflow.queue.virtual;

import com.careerfair.q.service.queue.response.EmployeeQueueData;
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

    /**
     * Adds a virtual queue for the given companyId and role, and associates it with the
     * given employeeId
     * @param companyId id of the company the employee associated with
     * @param employeeId id of the employee
     * @param role role the employee is associated with
     * @return EmployeeQueueData current status of the given employee
     */
    EmployeeQueueData addQueue(String companyId, String employeeId, Role role);

    EmployeeQueueData removeQueue();

    Long size();
}

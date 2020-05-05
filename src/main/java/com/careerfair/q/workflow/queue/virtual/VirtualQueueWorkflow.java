package com.careerfair.q.workflow.queue.virtual;

import com.careerfair.q.service.queue.response.EmployeeQueueData;
import com.careerfair.q.util.enums.Role;
import com.careerfair.q.service.queue.response.QueueStatus;
import com.careerfair.q.util.exception.InvalidRequestException;

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
     * Adds a virtual queue for the given, existing companyId and role, and associates it with the
     * given employeeId
     *
     * @param companyId id of the company the employee is associated with
     * @param employeeId id of the employee whose queue is to be added
     * @param role role the employee is associated with
     * @throws InvalidRequestException if the given employeeId already has a virtual queue associated
     *         with it
     * @return id of the virtual queue that the given employeeId got associated with
     */
    String addQueue(String companyId, String employeeId, Role role);

    /**
     *
     * @param employeeId
     * @return
     */
    void pauseQueueForEmployee(String employeeId);

    EmployeeQueueData removeQueue();

    Long size();
}

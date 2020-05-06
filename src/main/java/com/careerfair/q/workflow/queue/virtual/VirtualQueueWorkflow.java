package com.careerfair.q.workflow.queue.virtual;

import com.careerfair.q.model.redis.StudentQueueStatus;
import com.careerfair.q.service.queue.response.EmployeeQueueData;
import com.careerfair.q.util.enums.Role;
import com.careerfair.q.service.queue.response.QueueStatus;
import com.careerfair.q.util.exception.InvalidRequestException;

public interface VirtualQueueWorkflow {

    /**
     * Adds the student with the given studentId to the virtual queue of the given company and role
     *
     * @param companyId id of the company whose virtual queue the student will be added to
     * @param studentId id of the student
     * @param role role whose virtual queue the student will be added to
     * @param studentName name of the student
     * @return StudentQueueStatus current status of the student
     * @throws InvalidRequestException if no employee is taking students for the given role and company
     */
    StudentQueueStatus joinQueue(String companyId, String studentId, Role role, String studentName);

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
     * Pauses the virtual queue for the employee with the given employeeId
     *
     * @param employeeId id of the employee whose
     * @throws InvalidRequestException if the employee with the given employeeId doesn't have a virtual queue
     *         associated with them
     */
    void pauseQueueForEmployee(String employeeId);

    EmployeeQueueData removeQueue();

    /**
     * Returns the size of the virtual queue with the given queueId
     *
     * @param queueId
     * @return size of the queue or -1 if no queue with the given queueId exists
     */
    Long size(String queueId);
}

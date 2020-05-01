package com.careerfair.q.workflow.queue.physical;

import com.careerfair.q.enums.Role;
import com.careerfair.q.service.queue.response.EmployeeQueueData;
import com.careerfair.q.service.queue.response.QueueStatus;

public interface PhysicalQueueWorkflow {

    /**
     *
     * @param companyId
     * @param employeeId
     * @param studentId
     * @param role
     * @return
     */
    QueueStatus joinQueue(String companyId, String employeeId, String studentId, Role role);

    /**
     *
     * @param companyId
     * @param studentId
     * @param role
     * @return
     */
    QueueStatus leaveQueue(String companyId, String studentId, Role role);

    /**
     *
     * @param companyId
     * @param employeeId
     * @param role
     * @return
     */
    EmployeeQueueData addQueue(String companyId, String employeeId, Role role);

    /**
     *
     * @param companyId
     * @param employeeId
     * @return
     */
    EmployeeQueueData pauseQueue(String companyId, String employeeId);

    /**
     *
     * @param employeeId
     * @param studentId
     * @return
     */
    EmployeeQueueData removeStudent(String employeeId, String studentId);

    /**
     *
     * @param employeeId
     * @param studentId
     * @return
     */
    EmployeeQueueData skipStudent(String employeeId, String studentId);

    /**
     *
     * @param employeeId
     * @return
     */
    EmployeeQueueData getEmployeeQueueData(String employeeId);
}

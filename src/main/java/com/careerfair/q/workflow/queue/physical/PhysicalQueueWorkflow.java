package com.careerfair.q.workflow.queue.physical;

import com.careerfair.q.util.enums.Role;
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
     * Add the given employee's queue associated with the given company and given role to the fair
     *
     * @param companyId id of the company associated with the employee
     * @param employeeId id of the employee whose queue is to be added
     * @param role role associated with the given employee
     * @return EmployeeQueueData
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
    EmployeeQueueData registerStudent(String employeeId, String studentId);

    /**
     *
     * @param employeeId
     * @param studentId
     * @return
     */
    EmployeeQueueData removeStudent(String employeeId, String studentId);

    /**
     * Returns the data for the given employee's queue
     *
     * @param employeeId id of employee whose queue's data is to be retrieved
     * @return EmployeeQueueData
     */
    EmployeeQueueData getEmployeeQueueData(String employeeId);
}

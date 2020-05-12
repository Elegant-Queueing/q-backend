package com.careerfair.q.controller.queue;

import com.careerfair.q.service.queue.response.*;
import com.careerfair.q.util.enums.Role;

public interface QueueController {

    /**
     *
     * @param companyId
     * @param role
     * @return
     */
    GetWaitTimeResponse getCompanyWaitTime(String companyId, Role role);

    /**
     *
     * @param role
     * @return
     */
    GetWaitTimeResponse getAllCompaniesWaitTime(Role role);

    /**
     *
     * @param companyId
     * @param studentId
     * @param role
     * @return
     */
    JoinQueueResponse joinQueue(String companyId, String studentId, Role role, String name);

    /**
     * Adds the given student to the given employee's queue
     *
     * @param employeeId id of the employee whose queue to join
     * @param studentId id of the student requesting to join
     * @return JoinQueueResponse
     */
    JoinQueueResponse joinEmployeeQueue(String employeeId, String studentId);

    /**
     *
     * @param companyId
     * @param studentId
     * @param role
     */
    void leaveQueue(String companyId, String studentId, Role role);

    /**
     *
     * @param studentId
     * @return
     */
    GetQueueStatusResponse getQueueStatus(String studentId);

    /**
     * Add the given employee's queue associated with the given company and given role to the fair
     *
     * @param companyId id of the company associated with the employee
     * @param employeeId id of the employee whose queue is to be added
     * @param role role associated with the given employee
     * @return AddQueueResponse
     */
    AddQueueResponse addQueue(String companyId, String employeeId, Role role);

    /**
     * Returns the data for the given employee's queue
     *
     * @param employeeId id of employee whose queue's data is to be retrieved
     * @return GetEmployeeQueueDataResponse
     */
    GetEmployeeQueueDataResponse getEmployeeQueueData(String employeeId);

    /**
     *
     * @param employeeId
     * @return
     */
    PauseQueueResponse pauseQueue(String employeeId);

    /**
     * Registers that the given student has completed their talk with the given employee
     *
     * @param employeeId id of the employee who the student has talked to
     * @param studentId id of the student
     * @return RemoveStudentResponse
     */
    RemoveStudentResponse registerStudent(String employeeId, String studentId);

    /**
     * Removes the given student from the given employee's queue
     *
     * @param employeeId id of the employee from whose queue the student is to be removed
     * @param studentId id of the student being removed
     * @return RemoveStudentResponse
     */
    RemoveStudentResponse removeStudent(String employeeId, String studentId);
}

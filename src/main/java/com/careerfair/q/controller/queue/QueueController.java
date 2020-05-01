package com.careerfair.q.controller.queue;

import com.careerfair.q.util.enums.Role;
import com.careerfair.q.service.queue.response.*;

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
    JoinQueueResponse joinQueue(String companyId, String studentId, Role role);

    /**
     *
     * @param companyId
     * @param employeeId
     * @param studentId
     * @param role
     * @return
     */
    JoinQueueResponse joinEmployeeQueue(String companyId, String employeeId, String studentId, Role role);

    /**
     *
     * @param companyId
     * @param studentId
     * @param role
     * @return
     */
    LeaveQueueResponse leaveQueue(String companyId, String studentId, Role role);

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
     *
     * @param employeeId
     * @return
     */
    GetEmployeeQueueDataResponse getEmployeeQueueData(String employeeId);

    /**
     *
     * @param companyId
     * @param employeeId
     * @return
     */
    PauseQueueResponse pauseQueue(String companyId, String employeeId);

    /**
     * Should be used when an employee is done talking to a student
     * @param employeeId
     * @param studentId
     * @return
     */
    RemoveStudentResponse registerStudent(String employeeId, String studentId);

    /**
     * Should be used when an employee wants to skip a student
     * @param employeeId
     * @param studentId
     * @return
     */
    RemoveStudentResponse removeStudent(String employeeId, String studentId);
}

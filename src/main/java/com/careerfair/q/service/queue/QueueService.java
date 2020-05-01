package com.careerfair.q.service.queue;

import com.careerfair.q.enums.Role;
import com.careerfair.q.service.queue.response.*;

public interface QueueService {

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
    JoinQueueResponse joinVirtualQueue(String companyId, String studentId, Role role);

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
    GetQueueStatusResponse getQueueStatusStatus(String studentId);

    /**
     *
     * @param companyId
     * @param employeeId
     * @param role
     * @return
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
     *
     * @param employeeId
     * @param studentId
     * @return
     */
    RemoveStudentResponse removeStudent(String employeeId, String studentId);

    /**
     *
     * @param employeeId
     * @param studentId
     * @return
     */
    RemoveStudentResponse skipStudent(String employeeId, String studentId);

}

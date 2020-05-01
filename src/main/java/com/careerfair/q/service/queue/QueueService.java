package com.careerfair.q.service.queue;

import com.careerfair.q.enums.Role;
import com.careerfair.q.service.queue.response.*;

public interface QueueService {

    GetWaitTimeResponse getCompanyWaitTime(String companyId, Role role);

    GetWaitTimeResponse getAllCompaniesWaitTime(Role role);

    JoinQueueResponse joinVirtualQueue(String companyId, String studentId, Role role);

    JoinQueueResponse joinEmployeeQueue(String companyId, String employeeId, String studentId, Role role);

    LeaveQueueResponse leaveQueue(String companyId, String studentId, Role role);

    GetQueueStatusResponse getQueueStatusStatus(String studentId);

    AddQueueResponse addQueue(String companyId, String employeeId, Role role);

    GetEmployeeQueueDataResponse getEmployeeQueueData(String employeeId);

    PauseQueueResponse pauseQueue(String companyId, String employeeId);

    RemoveStudentResponse removeStudent(String employeeId, String studentId);

    RemoveStudentResponse skipStudent(String employeeId, String studentId);

}

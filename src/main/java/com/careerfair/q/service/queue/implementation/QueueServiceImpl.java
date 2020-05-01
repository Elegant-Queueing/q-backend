package com.careerfair.q.service.queue.implementation;

import com.careerfair.q.enums.Role;
import com.careerfair.q.service.queue.QueueService;
import com.careerfair.q.service.queue.response.*;
import com.careerfair.q.workflow.queue.physical.PhysicalQueueWorkflow;
import com.careerfair.q.workflow.queue.virtual.VirtualQueueWorkflow;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class QueueServiceImpl implements QueueService {

    @Autowired private VirtualQueueWorkflow virtualQueueWorkflow;
    @Autowired private PhysicalQueueWorkflow physicalQueueWorkflow;

    @Override
    public GetWaitTimeResponse getCompanyWaitTime(String companyId, Role role) {
        return null;
    }

    @Override
    public GetWaitTimeResponse getAllCompaniesWaitTime(Role role) {
        return null;
    }

    @Override
    public JoinQueueResponse joinVirtualQueue(String companyId, String studentId, Role role) {
        return null;
    }

    @Override
    public JoinQueueResponse joinEmployeeQueue(String companyId, String employeeId, String studentId, Role role) {
        return null;
    }

    @Override
    public LeaveQueueResponse leaveQueue(String companyId, String studentId, Role role) {
        return null;
    }

    @Override
    public GetQueueStatusResponse getQueueStatusStatus(String studentId) {
        return null;
    }

    @Override
    public AddQueueResponse addQueue(String companyId, String employeeId, Role role) {
        return null;
    }

    @Override
    public GetEmployeeQueueDataResponse getEmployeeQueueData(String employeeId) {
        return null;
    }

    @Override
    public PauseQueueResponse pauseQueue(String companyId, String employeeId) {
        return null;
    }

    @Override
    public RemoveStudentResponse removeStudent(String employeeId, String studentId) {
        return null;
    }

    @Override
    public RemoveStudentResponse skipStudent(String employeeId, String studentId) {
        return null;
    }
}

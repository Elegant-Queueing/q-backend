package com.careerfair.q.service.queue.implementation;

import com.careerfair.q.util.enums.Role;
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
        // TODO
        return null;
    }

    @Override
    public GetWaitTimeResponse getAllCompaniesWaitTime(Role role) {
        // TODO
        return null;
    }

    @Override
    public JoinQueueResponse joinVirtualQueue(String companyId, String studentId, Role role) {
        // TODO
        return null;
    }

    @Override
    public JoinQueueResponse joinEmployeeQueue(String companyId, String employeeId, String studentId, Role role) {
        // TODO
        return null;
    }

    @Override
    public LeaveQueueResponse leaveQueue(String companyId, String studentId, Role role) {
        // TODO
        return null;
    }

    @Override
    public GetQueueStatusResponse getQueueStatus(String studentId) {
        // TODO
        return null;
    }

    @Override
    public AddQueueResponse addQueue(String companyId, String employeeId, Role role) {
        return new AddQueueResponse(physicalQueueWorkflow.addQueue(companyId, employeeId, role));
    }

    @Override
    public GetEmployeeQueueDataResponse getEmployeeQueueData(String employeeId) {
        // TODO
        return null;
    }

    @Override
    public PauseQueueResponse pauseQueue(String companyId, String employeeId) {
        // TODO
        return null;
    }

    @Override
    public RemoveStudentResponse registerStudent(String employeeId, String studentId) {
        // TODO
        return null;
    }

    @Override
    public RemoveStudentResponse removeStudent(String employeeId, String studentId) {
        // TODO
        return null;
    }
}

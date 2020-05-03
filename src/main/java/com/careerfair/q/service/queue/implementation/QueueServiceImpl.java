package com.careerfair.q.service.queue.implementation;

import com.careerfair.q.model.redis.Student;
import com.careerfair.q.service.queue.QueueService;
import com.careerfair.q.service.queue.response.*;
import com.careerfair.q.util.enums.Role;
import com.careerfair.q.workflow.queue.physical.PhysicalQueueWorkflow;
import com.careerfair.q.workflow.queue.virtual.VirtualQueueWorkflow;
import com.careerfair.q.workflow.queue.window.WindowQueueWorkflow;
import com.google.cloud.Timestamp;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class QueueServiceImpl implements QueueService {

    public static final String EMPLOYEE_CACHE_NAME = "employees";
    public static final int WINDOW = 300;  // in seconds

    @Autowired private VirtualQueueWorkflow virtualQueueWorkflow;
    @Autowired private WindowQueueWorkflow windowQueueWorkflow;
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
    public JoinQueueResponse joinEmployeeQueue(String employeeId, String studentId) {
        Student student = new Student(studentId, "test", Timestamp.now());
        student.setJoinedWindowQueueAt(Timestamp.now());
                // windowQueueWorkflow.removeFromQueue(employeeId, studentId);
        return new JoinQueueResponse(physicalQueueWorkflow.joinQueue(employeeId, student));
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
        return new GetEmployeeQueueDataResponse(physicalQueueWorkflow.getEmployeeQueueData(
                employeeId));
    }

    @Override
    public PauseQueueResponse pauseQueue(String employeeId) {
        // TODO
        return null;
    }

    @Override
    public RemoveStudentResponse registerStudent(String employeeId, String studentId) {
        return new RemoveStudentResponse(physicalQueueWorkflow.registerStudent(employeeId,
                studentId));
    }

    @Override
    public RemoveStudentResponse removeStudent(String employeeId, String studentId) {
        return new RemoveStudentResponse(physicalQueueWorkflow.removeStudent(employeeId,
                studentId));
    }
}

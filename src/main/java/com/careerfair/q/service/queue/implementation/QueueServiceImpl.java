package com.careerfair.q.service.queue.implementation;

import com.careerfair.q.model.redis.Company;
import com.careerfair.q.model.redis.Student;
import com.careerfair.q.util.enums.Role;
import com.careerfair.q.service.queue.QueueService;
import com.careerfair.q.service.queue.response.*;
import com.careerfair.q.util.exception.InvalidRequestException;
import com.careerfair.q.workflow.queue.physical.PhysicalQueueWorkflow;
import com.careerfair.q.workflow.queue.virtual.VirtualQueueWorkflow;
import com.careerfair.q.workflow.queue.window.WindowQueueWorkflow;
import com.google.cloud.Timestamp;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

@Service
public class QueueServiceImpl implements QueueService {

    public static final String EMPLOYEE_CACHE_NAME = "employees";
    public static final int WINDOW = 300;  // in seconds

    @Autowired private VirtualQueueWorkflow virtualQueueWorkflow;
    @Autowired private WindowQueueWorkflow windowQueueWorkflow;
    @Autowired private PhysicalQueueWorkflow physicalQueueWorkflow;

    @Autowired private RedisTemplate<String, Role> companyRedisTemplate;

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
    public JoinQueueResponse joinEmployeeQueue(String companyId, String employeeId,
                                               String studentId, Role role) {
        checkEmployeeHasQueueOpen(companyId, employeeId, role);
        Student student = new Student(studentId, "test", Timestamp.now());
        student.setJoinedWindowQueueAt(Timestamp.now());
                // windowQueueWorkflow.removeFromQueue(employeeId, studentId);
        return new JoinQueueResponse(physicalQueueWorkflow.joinQueue(employeeId, role, student));
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
    public PauseQueueResponse pauseQueue(String companyId, String employeeId) {
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

    /**
     * Checks if the given employee is associated with the given company for the given role
     *
     * @param companyId id of the company associated with the employee
     * @param employeeId id of the employee
     * @param role role for which the employee is recruiting
     * @throws InvalidRequestException throws the exception if the company is not associated with
     *      employee for the given role
     */
    private void checkEmployeeHasQueueOpen(String companyId, String employeeId, Role role)
            throws InvalidRequestException {
        Company company = (Company) companyRedisTemplate.opsForHash().get(companyId, role);
        if (company == null || !company.getEmployeeIds().contains(employeeId)) {
            throw new InvalidRequestException("No company with company id=" + companyId +
                    " is associated with employee with employee id=" + employeeId +
                    " for role=" + role);
        }
    }
}

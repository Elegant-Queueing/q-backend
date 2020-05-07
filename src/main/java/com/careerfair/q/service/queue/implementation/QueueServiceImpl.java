package com.careerfair.q.service.queue.implementation;

import com.careerfair.q.model.redis.Employee;
import com.careerfair.q.model.redis.Student;
import com.careerfair.q.model.redis.StudentQueueStatus;
import com.careerfair.q.model.redis.VirtualQueueData;
import com.careerfair.q.service.queue.QueueService;
import com.careerfair.q.service.queue.response.*;
import com.careerfair.q.util.enums.QueueType;
import com.careerfair.q.util.enums.Role;
import com.careerfair.q.util.exception.InvalidRequestException;
import com.careerfair.q.workflow.queue.employee.physical.PhysicalQueueWorkflow;
import com.careerfair.q.workflow.queue.virtual.VirtualQueueWorkflow;
import com.careerfair.q.workflow.queue.employee.window.WindowQueueWorkflow;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.Set;

@Service
public class QueueServiceImpl implements QueueService {

    public static final String EMPLOYEE_CACHE_NAME = "employees";
    public static final String STUDENT_CACHE_NAME = "students";
    public static final int BUFFER = 10;  // in seconds
    public static final int WINDOW = 300;  // in seconds
    public static final long MAX_EMPLOYEE_QUEUE_SIZE = 5;

    @Autowired private VirtualQueueWorkflow virtualQueueWorkflow;
    @Autowired private WindowQueueWorkflow windowQueueWorkflow;
    @Autowired private PhysicalQueueWorkflow physicalQueueWorkflow;

//    @Autowired private RedisTemplate<String, Role> companyRedisTemplate;
    @Autowired private RedisTemplate<String, String> employeeRedisTemplate;
//    @Autowired private RedisTemplate<String, Student> queueRedisTemplate;
    @Autowired private RedisTemplate<String, String> studentRedisTemplate;

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
    public JoinQueueResponse joinVirtualQueue(String companyId, Role role, Student student) {
        QueueStatus status = virtualQueueWorkflow.joinQueue(companyId, role, student);

        String employeeId = getEmployeeWithMostQueueSpace(companyId, role);
        if (employeeId != null) {
            status = shiftStudentToWindow(companyId, employeeId, role, student);
        }

        return new JoinQueueResponse(status);
    }

    @Override
    public JoinQueueResponse joinEmployeeQueue(String employeeId, String studentId) {
        StudentQueueStatus studentQueueStatus = windowQueueWorkflow.leaveQueue(employeeId,
                studentId);
        Student student = new Student(studentId, studentQueueStatus.getName());
        QueueStatus queueStatus = physicalQueueWorkflow.joinQueue(employeeId, student,
                studentQueueStatus);
        return new JoinQueueResponse(queueStatus);
    }

    @Override
    public void leaveQueue(String companyId, String studentId, Role role) {
        StudentQueueStatus studentQueueStatus= getStudentQueueStatus(studentId);
        QueueType queueType = studentQueueStatus.getQueueType();

        switch (queueType) {

            case VIRTUAL:
                virtualQueueWorkflow.leaveQueue(companyId, studentId, role);
                break;

            case WINDOW:
                windowQueueWorkflow.leaveQueue(studentQueueStatus.getEmployeeId(), studentId);
                // TODO: transfer the top student in virtual queue associated with this company,
                // role to window queue
                //
                // if there is a student in the virtual queue, transfer that to window queue of
                // this employee
                break;

            case PHYSICAL:
                physicalQueueWorkflow.leaveQueue(studentQueueStatus.getEmployeeId(), studentId);
                // TODO: transfer the top student in virtual queue associated with this company,
                // role to window queue
                //
                // if there is a student in the virtual queue, transfer that to window queue of
                // this employee
                break;

            default:
                throw new InvalidRequestException("No such QueueType exists");
        }
    }

    @Override
    public GetQueueStatusResponse getQueueStatus(String studentId) {
        StudentQueueStatus studentQueueStatus= getStudentQueueStatus(studentId);
        QueueType queueType = studentQueueStatus.getQueueType();
        QueueStatus queueStatus;

        switch (queueType) {

            case VIRTUAL:
                queueStatus = virtualQueueWorkflow.getQueueStatus(studentQueueStatus);
                break;

            case WINDOW:
                queueStatus = windowQueueWorkflow.getQueueStatus(studentQueueStatus);
                break;

            case PHYSICAL:
                queueStatus = physicalQueueWorkflow.getQueueStatus(studentQueueStatus);
                break;

            default:
                throw new InvalidRequestException("No such QueueType exists");
        }

        return new GetQueueStatusResponse(queueStatus);
    }

    @Override
    public AddQueueResponse addQueue(String companyId, String employeeId, Role role) {
        Employee employee = (Employee) employeeRedisTemplate.opsForHash()
                .get(EMPLOYEE_CACHE_NAME, employeeId);
        if (employee == null) {
            employee = new Employee(employeeId, companyId, role);
        }

        virtualQueueWorkflow.addQueue(companyId, employeeId, role);

        if (employee.getWindowQueueId() == null) {
            windowQueueWorkflow.addQueue(employeeId);
        }
        if (employee.getPhysicalQueueId() == null) {
            physicalQueueWorkflow.addQueue(employeeId);
        }

        // TODO: start moving students to window queue from virtual if possible
        // transfer as many students as possible from virtual queue to window queue of THIS employee

        EmployeeQueueData employeeQueueData = physicalQueueWorkflow.getEmployeeQueueData(
                employeeId);
        return new AddQueueResponse(employeeQueueData);
    }

    @Override
    public GetEmployeeQueueDataResponse getEmployeeQueueData(String employeeId) {
        EmployeeQueueData employeeQueueData = physicalQueueWorkflow.getEmployeeQueueData(
                employeeId);
        return new GetEmployeeQueueDataResponse(employeeQueueData);
    }

    @Override
    public PauseQueueResponse pauseQueue(String employeeId) {
        virtualQueueWorkflow.pauseQueueForEmployee(employeeId);
        EmployeeQueueData employeeQueueData = physicalQueueWorkflow.getEmployeeQueueData(
                employeeId);
        return new PauseQueueResponse(employeeQueueData);
    }

    @Override
    public RemoveStudentResponse registerStudent(String employeeId, String studentId) {
        EmployeeQueueData employeeQueueData = physicalQueueWorkflow.registerStudent(employeeId,
                studentId);
        // TODO: move a student to window queue from virtual queue
        // // if there is a student in the virtual queue, transfer that to window queue of
        //                // this employee
        return new RemoveStudentResponse(employeeQueueData);
    }

    @Override
    public RemoveStudentResponse skipStudent(String employeeId, String studentId) {
        EmployeeQueueData employeeQueueData = physicalQueueWorkflow.skipStudent(employeeId,
                studentId);
        // TODO: move a student to window queue from virtual queue
        // // if there is a student in the virtual queue, transfer that to window queue of
        //                // this employee
        return new RemoveStudentResponse(employeeQueueData);
    }

    /**
     * Get the status of the student in a queue
     *
     * @param studentId id of the student
     * @return StudentQueueStatus
     * @throws InvalidRequestException if student is not present in any queue
     */
    private StudentQueueStatus getStudentQueueStatus(String studentId) {
        StudentQueueStatus studentQueueStatus = (StudentQueueStatus) studentRedisTemplate
                .opsForHash().get(STUDENT_CACHE_NAME, studentId);

        if (studentQueueStatus == null) {
            throw new InvalidRequestException("Student with id=" + studentId +
                    " not present in any queue");
        }

        return studentQueueStatus;
    }

    /**
     * Returns the employee with the most queue space available. Employee's current queue size is
     * equal to the sum of the employee's window and physical queue and must not exceed by
     * MAX_EMPLOYEE_QUEUE_SIZE
     *
     * @param companyId id of the company the employee is associated with
     * @param role role the employee is associated with
     * @return id of the employee with the most queue space available or null if no employee has
     *         queue space available
     */
    private String getEmployeeWithMostQueueSpace(String companyId, Role role) {
        VirtualQueueData virtualQueueData = virtualQueueWorkflow.getVirtualQueueData(companyId,
                role);
        Set<String> employeeIds = virtualQueueData.getEmployeeIds();
        String employeeWithMostSpaceId = null;
        long maxSpaceAvailable = 0;

        for (String employeeId: employeeIds) {
            long spaceAvailable = MAX_EMPLOYEE_QUEUE_SIZE - physicalQueueWorkflow.size(employeeId)
                    - windowQueueWorkflow.size(employeeId);

            if (spaceAvailable > maxSpaceAvailable) {
                maxSpaceAvailable = spaceAvailable;
                employeeWithMostSpaceId = employeeId;
            }
        }

        return employeeWithMostSpaceId;
    }

    /**
     *
     * @param companyId
     * @param employeeId
     * @param role
     * @param student
     * @return
     */
    private QueueStatus shiftStudentToWindow(String companyId, String employeeId, Role role,
                                             Student student) {
        StudentQueueStatus studentQueueStatus = virtualQueueWorkflow.leaveQueue(companyId,
                student.getId(), role);
        return windowQueueWorkflow.joinQueue(employeeId, student, studentQueueStatus);
    }
}

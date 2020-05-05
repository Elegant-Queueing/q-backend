package com.careerfair.q.workflow.queue.virtual.implementation;

import com.careerfair.q.model.redis.Employee;
import com.careerfair.q.model.redis.VirtualQueueData;
import com.careerfair.q.service.queue.response.EmployeeQueueData;
import com.careerfair.q.util.enums.Role;
import com.careerfair.q.service.queue.response.QueueStatus;
import com.careerfair.q.util.exception.InvalidRequestException;
import com.careerfair.q.workflow.queue.AbstractQueueWorkflow;
import com.careerfair.q.workflow.queue.virtual.VirtualQueueWorkflow;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

import static com.careerfair.q.service.queue.implementation.QueueServiceImpl.EMPLOYEE_CACHE_NAME;

@Component
public class VirtualQueueWorkflowImpl extends AbstractQueueWorkflow
        implements VirtualQueueWorkflow {

    @Autowired private RedisTemplate<String, Role> companyRedisTemplate;
    @Autowired private RedisTemplate<String, String> employeeRedisTemplate;
//    @Autowired private RedisTemplate<String, Student> queueRedisTemplate;
//    @Autowired private RedisTemplate<String, String> studentRedisTemplate;

    @Override
    public QueueStatus joinQueue(String companyId, String studentId, Role role) {
        // TODO
        return null;
    }

    @Override
    public QueueStatus leaveQueue(String companyId, String studentId, Role role) {
        // TODO
        return null;
    }

    @Override
    public String addQueue(String companyId, String employeeId, Role role) {
        checkEmployeeHasVirtualQueue(employeeId, "employeeId: " + employeeId + " already " +
                "has a virtual queue assocaited with it");

        VirtualQueueData virtualQueueData = (VirtualQueueData) companyRedisTemplate.opsForHash().get(companyId, role);
        if (virtualQueueData == null) {
            virtualQueueData = createRedisVirtualQueue();
        }
        virtualQueueData.getEmployeeIds().add(employeeId);
        companyRedisTemplate.opsForHash().put(companyId, role, virtualQueueData);
        return virtualQueueData.getVirtualQueueId();
    }

    @Override
    public void pauseQueueForEmployee(String employeeId) {
        checkEmployeeHasVirtualQueue(employeeId, "employeeId: " + employeeId + " is not " +
                " associated with a virtual queue");

        Employee employee = (Employee) employeeRedisTemplate.opsForHash().get(EMPLOYEE_CACHE_NAME, employeeId);
        if (employee == null) {
            throw new IllegalStateException("No existing employee object for employeeId: " + employeeId);
        }
        String companyId = employee.getCompanyId();
        Role role = employee.getRole();

        VirtualQueueData virtualQueueData = (VirtualQueueData) companyRedisTemplate.opsForHash().get(companyId, role);
        virtualQueueData.getEmployeeIds().remove(employeeId);
    }

    @Override
    public EmployeeQueueData removeQueue() {
        // TODO
        return null;
    }

    @Override
    public Long size() {
        // TODO
        return null;
    }

    /**
     * Creates and returns an employee to be stored in Redis
     *
     * @param employeeId id of the newly created employee
     * @return Employee
     */
    private Employee createRedisEmployee(String companyId, String employeeId, Role role) {
        return new Employee(employeeId, companyId, role);
    }

    /**
     * Creates and returns a representation of a company to be stored in Redis
     *
     * @return Company
     */
    private VirtualQueueData createRedisVirtualQueue() {
        return new VirtualQueueData(generateRandomId(), new HashSet<>());
    }

    /**
     * Creates and returns a snapshot of employee's queue
     *
     * @return EmployeeQueueData
     */
    private EmployeeQueueData createEmployeeQueueData() {
        return new EmployeeQueueData(new ArrayList<>(), 0, 0);
    }

    /**
     * Checks if the given employeeId has a virtual queue associated with it
     * @param employeeId id of the employee
     * @param message message to be shown with the exception
     * @throws InvalidRequestException if the given employeeId has a virtual queue associated with it
     */
    private void checkEmployeeHasVirtualQueue(String employeeId, String message) {
        if (employeeRedisTemplate.opsForHash().hasKey(EMPLOYEE_CACHE_NAME, employeeId)) {
            throw new InvalidRequestException(message);
        }
    }
}

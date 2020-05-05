package com.careerfair.q.workflow.queue.virtual.implementation;

import com.careerfair.q.model.redis.Employee;
import com.careerfair.q.model.redis.Student;
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
    public EmployeeQueueData addQueue(String companyId, String employeeId, Role role) {
        checkEmployeeHasVirtualQueue(employeeId);

        VirtualQueueData virtualQueueData = (VirtualQueueData) companyRedisTemplate.opsForHash().get(companyId, role);
        if (virtualQueueData == null) {
            virtualQueueData = createRedisVirtualQueue();
        }
        virtualQueueData.getEmployeeIds().add(employeeId);
        companyRedisTemplate.opsForHash().put(companyId, role, virtualQueueData);

        return createEmployeeQueueData();
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
     * @param employeeId
     * @throws InvalidRequestException if the given employeeId has a virtual queue associated with it
     */
    private void checkEmployeeHasVirtualQueue(String employeeId) {
        if (employeeRedisTemplate.opsForHash().hasKey(EMPLOYEE_CACHE_NAME, employeeId)) {
            throw new InvalidRequestException("Employee with employee id=" + employeeId +
                    " already has a queue");
        }
    }
}

package com.careerfair.q.workflow.queue.virtual.implementation;

import com.careerfair.q.model.redis.Employee;
import com.careerfair.q.model.redis.Student;
import com.careerfair.q.model.redis.StudentQueueStatus;
import com.careerfair.q.model.redis.VirtualQueueData;
import com.careerfair.q.service.queue.response.EmployeeQueueData;
import com.careerfair.q.util.enums.QueueType;
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
import static com.careerfair.q.service.queue.implementation.QueueServiceImpl.STUDENT_CACHE_NAME;

@Component
public class VirtualQueueWorkflowImpl extends AbstractQueueWorkflow
        implements VirtualQueueWorkflow {

    @Autowired private RedisTemplate<String, Role> companyRedisTemplate;
    @Autowired private RedisTemplate<String, String> employeeRedisTemplate;
    @Autowired private RedisTemplate<String, Student> queueRedisTemplate;
    @Autowired private RedisTemplate<String, String> studentRedisTemplate;

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
        Employee employee = getEmployeeWithId(employeeId);
        if(!isFalsy(employee.getVirtualQueueId())) {
            throw new InvalidRequestException("Employee with id: " + employeeId + " already has a queue");
        }

        VirtualQueueData virtualQueueData = (VirtualQueueData) companyRedisTemplate.opsForHash().get(companyId, role);
        if (virtualQueueData == null) {
            virtualQueueData = createRedisVirtualQueue();
        }
        virtualQueueData.getEmployeeIds().add(employeeId);
        companyRedisTemplate.opsForHash().put(companyId, role, virtualQueueData);

        String virtualQueueId = virtualQueueData.getVirtualQueueId();
        employee.setVirtualQueueId(virtualQueueId);
        employeeRedisTemplate.opsForHash().put(EMPLOYEE_CACHE_NAME, employeeId, employee);

        return virtualQueueId;
    }

    @Override
    public void pauseQueueForEmployee(String employeeId) {
        Employee employee = getEmployeeWithId(employeeId);
        if (isFalsy(employee.getVirtualQueueId())) {
            throw new InvalidRequestException("Employee with employeeId: " + employeeId + "is not associated" +
                    " with a virtual queue");
        }
        String companyId = employee.getCompanyId();
        Role role = employee.getRole();

        VirtualQueueData virtualQueueData = (VirtualQueueData) companyRedisTemplate.opsForHash().get(companyId, role);
        Set<String> employees = virtualQueueData.getEmployeeIds();
        employees.remove(employeeId);

        companyRedisTemplate.opsForHash().put(companyId, role,
                new VirtualQueueData(virtualQueueData.getVirtualQueueId(), employees));

        employee.setVirtualQueueId(null);
        employeeRedisTemplate.opsForHash().put(EMPLOYEE_CACHE_NAME, employeeId, employee);
    }

    @Override
    public EmployeeQueueData removeQueue() {
        // TODO
        return null;
    }

    @Override
    public Long size(String queueId) {
        if (queueRedisTemplate.keys(queueId).size() == 0) {
            return -1L;
        }

        return queueRedisTemplate.opsForList().size(queueId);
    }

    private boolean isFalsy(String id) {
        return id == null || id.equals("");
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

}

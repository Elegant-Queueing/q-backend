package com.careerfair.q.workflow.queue.virtual.implementation;

import com.careerfair.q.model.redis.Employee;
import com.careerfair.q.model.redis.Student;
import com.careerfair.q.model.redis.StudentQueueStatus;
import com.careerfair.q.model.redis.VirtualQueueData;
import com.careerfair.q.util.enums.QueueType;
import com.careerfair.q.util.enums.Role;
import com.careerfair.q.util.exception.InvalidRequestException;
import com.careerfair.q.workflow.queue.AbstractQueueWorkflow;
import com.careerfair.q.workflow.queue.virtual.VirtualQueueWorkflow;
import com.google.cloud.Timestamp;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

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
    public StudentQueueStatus joinQueue(String companyId, String studentId, Role role,
                                        String studentName) {
        // virtual queue should not be responsible for creating student objects
        StudentQueueStatus studentQueueStatus = getStudentQueueStatus(studentId);
        if(studentQueueStatus.getQueueType() != QueueType.NONE) {
            throw new InvalidRequestException("Student with id=" + studentId
                    + " already in a queue");
        }

        VirtualQueueData virtualQueueData = (VirtualQueueData) companyRedisTemplate.opsForHash()
                .get(companyId, role);
        if (virtualQueueData == null || virtualQueueData.getEmployeeIds().isEmpty()) {
            throw new InvalidRequestException("No employee with companyId=" + companyId
                    + " is currently taking students for role=" + role);
        }

        String virtualQueueId = virtualQueueData.getVirtualQueueId();
        queueRedisTemplate.opsForList().rightPush(virtualQueueId,
                new Student(studentId, studentName));

        studentQueueStatus.setCompanyId(companyId);
        studentQueueStatus.setRole(role);
        studentQueueStatus.setQueueId(virtualQueueId);
        studentQueueStatus.setQueueType(QueueType.VIRTUAL);
        studentQueueStatus.setJoinedVirtualQueueAt(Timestamp.now());
        studentRedisTemplate.opsForHash().put(STUDENT_CACHE_NAME, studentId, studentQueueStatus);
        return studentQueueStatus;
    }

    @Override
    public StudentQueueStatus leaveQueue(String companyId, String studentId, Role role) {
        StudentQueueStatus studentQueueStatus = getStudentQueueStatus(studentId);
        if(studentQueueStatus.getQueueType() != QueueType.VIRTUAL) {
            throw new InvalidRequestException("Student with id=" + studentId +
                    " is not present in any virtual queue");
        }

        VirtualQueueData virtualQueueData = (VirtualQueueData) companyRedisTemplate.opsForHash()
                .get(companyId, role);
        if (virtualQueueData == null || virtualQueueData.getEmployeeIds().isEmpty()) {
            throw new InvalidRequestException("No virtual queue present for companyId="
                    + companyId + " and role= " + role);
        }

        String virtualQueueId = studentQueueStatus.getQueueId();
        assert virtualQueueId.equals(virtualQueueData.getVirtualQueueId());

        List<Student> studentsInQueue = queueRedisTemplate.opsForList()
                .range(virtualQueueId, 0L, -1L);
        assert studentsInQueue != null;
        Student student = null;
        for(Student s : studentsInQueue) {
            if(s.getId().equals(studentId)) {
                student = s;
                break;
            }
        }
        assert student != null;

        queueRedisTemplate.opsForList().remove(virtualQueueId, 0L, student);
        studentRedisTemplate.opsForHash().delete(STUDENT_CACHE_NAME, studentId);
        return studentQueueStatus;
    }

    @Override
    public String addQueue(String companyId, String employeeId, Role role) {
        Employee employee = getEmployeeWithId(employeeId);
        if(!isFalsy(employee.getVirtualQueueId())) {
            throw new InvalidRequestException("Employee with id=" + employeeId +
                    " already has a queue");
        }

        VirtualQueueData virtualQueueData = (VirtualQueueData) companyRedisTemplate.opsForHash()
                .get(companyId, role);
        if (virtualQueueData == null) {
            virtualQueueData = createRedisVirtualQueue();
        }
        virtualQueueData.getEmployeeIds().add(employeeId);
        companyRedisTemplate.opsForHash().put(companyId, role, virtualQueueData);

        employee.setVirtualQueueId(virtualQueueData.getVirtualQueueId());
        employeeRedisTemplate.opsForHash().put(EMPLOYEE_CACHE_NAME, employeeId, employee);

        return virtualQueueData.getVirtualQueueId();
    }

    @Override
    public void pauseQueueForEmployee(String employeeId) {
        Employee employee = getEmployeeWithId(employeeId);
        if (isFalsy(employee.getVirtualQueueId())) {
            throw new InvalidRequestException("Employee with employeeId=" + employeeId
                    + "is not associated with a virtual queue");
        }
        String companyId = employee.getCompanyId();
        Role role = employee.getRole();

        VirtualQueueData virtualQueueData = (VirtualQueueData) companyRedisTemplate.opsForHash()
                .get(companyId, role);
        Set<String> employees = virtualQueueData.getEmployeeIds();
        employees.remove(employeeId);

        companyRedisTemplate.opsForHash().put(companyId, role,
                new VirtualQueueData(virtualQueueData.getVirtualQueueId(), employees));

        employee.setVirtualQueueId(null);
        employeeRedisTemplate.opsForHash().put(EMPLOYEE_CACHE_NAME, employeeId, employee);
    }

    @Override
    public void removeQueue(String companyId, Role role) {
        VirtualQueueData virtualQueueData = (VirtualQueueData) companyRedisTemplate.opsForHash()
                .get(companyId, role);

        Set<String> employeeIds = virtualQueueData.getEmployeeIds();
        for(String id: employeeIds) {
            Employee employee = (Employee) employeeRedisTemplate.opsForHash()
                    .get(EMPLOYEE_CACHE_NAME, id);
            assert employee != null;
            employee.setVirtualQueueId(null);
            employeeRedisTemplate.opsForHash().put(EMPLOYEE_CACHE_NAME, id, employee);
        }

        String virtualQueueId = virtualQueueData.getVirtualQueueId();
        List<String> studentIds = queueRedisTemplate.opsForList()
                .range(virtualQueueId, 0L, -1L)
                .stream()
                .map(Student::getId)
                .collect(Collectors.toList());
        for(String id: studentIds) {
            studentRedisTemplate.opsForHash().delete(STUDENT_CACHE_NAME, id);
        }

        queueRedisTemplate.delete(virtualQueueId);
        companyRedisTemplate.opsForHash().delete(companyId, role);
    }

    @Override
    public Long size(String queueId) {
        // TODO: check if this gives a null pointer
        if (queueRedisTemplate.keys(queueId).isEmpty()) {
            return -1L;
        }

        return queueRedisTemplate.opsForList().size(queueId);
    }

    /**
     * Checks if a given string is falsy
     * @param str the given string
     * @return true if the given string is falsy, false otherwise
     */
    private boolean isFalsy(String str) {
        return str == null || str.equals("");
    }

    /**
     * Creates and returns a representation of a company to be stored in Redis
     *
     * @return Company
     */
    private VirtualQueueData createRedisVirtualQueue() {
        return new VirtualQueueData(generateRandomId(), new HashSet<>());
    }
}

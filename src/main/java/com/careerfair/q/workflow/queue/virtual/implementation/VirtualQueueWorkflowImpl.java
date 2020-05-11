package com.careerfair.q.workflow.queue.virtual.implementation;

import com.careerfair.q.model.redis.Employee;
import com.careerfair.q.model.redis.Student;
import com.careerfair.q.model.redis.StudentQueueStatus;
import com.careerfair.q.model.redis.VirtualQueueData;
import com.careerfair.q.service.queue.response.QueueStatus;
import com.careerfair.q.util.enums.QueueType;
import com.careerfair.q.util.enums.Role;
import com.careerfair.q.util.exception.InvalidRequestException;
import com.careerfair.q.workflow.queue.AbstractQueueWorkflow;
import com.careerfair.q.workflow.queue.virtual.VirtualQueueWorkflow;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.util.HashSet;
import java.util.List;
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
    public QueueStatus joinQueue(String companyId, Role role, Student student) {
        String studentId = student.getId();
        if (studentRedisTemplate.opsForHash().hasKey(STUDENT_CACHE_NAME, studentId)) {
            throw new InvalidRequestException("Student with id=" + studentId + " already present " +
                    "in a queue");
        }
        StudentQueueStatus studentQueueStatus = new StudentQueueStatus(student.getName(), companyId,
                studentId, role);

        VirtualQueueData virtualQueueData = getVirtualQueueData(companyId, role);
        if (virtualQueueData.getEmployeeIds().isEmpty()) {
            throw new InvalidRequestException("No employee associated with virtual " +
                    "queue for companyId=" + companyId + " and role=" + role);
        }

        String virtualQueueId = virtualQueueData.getVirtualQueueId();
        queueRedisTemplate.opsForList().rightPush(virtualQueueId, student);

        studentQueueStatus.setQueueId(virtualQueueId);
        studentQueueStatus.setQueueType(QueueType.VIRTUAL);
        studentRedisTemplate.opsForHash().put(STUDENT_CACHE_NAME, studentId, studentQueueStatus);
        return createQueueStatus(studentQueueStatus, virtualQueueData);
    }

    @Override
    public StudentQueueStatus leaveQueue(String companyId, String studentId, Role role) {
        StudentQueueStatus studentQueueStatus = getStudentQueueStatus(studentId);
        if (studentQueueStatus.getQueueType() != QueueType.VIRTUAL) {
            throw new InvalidRequestException("Student with id=" + studentId +
                    " is not present in any virtual queue");
        }

        VirtualQueueData virtualQueueData = getVirtualQueueData(companyId, role);
        if (virtualQueueData.getEmployeeIds().isEmpty()) {
            throw new InvalidRequestException("No employee associated with virtual " +
                    "queue for companyId=" + companyId + " and role= " + role);
        }

        String virtualQueueId = studentQueueStatus.getQueueId();
        if (!virtualQueueId.equals(virtualQueueData.getVirtualQueueId())) {
            throw new InvalidRequestException("Student virtualQueueId and (Company,Role) mismatch");
        }

        List<Student> studentsInQueue = queueRedisTemplate.opsForList()
                .range(virtualQueueId, 0L, -1L);
        assert studentsInQueue != null;
        int index = getStudentPosition(studentId, studentsInQueue);
        if (index == -1) {
            throw new InvalidRequestException("Student with student id=" + studentId +
                    " is not present in the associated virtual queue with id=" + virtualQueueId);
        }
        Student student = studentsInQueue.get(index);

        queueRedisTemplate.opsForList().remove(virtualQueueId, 1L, student);
        studentRedisTemplate.opsForHash().delete(STUDENT_CACHE_NAME, studentId);
        return studentQueueStatus;
    }

    @Override
    public String addQueue(String companyId, String employeeId, Role role) {
        Employee employee = getEmployeeWithId(employeeId);
        if (employee.getVirtualQueueId() != null) {
            throw new InvalidRequestException("Employee with id=" + employeeId +
                    " already has a queue");
        }

        if (employee.getRole() != role) {
            throw new InvalidRequestException("Employee with id=" + employeeId +
                    " associated with role=" + employee.getRole() +
                    " is trying to add a queue for role=" + role);
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
        if (employee.getVirtualQueueId() == null) {
            throw new InvalidRequestException("Employee with employeeId=" + employeeId
                    + "is not associated with a virtual queue");
        }
        String companyId = employee.getCompanyId();
        Role role = employee.getRole();

        VirtualQueueData virtualQueueData = getVirtualQueueData(companyId, role);
        Set<String> employees = virtualQueueData.getEmployeeIds();
        if (employees.size() == 1) {
            removeQueue(companyId, role);
        } else {
            employees.remove(employeeId);
            companyRedisTemplate.opsForHash().put(companyId, role, virtualQueueData);

            employee.setVirtualQueueId(null);
            employeeRedisTemplate.opsForHash().put(EMPLOYEE_CACHE_NAME, employeeId, employee);
        }

    }

    @Override
    public void removeQueue(String companyId, Role role) {
        VirtualQueueData virtualQueueData = getVirtualQueueData(companyId, role);

        Set<String> employeeIds = virtualQueueData.getEmployeeIds();
        for(String id: employeeIds) {
            Employee employee = getEmployeeWithId(id);
            employee.setVirtualQueueId(null);
            employeeRedisTemplate.opsForHash().put(EMPLOYEE_CACHE_NAME, id, employee);
        }

        String virtualQueueId = virtualQueueData.getVirtualQueueId();
        List<Student> students = queueRedisTemplate.opsForList().range(virtualQueueId, 0L, -1L);
        assert students != null;
        for(Student student: students) {
            studentRedisTemplate.opsForHash().delete(STUDENT_CACHE_NAME, student.getId());
        }

        queueRedisTemplate.delete(virtualQueueId);
        companyRedisTemplate.opsForHash().delete(companyId, role);
    }

    @Override
    public Long size(String companyId, Role role) {
        VirtualQueueData virtualQueueData = getVirtualQueueData(companyId, role);
        return queueRedisTemplate.opsForList().size(virtualQueueData.getVirtualQueueId());
    }

    @Override
    public QueueStatus getQueueStatus(StudentQueueStatus studentQueueStatus) {
        VirtualQueueData virtualQueueData = getVirtualQueueData(studentQueueStatus.getCompanyId(),
                studentQueueStatus.getRole());
        return createQueueStatus(studentQueueStatus, virtualQueueData);
    }

    @Override
    public VirtualQueueData getVirtualQueueData(String companyId, Role role) {
        VirtualQueueData virtualQueueData = (VirtualQueueData) companyRedisTemplate.opsForHash()
                .get(companyId, role);
        if (virtualQueueData == null) {
            throw new InvalidRequestException("No virtual queue present for companyId="
                    + companyId + " and role= " + role);
        }
        return virtualQueueData;
    }

    @Override
    public Student getStudentAtHead(String companyId, Role role) {
        String virtualQueueId = getVirtualQueueData(companyId, role).getVirtualQueueId();

        Long size = queueRedisTemplate.opsForList().size(virtualQueueId);
        if (size == null || size == 0L) {
            return null;
        }

        return queueRedisTemplate.opsForList().index(virtualQueueId, 0L);
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
     * Creates and returns QueueStatus based on the given studentQueueStatus and virtualQueueData
     *
     * @param studentQueueStatus current status of the student
     * @param virtualQueueData current data of the virtual queue that the student is in
     * @return QueueStatus
     */
    private QueueStatus createQueueStatus(StudentQueueStatus studentQueueStatus,
                                          VirtualQueueData virtualQueueData) {
        String companyId = studentQueueStatus.getCompanyId();
        Role role = studentQueueStatus.getRole();
        long currentPosition = size(companyId, role);
        QueueStatus queueStatus = new QueueStatus(companyId, studentQueueStatus.getQueueId(),
                studentQueueStatus.getQueueType(), studentQueueStatus.getRole());
        queueStatus.setPosition((int) currentPosition);
        return queueStatus;
    }

}

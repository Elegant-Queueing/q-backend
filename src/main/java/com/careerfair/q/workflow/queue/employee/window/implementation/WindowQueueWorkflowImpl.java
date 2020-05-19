package com.careerfair.q.workflow.queue.employee.window.implementation;

import com.careerfair.q.model.redis.Employee;
import com.careerfair.q.model.redis.Student;
import com.careerfair.q.model.redis.StudentQueueStatus;
import com.careerfair.q.service.queue.response.QueueStatus;
import com.careerfair.q.util.enums.QueueType;
import com.careerfair.q.util.exception.InvalidRequestException;
import com.careerfair.q.workflow.queue.employee.AbstractEmployeeQueueWorkflow;
import com.careerfair.q.workflow.queue.employee.window.WindowQueueWorkflow;
import com.google.cloud.Timestamp;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.util.List;

import static com.careerfair.q.util.constant.Queue.EMPLOYEE_CACHE_NAME;

@Component
public class WindowQueueWorkflowImpl extends AbstractEmployeeQueueWorkflow
        implements WindowQueueWorkflow {

    @Autowired private RedisTemplate<String, String> employeeRedisTemplate;

    @Override
    public QueueStatus joinQueue(String employeeId, Student student,
                                 StudentQueueStatus virtualStudentQueueStatus) {
        Employee employee = getEmployeeWithId(employeeId);
        StudentQueueStatus studentQueueStatus = addStudent(employee, student,
                virtualStudentQueueStatus);
        long currentPosition = size(employee.getId());
        return createQueueStatus(studentQueueStatus, employee, currentPosition);
    }

    @Override
    public StudentQueueStatus leaveQueue(String employeeId, String studentId) {
        Employee employee = getEmployeeWithId(employeeId);
        return removeStudent(employeeId, checkQueueAssociated(employee), studentId, false);
    }

    @Override
    public Employee addQueue(String employeeId) {
        Employee employee = getEmployeeWithId(employeeId);

        if (employee.getWindowQueueId() != null) {
            throw new InvalidRequestException("Employee with employee id=" + employeeId +
                    " is associated with window queue with id=" + employee.getWindowQueueId());
        }

        employee.setWindowQueueId(generateRandomId());
        employeeRedisTemplate.opsForHash().put(EMPLOYEE_CACHE_NAME, employeeId, employee);
        return employee;
    }

    @Override
    public Employee removeQueue(String employeeId, boolean isEmpty) {
        Employee employee = getEmployeeWithId(employeeId);
        removeQueue(employee, isEmpty);
        employee.setWindowQueueId(null);
        employeeRedisTemplate.opsForHash().put(EMPLOYEE_CACHE_NAME, employeeId, employee);
        return employee;
    }

    @Override
    public Long size(String employeeId) {
        return super.size(employeeId);
    }

    @Override
    public QueueStatus getQueueStatus(StudentQueueStatus studentQueueStatus) {
        if (studentQueueStatus.getQueueType() != QueueType.WINDOW) {
            throw new InvalidRequestException("QueueType in studentQueueStatus != WINDOW");
        }

        return super.getQueueStatus(studentQueueStatus);
    }

    @Override
    public List<Student> getAllStudents(String employeeId) {
        return super.getAllStudents(employeeId);
    }

    @Override
    protected String checkQueueAssociated(Employee employee) {
        String windowQueueId = employee.getWindowQueueId();
        if (windowQueueId == null) {
            throw new InvalidRequestException("Employee with employee id=" + employee.getId() +
                    " is not associated with any window queue");
        }
        return windowQueueId;
    }

    @Override
    protected void updateStudentQueueStatus(StudentQueueStatus studentQueueStatus,
                                            Employee employee) {
        studentQueueStatus.setEmployeeId(employee.getId());
        studentQueueStatus.setQueueId(employee.getWindowQueueId());
        studentQueueStatus.setQueueType(QueueType.WINDOW);
        studentQueueStatus.setJoinedWindowQueueAt(Timestamp.now());
    }
}

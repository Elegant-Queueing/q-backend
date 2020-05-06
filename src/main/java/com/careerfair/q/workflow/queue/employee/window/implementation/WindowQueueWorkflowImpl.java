package com.careerfair.q.workflow.queue.employee.window.implementation;

import com.careerfair.q.model.redis.Employee;
import com.careerfair.q.model.redis.Student;
import com.careerfair.q.model.redis.StudentQueueStatus;
import com.careerfair.q.service.queue.response.QueueStatus;
import com.careerfair.q.util.enums.QueueType;
import com.careerfair.q.util.exception.InvalidRequestException;
import com.careerfair.q.workflow.queue.employee.AbstractEmployeeQueueWorkflow;
import com.careerfair.q.workflow.queue.employee.physical.PhysicalQueueWorkflow;
import com.careerfair.q.workflow.queue.employee.window.WindowQueueWorkflow;
import com.google.cloud.Timestamp;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

@Component
public class WindowQueueWorkflowImpl extends AbstractEmployeeQueueWorkflow
        implements WindowQueueWorkflow {

    @Autowired private RedisTemplate<String, Student> queueRedisTemplate;
    @Autowired private RedisTemplate<String, String> studentRedisTemplate;

    @Autowired private PhysicalQueueWorkflow physicalQueueWorkflow;

    @Override
    public QueueStatus addToQueue(String employeeId, Student student) {
        Employee employee = getEmployeeWithId(employeeId);
        StudentQueueStatus studentQueueStatus = joinQueue(employee, student);
        long currentPosition = size(employee.getId()) +
                physicalQueueWorkflow.size(employee.getId());
        return createQueueStatus(studentQueueStatus, employee, currentPosition);
    }

    @Override
    public StudentQueueStatus removeFromQueue(String employeeId, String studentId) {
        Employee employee = getEmployeeWithId(employeeId);
        return removeStudent(employeeId, employee.getWindowQueueId(), studentId, false);
    }

    @Override
    public Long size(String employeeId) {
        Employee employee = getEmployeeWithId(employeeId);
        return queueRedisTemplate.opsForList().size(employee.getWindowQueueId());
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
    protected StudentQueueStatus createStudentQueueStatus(String studentId, Employee employee) {
        StudentQueueStatus studentQueueStatus = new StudentQueueStatus(employee.getCompanyId(),
                studentId, employee.getRole());
        studentQueueStatus.setEmployeeId(employee.getId());
        studentQueueStatus.setQueueId(employee.getWindowQueueId());
        studentQueueStatus.setQueueType(QueueType.WINDOW);
        studentQueueStatus.setJoinedWindowQueueAt(Timestamp.now());
        return studentQueueStatus;
    }
}

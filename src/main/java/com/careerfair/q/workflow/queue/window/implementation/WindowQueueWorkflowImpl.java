package com.careerfair.q.workflow.queue.window.implementation;

import com.careerfair.q.model.redis.VirtualQueueData;
import com.careerfair.q.model.redis.Employee;
import com.careerfair.q.model.redis.Student;
import com.careerfair.q.util.enums.Role;
import com.careerfair.q.util.exception.InvalidRequestException;
import com.careerfair.q.workflow.queue.AbstractQueueWorkflow;
import com.careerfair.q.workflow.queue.window.WindowQueueWorkflow;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class WindowQueueWorkflowImpl extends AbstractQueueWorkflow implements WindowQueueWorkflow {

    @Autowired private RedisTemplate<String, Role> companyRedisTemplate;
    @Autowired private RedisTemplate<String, Student> queueRedisTemplate;

    @Override
    public boolean addToQueue(String employeeId, String studentId, Role role) {
        return false;
    }

    @Override
    public Student removeFromQueue(String employeeId, String studentId) {
        // TODO: This implementation may not be complete or even correct
        Employee employee = getEmployeeWithId(employeeId);

        List<Student> studentsInWindowQueue = queueRedisTemplate.opsForList()
                .range(employee.getWindowQueueId(), 0L, -1L);
        assert studentsInWindowQueue != null;

        int positionInWindowQueue = getStudentPosition(studentId, studentsInWindowQueue);

        if (positionInWindowQueue == -1) {
            throw new InvalidRequestException("Student with student id=" + studentId +
                    " is not present in the window queue of the employee with employee id=" +
                    employeeId);
        }

        Student student = studentsInWindowQueue.get(positionInWindowQueue);
        queueRedisTemplate.opsForList().remove(employee.getWindowQueueId(), 1, student);
        return student;
    }

    @Override
    public Long size(String employeeId) {
        Employee employee = getEmployeeWithId(employeeId);
        return queueRedisTemplate.opsForList().size(employee.getWindowQueueId());
    }

    /**
     * Checks if the given employee has their queue open
     *
     * @param employee employee to validate
     * @throws InvalidRequestException throws the exception if the employee does not have their
     *      queue open
     */
    private void checkEmployeeHasQueueOpen(Employee employee)
            throws InvalidRequestException {
        VirtualQueueData virtualQueueData = (VirtualQueueData) companyRedisTemplate.opsForHash().get(employee.getCompanyId(),
                employee.getRole());
        if (virtualQueueData == null || !virtualQueueData.getEmployeeIds().contains(employee.getId())) {
            throw new InvalidRequestException("No company with company id=" +
                    employee.getCompanyId() + " is associated with employee with employee id=" +
                    employee.getId() + " for role=" + employee.getRole());
        }
    }
}

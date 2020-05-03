package com.careerfair.q.workflow.queue;

import com.careerfair.q.model.redis.Employee;
import com.careerfair.q.model.redis.Student;
import com.careerfair.q.util.exception.InvalidRequestException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;

import java.util.List;
import java.util.stream.Collectors;

import static com.careerfair.q.service.queue.implementation.QueueServiceImpl.EMPLOYEE_CACHE_NAME;

public abstract class AbstractQueueWorkflow {

    @Autowired private RedisTemplate<String, String> employeeRedisTemplate;

    /**
     * Checks and returns whether the employee is present at the career fair
     *
     * @param employeeId id of the employee to check for
     * @return Employee
     * @throws InvalidRequestException throws the exception if the employee is not present at the
     *      career fair
     */
    protected Employee getEmployeeWithId(String employeeId) throws InvalidRequestException {
        Employee employee = (Employee) employeeRedisTemplate.opsForHash().get(EMPLOYEE_CACHE_NAME,
                employeeId);
        if (employee == null) {
            throw new InvalidRequestException("No such employee with employee id=" + employeeId +
                    " exists");
        }
        return employee;
    }

    /**
     * Gets and returns index of the given student in the given queue
     *
     * @param studentId id of the student to find in the queue
     * @param queue queue to find the student in
     * @return int position of the student in the queue. -1 if not present
     * @throws InvalidRequestException throws the exception if the student is not present in the
     *      employee's queue
     */
    protected int getStudentIndexInQueue(String studentId, List<Student> queue) {
        List<String> studentIdsInWindowQueue = queue.stream()
                .map(Student::getId)
                .collect(Collectors.toList());
        return studentIdsInWindowQueue.indexOf(studentId);
    }
}

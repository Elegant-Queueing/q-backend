package com.careerfair.q.workflow.queue;

import com.careerfair.q.model.redis.Employee;
import com.careerfair.q.model.redis.Student;
import com.careerfair.q.model.redis.StudentQueueStatus;
import com.careerfair.q.util.exception.InvalidRequestException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import static com.careerfair.q.service.queue.implementation.QueueServiceImpl.EMPLOYEE_CACHE_NAME;
import static com.careerfair.q.service.queue.implementation.QueueServiceImpl.STUDENT_CACHE_NAME;

public abstract class AbstractQueueWorkflow {

    @Autowired private RedisTemplate<String, String> employeeRedisTemplate;
    @Autowired private RedisTemplate<String, String> studentRedisTemplate;

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
     * Checks and returns whether the status of the student present in a queue
     *
     * @param studentId id of the student whose status to retrieve
     * @return StudentQueueStatus
     * @throws InvalidRequestException throws the exception if the student is not present in a queue
     */
    protected StudentQueueStatus getStudentQueueStatus(String studentId)
            throws InvalidRequestException {
        StudentQueueStatus studentQueueStatus = (StudentQueueStatus) studentRedisTemplate
                .opsForHash().get(STUDENT_CACHE_NAME, studentId);
        if (studentQueueStatus == null) {
            throw new InvalidRequestException("No student with student id=" + studentId +
                    " is present in a queue");
        }
        return studentQueueStatus;
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
    protected int getStudentPosition(String studentId, List<Student> queue) {
        List<String> studentIdsInWindowQueue = queue.stream()
                .map(Student::getId)
                .collect(Collectors.toList());
        return studentIdsInWindowQueue.indexOf(studentId);
    }

    /**
     * Generates and returns a unique random id
     *
     * @return String representing the unique id
     */
    protected String generateRandomId() {
        return UUID.randomUUID().toString();
    }
}

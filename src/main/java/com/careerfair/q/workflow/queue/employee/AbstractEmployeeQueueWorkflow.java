package com.careerfair.q.workflow.queue.employee;

import com.careerfair.q.model.redis.Employee;
import com.careerfair.q.model.redis.Student;
import com.careerfair.q.model.redis.StudentQueueStatus;
import com.careerfair.q.service.queue.response.QueueStatus;
import com.careerfair.q.util.exception.InvalidRequestException;
import com.careerfair.q.workflow.queue.AbstractQueueWorkflow;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;

import java.util.List;

import static com.careerfair.q.service.queue.implementation.QueueServiceImpl.STUDENT_CACHE_NAME;

public abstract class AbstractEmployeeQueueWorkflow extends AbstractQueueWorkflow {

    @Autowired private RedisTemplate<String, Student> queueRedisTemplate;
    @Autowired private RedisTemplate<String, String> studentRedisTemplate;

    /**
     * Adds the given student to the given employee's queue
     *
     * @param employee employee to whose queue the student is to be added
     * @param student the student to be added
     * @return StudentQueueStatus
     */
    protected StudentQueueStatus joinQueue(Employee employee, Student student) {
        String queueId = checkQueueAssociated(employee);

        List<Student> studentsInQueue = queueRedisTemplate.opsForList()
                .range(queueId, 0L, -1L);
        assert studentsInQueue != null;

        if (getStudentPosition(student.getId(), studentsInQueue) != -1) {
            throw new InvalidRequestException("Student with student id=" + student.getId() +
                    " is already present in the queue of the employee with employee id=" +
                    employee.getId());
        }

        StudentQueueStatus studentQueueStatus = createStudentQueueStatus(student.getId(), employee);
        studentRedisTemplate.opsForHash().put(STUDENT_CACHE_NAME, student.getId(),
                studentQueueStatus);
        queueRedisTemplate.opsForList().rightPush(queueId, student);
        return studentQueueStatus;
    }

    /**
     * Removes the given student from the given employee's queue
     *
     * @param employeeId id of the employee from whose queue the student is to be removed
     * @param queueId id of the queue to remove the student from
     * @param studentId id of the student to be removed
     * @param isFirst whether the student to be removed should be the first in the queue
     * @return StudentQueueStatus
     * @throws InvalidRequestException throws if the student is not present in the employee's
     *      queue or the student is not the first in queue if isFirst flag is set
     */
    protected StudentQueueStatus removeStudent(String employeeId, String queueId, String studentId,
                                               boolean isFirst) throws InvalidRequestException {
        List<Student> studentsInQueue = queueRedisTemplate.opsForList().range(queueId, 0L, -1L);
        assert studentsInQueue != null;

        int position = getStudentPosition(studentId, studentsInQueue);

        if (position == -1) {
            throw new InvalidRequestException("Student with student id=" + studentId +
                    " is not present in the queue of employee with employee id=" +
                    employeeId);
        } else if (isFirst && position != 0) {
            throw new InvalidRequestException("Student with student id=" + studentId +
                    " is not at the head of the queue of employee with employee id=" +
                    employeeId);
        }

        StudentQueueStatus studentQueueStatus = (StudentQueueStatus) studentRedisTemplate
                .opsForHash().get(STUDENT_CACHE_NAME, studentId);
        studentRedisTemplate.opsForHash().delete(STUDENT_CACHE_NAME, studentId);

        if (isFirst) {
            queueRedisTemplate.opsForList().leftPop(queueId);
        } else {
            queueRedisTemplate.opsForList().remove(queueId, 1L, studentsInQueue.get(position));
        }

        return studentQueueStatus;
    }

    /**
     * Creates and returns QueueStatus based on the given studentQueueStatus and employee
     *
     * @param studentQueueStatus student's queue status
     * @param employee employee in whose queue the student is present
     * @return QueueStatus
     */
    protected QueueStatus createQueueStatus(StudentQueueStatus studentQueueStatus,
                                            Employee employee, long currentPosition) {
        int waitTime = (int) (calcEmployeeAverageTime(employee) * currentPosition);

        QueueStatus queueStatus = new QueueStatus(studentQueueStatus.getQueueId(),
                studentQueueStatus.getQueueType(), studentQueueStatus.getRole(),
                (int) currentPosition, waitTime);
        queueStatus.setEmployee(employee);
        return queueStatus;
    }

    /**
     * Calculates and returns the average time spent by the employee talking to a student
     *
     * @param employee The employee whose average time is to be calculated
     * @return double Average time spent by the employee talking to a student
     */
    protected double calcEmployeeAverageTime(Employee employee) {
        return employee.getTotalTimeSpent() * 1. / Math.max(employee.getNumRegisteredStudents(), 1);
    }

    /**
     * Checks whether a queue is associated with the given employee
     *
     * @param employee employee to validate
     * @return String id of the queue associated with the given employee
     * @throws InvalidRequestException throws the exception if no queue is associated with the
     *      employee
     */
    protected abstract String checkQueueAssociated(Employee employee);

    /**
     * Creates and return the student's queue status based on the given employee
     *
     * @param studentId id of the student for whom to create the status
     * @param employee the employee to update
     * @return StudentQueueStatus
     */
    protected abstract StudentQueueStatus createStudentQueueStatus(String studentId,
                                                                   Employee employee);
}

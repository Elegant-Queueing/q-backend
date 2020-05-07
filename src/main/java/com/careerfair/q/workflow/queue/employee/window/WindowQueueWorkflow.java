package com.careerfair.q.workflow.queue.employee.window;

import com.careerfair.q.model.redis.Employee;
import com.careerfair.q.model.redis.Student;
import com.careerfair.q.model.redis.StudentQueueStatus;
import com.careerfair.q.service.queue.response.QueueStatus;

public interface WindowQueueWorkflow {

    /**
     * Adds the given student to the given employee's window queue
     *
     * @param employeeId id of the employee whose queue the student is to join
     * @param student student requesting to join
     * @param virtualStudentQueueStatus the current queue status of the student
     * @return QueueStatus
     */
    QueueStatus joinQueue(String employeeId, Student student,
                          StudentQueueStatus virtualStudentQueueStatus);

    /**
     * Removes the given student from the given employee's window queue
     *
     * @param employeeId id of the employee from whose queue to remove the student from
     * @param studentId id of the student to remove
     * @return StudentQueueStatus
     */
    StudentQueueStatus leaveQueue(String employeeId, String studentId);

    /**
     * Associates a window queue to the given employee
     *
     * @param employeeId id of the employee whose queue is to be added
     * @return Employee
     */
    Employee addQueue(String employeeId);

    /**
     * Removes the given employee's queue
     *
     * @param employeeId id of the employee whose queue needs to be paused
     * @param isEmpty flag to assert that queue needs to be empty for successful operation
     * @return Employee
     */
    Employee removeQueue(String employeeId, boolean isEmpty);

    /**
     * Returns the size of the given employee's window queue
     *
     * @param employeeId id of the employee
     * @return Long size of the queue
     */
    Long size(String employeeId);
}

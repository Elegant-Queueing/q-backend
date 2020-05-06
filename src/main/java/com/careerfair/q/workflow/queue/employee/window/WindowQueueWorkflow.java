package com.careerfair.q.workflow.queue.employee.window;

import com.careerfair.q.model.redis.Student;
import com.careerfair.q.model.redis.StudentQueueStatus;
import com.careerfair.q.service.queue.response.QueueStatus;

public interface WindowQueueWorkflow {

    /**
     * Adds the given student to the given employee's window queue
     *
     * @param employeeId id of the employee whose queue the student is to join
     * @param student student requesting to join
     * @return QueueStatus
     */
    QueueStatus addToQueue(String employeeId, Student student);

    /**
     * Removes the given student from the given employee's window queue
     *
     * @param employeeId id of the employee from whose queue to remove the student from
     * @param studentId id of the student to remove
     * @return StudentQueueStatus
     */
    StudentQueueStatus removeFromQueue(String employeeId, String studentId);

    /**
     *
     * @param employeeId
     * @return
     */
    Long size(String employeeId);
}

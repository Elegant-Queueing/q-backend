package com.careerfair.q.workflow.queue.employee.physical;

import com.careerfair.q.model.redis.Employee;
import com.careerfair.q.model.redis.Student;
import com.careerfair.q.model.redis.StudentQueueStatus;
import com.careerfair.q.service.queue.response.EmployeeQueueData;
import com.careerfair.q.service.queue.response.QueueStatus;

public interface PhysicalQueueWorkflow {

    /**
     * Adds the given student to the given employee's queue
     *
     * @param employeeId id of the employee whose queue to join
     * @param student student requesting to join
     * @param studentWindowQueueStatus the window queue status of the status
     * @return QueueStatus
     */
    QueueStatus joinQueue(String employeeId, Student student,
                          StudentQueueStatus studentWindowQueueStatus);

    /**
     * Removes the given student from the employee's queue
     *
     * @param employeeId id of the employee whose queue the student wants to leave
     * @param studentId id of the student wanting to leave
     */
    void leaveQueue(String employeeId, String studentId);

    /**
     * Associates a physical queue to the given employee
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
     * Registers that the given student has completed their talk with the given employee
     *
     * @param employeeId id of the employee who the student has talked to
     * @param studentId id of the student
     * @return EmployeeQueueData
     */
    EmployeeQueueData registerStudent(String employeeId, String studentId);

    /**
     * Removes the given student from the given employee's queue
     *
     * @param employeeId id of the employee from whose queue the student is to be removed
     * @param studentId id of the student being removed
     * @return EmployeeQueueData
     */
    EmployeeQueueData removeStudentFromQueue(String employeeId, String studentId);

    /**
     * Returns the data for the given employee's queue
     *
     * @param employeeId id of employee whose queue's data is to be retrieved
     * @return EmployeeQueueData
     */
    EmployeeQueueData getEmployeeQueueData(String employeeId);

    /**
     * Returns the size of the given employee's queue
     *
     * @param employeeId id of employee whose queue's size is to be returned
     * @return Long size of employee's queue
     */
    Long size(String employeeId);
}

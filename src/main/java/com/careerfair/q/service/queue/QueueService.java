package com.careerfair.q.service.queue;

import com.careerfair.q.model.redis.Student;
import com.careerfair.q.util.enums.Role;
import com.careerfair.q.service.queue.response.*;

public interface QueueService {

    /**
     * Gets the wait time for the given company and role
     *
     * @param companyId id of the company whose wait time is to retrieved
     * @param role role the student is recruiting for
     * @return GetWaitTimeResponse
     */
    GetWaitTimeResponse getCompanyWaitTime(String companyId, Role role);

    /**
     * Gets the wait time for the all the companies with a queue open for the given role
     *
     * @param role role the student is recruiting for
     * @return GetWaitTimeResponse
     */
    GetWaitTimeResponse getAllCompaniesWaitTime(Role role);

    /**
     * Adds the given student to the given company's virtual queue for the given role
     *
     * @param companyId id of the company whose virtual queue to join
     * @param role role to join for
     * @param student the student requesting to join
     * @return JoinQueueResponse
     */
    JoinQueueResponse joinVirtualQueue(String companyId, Role role, Student student);

    /**
     * Adds the given student to the given employee's queue
     *
     * @param employeeId id of the employee whose queue to join
     * @param studentId id of the student requesting to join
     * @return JoinQueueResponse
     */
    JoinQueueResponse joinEmployeeQueue(String employeeId, String studentId);

    /**
     * Removes the given student from the given company's queue for the given role
     *
     * @param companyId id of the company whose queue the student wants to leave
     * @param studentId id of the student requesting to leave
     * @param role role that the student is recruiting for
     */
    void leaveQueue(String companyId, String studentId, Role role);

    /**
     * Gets the status of the queue for the given student
     *
     * @param studentId id of the student whose status of queue is to be retrieved
     * @return GetQueueStatusResponse
     */
    GetQueueStatusResponse getQueueStatus(String studentId);

    /**
     * Add the given employee's queue associated with the given company and given role to the fair
     *
     * @param companyId id of the company associated with the employee
     * @param employeeId id of the employee whose queue is to be added
     * @param role role associated with the given employee
     * @return AddQueueResponse
     */
    AddQueueResponse addQueue(String companyId, String employeeId, Role role);

    /**
     * Returns the data for the given employee's queue
     *
     * @param employeeId id of employee whose queue's data is to be retrieved
     * @return GetEmployeeQueueDataResponse
     */
    GetEmployeeQueueDataResponse getEmployeeQueueData(String employeeId);

    /**
     * Pauses the queue for the given employee
     *
     * @param employeeId id of the employee whose queue is to be paused
     * @return PauseQueueResponse
     */
    PauseQueueResponse pauseQueue(String employeeId);

    /**
     * Registers that the given student has completed their talk with the given employee
     *
     * @param employeeId id of the employee who the student has talked to
     * @param studentId id of the student
     * @return RemoveStudentResponse
     */
    RemoveStudentResponse registerStudent(String employeeId, String studentId);

    /**
     * Removes the given student from the given employee's queue
     *
     * @param employeeId id of the employee from whose queue the student is to be removed
     * @param studentId id of the student being removed
     * @return RemoveStudentResponse
     */
    RemoveStudentResponse skipStudent(String employeeId, String studentId);

//    /**
//     * Clears all data in Redis. USED FOR TESTING ONLY
//     */
//    void clearAll();
//
//    /**
//     * Gets all data in Redis. USED FOR TESTING ONLY
//     */
//    String getAll();
}

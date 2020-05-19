package com.careerfair.q.service.notification;

import com.careerfair.q.util.enums.Role;
import com.careerfair.q.util.exception.NotificationException;

public interface NotificationService {

    /**
     * Notifies any client registered to the given role about a company's addition of queue
     *
     * @param companyId id of the company whose queue is added
     * @param role role that the client has to be registered to
     * @throws NotificationException if the topic is not valid or an unexpected error occurs in
     *      sending the notification
     */
    void notifyQueueOpen(String companyId, Role role) throws NotificationException;

    /**
     * Notifies any client registered to the given role about a company's removal of queue
     *
     * @param companyId id of the company whose queue is removed
     * @param role role that the client has to be registered to
     * @throws NotificationException if the topic is not valid or an unexpected error occurs in
     *      sending the notification
     */
    void notifyQueueClose(String companyId, Role role) throws NotificationException;

    /**
     * Notifies the client subscribed to the given role about the company's wait time
     *
     * @param companyId id of the company whose wait time is to be notified
     * @param role role that the client has to be registered to
     * @throws NotificationException if an unexpected error occurs in sending the notification
     */
    void notifyCompanyWaitTime(String companyId, Role role) throws NotificationException;

    /**
     * Notifies all the students in the queue for the given company and role about their position in
     * the queue
     *
     * @param companyId id of the company that the students are in
     * @param role role for which the student is queueing for
     * @param position the start position in the virtual queue to send notifications
     * @throws NotificationException if an unexpected error occurs in sending the notification
     */
    void notifyPositionUpdate(String companyId, Role role, int position)
            throws NotificationException;

    /**
     * Notifies all the students in the employee's queue about their position in the queue
     *
     * @param companyId id of the company the employee is associated with
     * @param employeeId id of the employee that the students are in
     * @param role role that the employee is associated with
     * @throws NotificationException if an unexpected error occurs in sending the notification
     */
    void notifyPositionUpdate(String companyId, String employeeId, Role role)
            throws NotificationException;

    /**
     * Notifies a student about their position in the queue
     *
     * @param studentId id of the student to notify
     * @param position the start position in the virtual queue to send notifications
     * @throws NotificationException if an unexpected error occurs in sending the notification
     */
    void notifyPositionUpdate(String studentId, int position) throws NotificationException;

    /**
     * Notifies the given student about their assigned employee
     *
     * @param employeeId id of the employee assigned to the student
     * @param studentId id of the student the employee is assigned to
     * @throws NotificationException if an unexpected error occurs in sending the notification
     */
    void notifyEmployeeAssociation(String employeeId, String studentId)
            throws NotificationException;

    /**
     * Notifies the given employee about student addition in their queue
     *
     * @param employeeId id of the employee whose queue is been updated
     * @param studentId id of the student added to the employee's queue
     * @throws NotificationException if an unexpected error occurs in sending the notification
     */
    void notifyStudentAdditionFromEmployeeQueue(String employeeId, String studentId)
            throws NotificationException;

    /**
     * Notifies the given employee about student removal in their queue
     *
     * @param employeeId id of the employee whose queue is been updated
     * @param studentId id of the student removed from the employee's queue
     * @throws NotificationException if an unexpected error occurs in sending the notification
     */
    void notifyStudentRemovalFromEmployeeQueue(String employeeId, String studentId)
            throws NotificationException;

    /**
     * Notifies all the students that they have been removed from the virtual queue
     *
     * @param companyId id of the company that the students were in
     * @param role role that the students were queued up for
     */
    void notifyStudentRemovalFromVirtualQueue(String companyId, Role role);
}

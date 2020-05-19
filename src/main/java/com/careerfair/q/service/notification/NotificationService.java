package com.careerfair.q.service.notification;

import com.careerfair.q.util.enums.Topic;
import com.careerfair.q.util.exception.NotificationException;

public interface NotificationService {

    /**
     * Notifies any client registered to the {@link Topic#QUEUE} and the given role based topic
     * about a company's addition of queue
     *
     * @param companyId id of the company whose queue is added
     * @param topic role based topic that the client has to be registered to
     * @throws NotificationException if the topic is not valid or an unexpected error occurs in
     *      sending the notification
     */
    void notifyQueueOpen(String companyId, Topic topic) throws NotificationException;

    /**
     * Notifies any client registered to the {@link Topic#QUEUE} and the given role based topic
     * about a company's removal of queue
     *
     * @param companyId id of the company whose queue is removed
     * @param topic role based topic that the client has to be registered to
     * @throws NotificationException if the topic is not valid or an unexpected error occurs in
     *      sending the notification
     */
    void notifyQueueClose(String companyId, Topic topic) throws NotificationException;

    void notifyPositionUpdate();

    void notifyEmployeeAssociation();
}

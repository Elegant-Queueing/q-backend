package com.careerfair.q.service.notification;

import com.careerfair.q.util.enums.Topic;
import com.careerfair.q.util.exception.NotificationException;

public interface NotificationService {

    void notifyQueueOpen(String companyId, Topic topic) throws NotificationException;

    void notifyQueueClose(Topic primaryTopic, Topic... otherTopics) throws NotificationException;

    void notifyPositionUpdate();

    void notifyEmployeeAssociation();
}

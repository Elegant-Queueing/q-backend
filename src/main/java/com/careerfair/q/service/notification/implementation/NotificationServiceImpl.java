package com.careerfair.q.service.notification.implementation;

import com.careerfair.q.model.db.Company;
import com.careerfair.q.service.database.FirebaseService;
import com.careerfair.q.service.notification.NotificationService;
import com.careerfair.q.service.queue.QueueService;
import com.careerfair.q.util.constant.Fair;
import com.careerfair.q.util.enums.Role;
import com.careerfair.q.util.enums.Topic;
import com.careerfair.q.util.exception.NotificationException;
import com.google.common.collect.Maps;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.FirebaseMessagingException;
import com.google.firebase.messaging.Message;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public class NotificationServiceImpl implements NotificationService {

    private final FirebaseService firebaseService;
    private final QueueService queueService;

    public NotificationServiceImpl(@Autowired FirebaseService firebaseService,
                                   @Autowired QueueService queueService) {
        this.firebaseService = firebaseService;
        this.queueService = queueService;
    }

    @Override
    public void notifyQueueOpen(String companyId, Topic topic) throws NotificationException {
        System.out.println(topic.getTopic());
        new Thread(() -> notifyQueueChange(companyId, topic, true)).start();
    }

    @Override
    public void notifyQueueClose(String companyId, Topic topic) {
        new Thread(() -> notifyQueueChange(companyId, topic, false)).start();
    }

    @Override
    public void notifyPositionUpdate() {

    }

    @Override
    public void notifyEmployeeAssociation() {

    }

    /**
     * Notifies any client registered to the {@link Topic#QUEUE} and the given role based topic
     * about a company's addition or removal of queue
     *
     * @param companyId id of the company whose queue is added or removed
     * @param topic role based topic that the client has to be registered to
     * @param isAddition signifies whether the queue is added or removed
     */
    private void notifyQueueChange(String companyId, Topic topic, boolean isAddition) {
        Role role;
        
        try {
            role = Role.valueOf(topic.name());
        } catch (Exception ex) {
            throw new NotificationException("Invalid notification topic=" + topic);
        }

        String condition = "'" + Topic.QUEUE.getTopic() + "' in topics && '" +
                topic.getTopic() + "' in topics";

        Message message = Message.builder()
                .putAllData(getData(companyId, role, isAddition))
                .setCondition(condition)
                .build();

        try {
            FirebaseMessaging.getInstance().send(message);
        } catch (FirebaseMessagingException ex) {
            throw new NotificationException(ex.getMessage());
        }
    }

    /**
     * Returns a Map consisting of all the data that needs to be passed in for the message
     *
     * @param companyId id of the company whose data to extract
     * @param role role that the student is recruiting for
     * @return Map containing all relevant information
     */
    private Map<String, String> getData(String companyId, Role role, boolean isAddition) {
        Company company = firebaseService.getCompanyWithId(Fair.THE_FAIR_ID, companyId);

        Map<String, String> data = Maps.newHashMap();
        data.put("name", company.getName());
        data.put("employer-match", "100");  // TODO: Update this

        if (isAddition) {
            int waitTime = queueService.getOverallWaitTime(companyId, role);
            data.put("wait-time", String.valueOf(waitTime));
            data.put("add", String.valueOf(true));
        } else {
            data.put("add", String.valueOf(false));
        }

        return data;
    }
}

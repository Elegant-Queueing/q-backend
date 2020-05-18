package com.careerfair.q.service.notification.implementation;

import com.careerfair.q.model.db.Company;
import com.careerfair.q.service.database.FirebaseService;
import com.careerfair.q.service.notification.NotificationService;
import com.careerfair.q.util.constant.Fair;
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

    public NotificationServiceImpl(@Autowired FirebaseService firebaseService) {
        this.firebaseService = firebaseService;
    }

    @Override
    public void notifyQueueOpen(String companyId, Topic topic) throws NotificationException {
        new Thread(() -> {
            String condition = new StringBuilder("'")
                    .append(Topic.QUEUE.getTopicName())
                    .append("' in topics && '")
                    .append(topic.getTopicName())
                    .append("' in topics").toString();

            Company company = firebaseService.getCompanyWithId(Fair.THE_FAIR_ID, companyId);

            Message message = Message.builder()
                    .putAllData(getCompanyData(company))
                    .setCondition(condition)
                    .build();

            try {
                FirebaseMessaging.getInstance().send(message);
            } catch (FirebaseMessagingException ex) {
                throw new NotificationException(ex.getMessage());
            }
        }).start();
    }

    @Override
    public void notifyQueueClose(Topic primaryTopic, Topic... otherTopics) {

    }

    @Override
    public void notifyPositionUpdate() {

    }

    @Override
    public void notifyEmployeeAssociation() {

    }

    /**
     * Returns a Map consisting of all the data that needs to be passed in for the message
     *
     * @param company company whose data to extract
     * @return Map containing all relevant information
     */
    private Map<String, String> getCompanyData(Company company) {
        Map<String, String> companyDataMap = Maps.newHashMap();
        companyDataMap.put("name", company.getName());
        return companyDataMap;
    }
}

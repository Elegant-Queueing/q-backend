package com.careerfair.q.service.notification.implementation;

import com.careerfair.q.model.db.Company;
import com.careerfair.q.model.db.Employee;
import com.careerfair.q.model.db.Student;
import com.careerfair.q.service.employee.EmployeeService;
import com.careerfair.q.service.fair.FairService;
import com.careerfair.q.service.fair.response.GetWaitTimeResponse;
import com.careerfair.q.service.notification.NotificationService;
import com.careerfair.q.service.queue.QueueService;
import com.careerfair.q.service.student.StudentService;
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

import java.util.List;
import java.util.ListIterator;
import java.util.Map;

@Service
public class NotificationServiceImpl implements NotificationService {

    private final QueueService queueService;
    private final StudentService studentService;
    private final FairService fairService;
    private final EmployeeService employeeService;

    public NotificationServiceImpl(@Autowired QueueService queueService,
                                   @Autowired StudentService studentService,
                                   @Autowired FairService fairService,
                                   @Autowired EmployeeService employeeService) {
        this.queueService = queueService;
        this.studentService = studentService;
        this.fairService = fairService;
        this.employeeService = employeeService;
    }

    @Override
    public void notifyQueueOpen(String companyId, Role role) throws NotificationException {
        new Thread(() -> notifyQueueChange(companyId, role, true)).start();
    }

    @Override
    public void notifyQueueClose(String companyId, Role role) throws NotificationException {
        new Thread(() -> notifyQueueChange(companyId, role, false)).start();
    }

    @Override
    public void notifyCompanyWaitTime(String companyId, Role role) throws NotificationException {
        new Thread(() -> {
            Topic notificationTopic = getTopicFromRole(role);
            GetWaitTimeResponse waitTimeResponse = fairService.getCompanyWaitTime(companyId, role);
            int waitTime = waitTimeResponse.getCompanyWaitTimes().get(companyId);

            Message message = Message.builder()
                    .putData("company-id", companyId)
                    .putData("wait-time", String.valueOf(waitTime))
                    .setTopic(notificationTopic.getTopic())
                    .build();
            sendMessage(message);
        }).start();
    }

    @Override
    public void notifyPositionUpdate(String companyId, Role role, int position)
            throws NotificationException {
        new Thread(() -> {
            ListIterator<com.careerfair.q.model.redis.Student> studentListIterator = queueService
                    .getVirtualQueueStudents(companyId, role).listIterator(position - 1);

            while (studentListIterator.hasNext()) {
                int index = studentListIterator.nextIndex();
                String studentId = studentListIterator.next().getId();

                notifyPositionUpdate(studentId, index + 1);
            }
        }).start();
    }

    @Override
    public void notifyPositionUpdate(String companyId, String employeeId, Role role)
            throws NotificationException {
        notifyPositionUpdate(companyId, role, 1);

        new Thread(() -> {
            List<com.careerfair.q.model.redis.Student> students = queueService
                    .getEmployeeQueueStudents(employeeId);

            for (int i = 0; i < students.size(); i++) {
                notifyPositionUpdate(students.get(i).getId(), i + 1);
            }
        }).start();
    }

    @Override
    public void notifyPositionUpdate(String studentId, int position) {
        new Thread(() -> {
            String registrationToken = studentService.getRegistrationToken(studentId);

            Message message = Message.builder()
                    .setToken(registrationToken)
                    .putData("student-id", studentId)
                    .putData("position", String.valueOf(position))
                    .build();
            sendMessage(message);
        }).start();
    }

    @Override
    public void notifyEmployeeAssociation(String employeeId, String studentId)
            throws NotificationException{
        new Thread(() -> {
            String registrationToken = studentService.getRegistrationToken(studentId);
            Employee employee = employeeService.getEmployeeWithId(employeeId).getEmployee();

            Map<String, String> employeeData = Maps.newHashMap();
            employeeData.put("student-id", studentId);
            employeeData.put("employee-id", employeeId);
            employeeData.put("name", employee.getName());
            employeeData.put("bio", employee.getBio());
            employeeData.put("email", employee.getEmail());

            Message message = Message.builder()
                    .setToken(registrationToken)
                    .putAllData(employeeData)
                    .build();
            sendMessage(message);
        }).start();
    }

    @Override
    public void notifyStudentAdditionFromEmployeeQueue(String employeeId, String studentId)
            throws NotificationException {
        new Thread(() -> notifyEmployeeQueueUpdate(employeeId, studentId, true)).start();
    }

    @Override
    public void notifyStudentRemovalFromEmployeeQueue(String employeeId, String studentId)
            throws NotificationException {
        new Thread(() -> notifyEmployeeQueueUpdate(employeeId, studentId, false)).start();
    }

    @Override
    public void notifyStudentRemovalFromVirtualQueue(String companyId, Role role) {
        new Thread(() -> {
            queueService.getVirtualQueueStudents(companyId, role).forEach(student ->
                    notifyPositionUpdate(student.getId(), -1));
        }).start();
    }

    /**
     * Notifies any client registered to the given role about a company's addition or removal of
     * queue
     *
     * @param companyId id of the company whose queue is added or removed
     * @param role role that the client has to be registered to
     * @param isAddition signifies whether the queue is added or removed
     */
    private void notifyQueueChange(String companyId, Role role, boolean isAddition) {
        Topic notificationTopic = getTopicFromRole(role);
        Company company = fairService.getCompanyWithId(Fair.THE_FAIR_ID, companyId).getCompany();

        Map<String, String> companyData = Maps.newHashMap();
        companyData.put("company-id", companyId);
        companyData.put("name", company.getName());
        companyData.put("add", String.valueOf(isAddition));

        if (isAddition) {
            int waitTime = queueService.getOverallWaitTime(companyId, role);
            companyData.put("employer-match", "100");  // TODO: Update this. How do I even get this?? No student here...
            companyData.put("wait-time", String.valueOf(waitTime));
        }

        Message message = Message.builder()
                .putAllData(companyData)
                .setTopic(notificationTopic.getTopic())
                .build();
        sendMessage(message);
    }

    /**
     * Notifies the given employee about student addition or removal in their queue
     *
     * @param employeeId id of the employee whose queue is been updated
     * @param studentId id of the student added to or removed from the employee's queue
     * @param isAddition whether the student is an addition or a removal
     */
    private void notifyEmployeeQueueUpdate(String employeeId, String studentId,
                                           boolean isAddition) {
        Student student = studentService.getStudentWithId(studentId).getStudent();
        String registrationToken = employeeService.getRegistrationToken(employeeId);

        Map<String, String> queueData = Maps.newHashMap();
        queueData.put("employee-id", employeeId);
        queueData.put("student-id", studentId);
        queueData.put("first-name", student.getFirstName());
        queueData.put("last-name", student.getLastName());
        queueData.put("add", String.valueOf(isAddition));

        Message message = Message.builder()
                .setToken(registrationToken)
                .putAllData(queueData)
                .build();
        sendMessage(message);
    }

    /**
     * Gets the topic from a role
     *
     * @param role role that needs to be converted to a topic
     * @return Topic
     */
    private Topic getTopicFromRole(Role role) {
        try {
            return Topic.valueOf(role.name());
        } catch (Exception ex) {
            throw new NotificationException("Invalid role=" + role);
        }
    }

    /**
     * Sends the message
     *
     * @param message the message to send
     */
    private void sendMessage(Message message) {
        try {
            FirebaseMessaging.getInstance().send(message);
        } catch (FirebaseMessagingException ex) {
            throw new NotificationException(ex.getMessage());
        }
    }
}

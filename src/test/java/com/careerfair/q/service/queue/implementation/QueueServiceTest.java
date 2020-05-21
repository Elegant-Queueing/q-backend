package com.careerfair.q.service.queue.implementation;

import com.careerfair.q.model.redis.Employee;
import com.careerfair.q.model.redis.Student;
import com.careerfair.q.model.redis.StudentQueueStatus;
import com.careerfair.q.model.redis.VirtualQueueData;
import com.careerfair.q.service.database.FirebaseService;
import com.careerfair.q.service.queue.response.JoinQueueResponse;
import com.careerfair.q.service.queue.response.QueueStatus;
import com.careerfair.q.service.validation.ValidationService;
import com.careerfair.q.util.enums.QueueType;
import com.careerfair.q.util.enums.Role;
import com.careerfair.q.workflow.queue.employee.physical.PhysicalQueueWorkflow;
import com.careerfair.q.workflow.queue.employee.window.WindowQueueWorkflow;
import com.careerfair.q.workflow.queue.virtual.VirtualQueueWorkflow;
import org.assertj.core.util.Sets;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.Spy;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.RedisTemplate;

import java.util.Collections;
import java.util.Set;

import static com.careerfair.q.util.constant.Queue.MAX_EMPLOYEE_QUEUE_SIZE;
import static org.junit.Assert.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

public class QueueServiceTest {

    @Mock
    private VirtualQueueWorkflow virtualQueueWorkflow;
    @Mock
    private WindowQueueWorkflow windowQueueWorkflow;
    @Mock
    private PhysicalQueueWorkflow physicalQueueWorkflow;

    @Spy
    private RedisTemplate<String, String> employeeRedisTemplate;
    @Spy
    private RedisTemplate<String, String> studentRedisTemplate;

    @Mock
    private HashOperations<String, Object, Object> employeeHashOperations;
    @Mock
    private HashOperations<String, Object, Object> studentHashOperations;

    @Mock
    private FirebaseService firebaseService;
    @Mock
    private ValidationService validationService;

    @InjectMocks
    private final QueueServiceImpl queueService = new QueueServiceImpl();

    private QueueStatus virtualQueueStatus;
    private QueueStatus windowQueueStatus;
    private QueueStatus physicalQueueStatus;

    private Student student;
    private Employee employee;
    private StudentQueueStatus studentQueueStatus;

    @BeforeEach
    public void setupMocks() {
        MockitoAnnotations.initMocks(this);

        student = new Student("s1", "student1");
        employee = new Employee("e1", "c1", Role.SWE);
        studentQueueStatus = new StudentQueueStatus("student1", "c1", "s1", Role.SWE);

        virtualQueueStatus = new QueueStatus("c1", "vq1", QueueType.VIRTUAL, Role.SWE);
        windowQueueStatus = new QueueStatus("c1", "wq1", QueueType.WINDOW, Role.SWE);
        windowQueueStatus.setEmployee(employee);
        physicalQueueStatus = new QueueStatus("c1", "pq1", QueueType.PHYSICAL, Role.SWE);
        physicalQueueStatus.setEmployee(employee);

        when(employeeRedisTemplate.opsForHash()).thenReturn(employeeHashOperations);
        when(studentRedisTemplate.opsForHash()).thenReturn(studentHashOperations);
    }

    @Test
    public void testJoinVirtualQueueNotHead() {
        int position = 2;
        virtualQueueStatus.setPosition(position);

        employee.setTotalTimeSpent(5);
        employee.setNumRegisteredStudents(1);

        Set<String> employees = Sets.newHashSet(Collections.singleton("e1"));
        VirtualQueueData virtualQueueData = new VirtualQueueData("vq1", employees);

        doNothing().when(validationService).checkValidCompanyId(anyString());
        doNothing().when(validationService).checkValidStudentId(anyString());
        doReturn(virtualQueueStatus).when(virtualQueueWorkflow).joinQueue(anyString(), any(),
                any());
        doReturn(virtualQueueData).when(virtualQueueWorkflow).getVirtualQueueData(anyString(),
                any());
        doReturn(employee).when(employeeHashOperations).get(anyString(), any());

        JoinQueueResponse response = queueService.joinVirtualQueue("c1", Role.SWE,
                student);

        verify(validationService).checkValidStudentId(anyString());
        verify(validationService).checkValidCompanyId(anyString());
        verify(virtualQueueWorkflow).joinQueue(anyString(), any(), any());

        assertNotNull(response);
        assertNotNull(response.getQueueStatus());

        assertEquals(response.getQueueStatus().getCompanyId(), "c1");
        assertEquals(response.getQueueStatus().getQueueId(), "vq1");
        assertEquals(response.getQueueStatus().getRole(), Role.SWE);
        assertEquals(response.getQueueStatus().getPosition(), MAX_EMPLOYEE_QUEUE_SIZE + position);
        assertEquals(response.getQueueStatus().getQueueType(), QueueType.VIRTUAL);
        assertNotEquals(response.getQueueStatus().getWaitTime(), 0);
        assertNull(response.getQueueStatus().getEmployee());
    }

    @Test
    public void testJoinVirtualQueueHeadWithNoEmployeeSpace() {
        int position = 1;
        long windowSize = 4L;
        long physicalSize = 1L;

        int numStudents = 1;
        int timeSpent = 5;
        int expectedWaitTime = (int) (1. * (windowSize + physicalSize) * timeSpent / numStudents);

        assertEquals(windowSize + physicalSize, MAX_EMPLOYEE_QUEUE_SIZE);

        virtualQueueStatus.setPosition(position);

        QueueStatus queueStatus = testJoinVirtualQueueAtHead(windowSize, physicalSize, numStudents,
                timeSpent);

        assertEquals(queueStatus.getQueueId(), "vq1");
        assertEquals(queueStatus.getPosition(), MAX_EMPLOYEE_QUEUE_SIZE + position);
        assertEquals(queueStatus.getQueueType(), QueueType.VIRTUAL);
        assertEquals(queueStatus.getWaitTime(), expectedWaitTime);
        assertNull(queueStatus.getEmployee());
    }

    @Test
    public void testJoinVirtualQueueHeadWithEmployeeSpace() {
        int windowPosition = 3;
        long physicalSize = 0L;

        int numStudents = 1;
        int timeSpent = 5;
        int expectedWaitTime = (int) ((windowPosition + physicalSize - 1.) * timeSpent / numStudents);

        virtualQueueStatus.setPosition(1);
        windowQueueStatus.setPosition(windowPosition);

        doReturn(studentQueueStatus).when(virtualQueueWorkflow).leaveQueue(anyString(), anyString(),
                any());
        doReturn(windowQueueStatus).when(windowQueueWorkflow).joinQueue(anyString(), any(), any());

        QueueStatus queueStatus = testJoinVirtualQueueAtHead(0L, physicalSize, numStudents,
                timeSpent);

        assertEquals(queueStatus.getQueueId(), "wq1");
        assertEquals(queueStatus.getPosition(), physicalSize + windowPosition);
        assertEquals(queueStatus.getQueueType(), QueueType.WINDOW);
        assertEquals(queueStatus.getWaitTime(), expectedWaitTime);
        assertEquals(queueStatus.getEmployee(), employee);
    }

    private QueueStatus testJoinVirtualQueueAtHead(long windowSize, long physicalSize,
                                                   int numStudents, int timeSpent) {
        employee.setNumRegisteredStudents(numStudents);
        employee.setTotalTimeSpent(timeSpent);

        Set<String> employees = Sets.newHashSet(Collections.singleton("e1"));
        VirtualQueueData virtualQueueData = new VirtualQueueData("vq1", employees);

        doNothing().when(validationService).checkValidCompanyId(anyString());
        doNothing().when(validationService).checkValidStudentId(anyString());
        doReturn(virtualQueueStatus).when(virtualQueueWorkflow).joinQueue(anyString(), any(),
                any());
        doReturn(virtualQueueData).when(virtualQueueWorkflow).getVirtualQueueData(anyString(),
                any());
        doReturn(physicalSize).when(physicalQueueWorkflow).size(anyString());
        doReturn(windowSize).when(windowQueueWorkflow).size(anyString());
        doReturn(employee).when(employeeHashOperations).get(anyString(), any());

        JoinQueueResponse response = queueService.joinVirtualQueue("c1", Role.SWE,
                student);

        verify(validationService).checkValidStudentId(anyString());
        verify(validationService).checkValidCompanyId(anyString());
        verify(virtualQueueWorkflow).joinQueue(anyString(), any(), any());

        assertNotNull(response);
        assertNotNull(response.getQueueStatus());

        assertEquals(response.getQueueStatus().getCompanyId(), "c1");
        assertEquals(response.getQueueStatus().getRole(), Role.SWE);

        return response.getQueueStatus();
    }

    @Test
    public void joinEmployeeQueueAtHead() {
        QueueStatus queueStatus = testJoinEmployeeQueue(1);
        assertEquals(queueStatus.getWaitTime(), 0);
    }

    @Test
    public void joinEmployeeQueueNotAtHead() {
        QueueStatus queueStatus = testJoinEmployeeQueue(2);
        assertNotEquals(queueStatus.getWaitTime(), 0);
    }

    private QueueStatus testJoinEmployeeQueue(int position) {
        physicalQueueStatus.setPosition(position);

        employee.setTotalTimeSpent(5);
        employee.setNumRegisteredStudents(1);

        doReturn(studentQueueStatus).when(windowQueueWorkflow).leaveQueue(anyString(), anyString());
        doReturn(physicalQueueStatus).when(physicalQueueWorkflow).joinQueue(anyString(), any(),
                any());

        JoinQueueResponse response = queueService.joinEmployeeQueue("e1", "s1");

        verify(windowQueueWorkflow).leaveQueue(anyString(), anyString());
        verify(physicalQueueWorkflow).joinQueue(anyString(), any(), any());

        assertNotNull(response);
        assertNotNull(response.getQueueStatus());

        assertEquals(response.getQueueStatus().getCompanyId(), "c1");
        assertEquals(response.getQueueStatus().getQueueId(), "pq1");
        assertEquals(response.getQueueStatus().getRole(), Role.SWE);
        assertEquals(response.getQueueStatus().getPosition(), position);
        assertEquals(response.getQueueStatus().getQueueType(), QueueType.PHYSICAL);
        assertEquals(response.getQueueStatus().getEmployee(), employee);

        return response.getQueueStatus();
    }

    @Test
    public void testLeaveQueueVirtual() {
        studentQueueStatus.setQueueType(QueueType.VIRTUAL);

        doReturn(studentQueueStatus).when(studentHashOperations).get(anyString(), any());
        doReturn(studentQueueStatus).when(virtualQueueWorkflow).leaveQueue(anyString(), anyString(),
                any());

        queueService.leaveQueue("c1", "s1", Role.SWE);

        verify(virtualQueueWorkflow).leaveQueue(anyString(), anyString(), any());
        verify(windowQueueWorkflow, never()).leaveQueue(anyString(), anyString());
        verify(physicalQueueWorkflow, never()).leaveQueue(anyString(), anyString());
    }

    @Test
    public void testLeaveQueueWindowWithNoHeadStudent() {
        studentQueueStatus.setQueueType(QueueType.WINDOW);
        studentQueueStatus.setEmployeeId("e1");

        doReturn(studentQueueStatus).when(studentHashOperations).get(anyString(), any());
        doReturn(studentQueueStatus).when(virtualQueueWorkflow).leaveQueue(anyString(), anyString(),
                any());
        doReturn(null).when(virtualQueueWorkflow).getStudentAtHead(anyString(), any());

        queueService.leaveQueue("c1", "s1", Role.SWE);

        verify(virtualQueueWorkflow, never()).leaveQueue(anyString(), anyString(), any());
        verify(windowQueueWorkflow).leaveQueue(anyString(), anyString());
        verify(physicalQueueWorkflow, never()).leaveQueue(anyString(), anyString());
    }

    @Test
    public void testLeaveQueueWindowWithHeadStudent() {
        studentQueueStatus.setQueueType(QueueType.WINDOW);
        studentQueueStatus.setEmployeeId("e1");

        doReturn(studentQueueStatus).when(studentHashOperations).get(anyString(), any());
        doReturn(studentQueueStatus).when(virtualQueueWorkflow).leaveQueue(anyString(), anyString(),
                any());
        doReturn(student).when(virtualQueueWorkflow).getStudentAtHead(anyString(), any());
        doReturn(windowQueueStatus).when(windowQueueWorkflow).joinQueue(anyString(), any(), any());

        queueService.leaveQueue("c1", "s1", Role.SWE);

        verify(virtualQueueWorkflow).leaveQueue(anyString(), anyString(), any());
        verify(windowQueueWorkflow).leaveQueue(anyString(), anyString());
        verify(windowQueueWorkflow).joinQueue(anyString(), any(), any());
        verify(physicalQueueWorkflow, never()).leaveQueue(anyString(), anyString());
    }

    @Test
    public void testLeaveQueuePhysicalWithNoHeadStudent() {
        studentQueueStatus.setQueueType(QueueType.PHYSICAL);
        studentQueueStatus.setEmployeeId("e1");

        doReturn(studentQueueStatus).when(studentHashOperations).get(anyString(), any());
        doReturn(studentQueueStatus).when(virtualQueueWorkflow).leaveQueue(anyString(), anyString(),
                any());
        doReturn(null).when(virtualQueueWorkflow).getStudentAtHead(anyString(), any());

        queueService.leaveQueue("c1", "s1", Role.SWE);

        verify(virtualQueueWorkflow, never()).leaveQueue(anyString(), anyString(), any());
        verify(windowQueueWorkflow, never()).leaveQueue(anyString(), anyString());
        verify(physicalQueueWorkflow).leaveQueue(anyString(), anyString());
    }

    @Test
    public void testLeaveQueuePhysicalWithHeadStudent() {
        studentQueueStatus.setQueueType(QueueType.PHYSICAL);
        studentQueueStatus.setEmployeeId("e1");

        doReturn(studentQueueStatus).when(studentHashOperations).get(anyString(), any());
        doReturn(studentQueueStatus).when(virtualQueueWorkflow).leaveQueue(anyString(), anyString(),
                any());
        doReturn(student).when(virtualQueueWorkflow).getStudentAtHead(anyString(), any());
        doReturn(windowQueueStatus).when(windowQueueWorkflow).joinQueue(anyString(), any(), any());

        queueService.leaveQueue("c1", "s1", Role.SWE);

        verify(virtualQueueWorkflow).leaveQueue(anyString(), anyString(), any());
        verify(windowQueueWorkflow, never()).leaveQueue(anyString(), anyString());
        verify(windowQueueWorkflow).joinQueue(anyString(), any(), any());
        verify(physicalQueueWorkflow).leaveQueue(anyString(), anyString());
    }
}

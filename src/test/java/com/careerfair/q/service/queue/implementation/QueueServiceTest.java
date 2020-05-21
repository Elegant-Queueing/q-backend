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
    private Employee employee;
    private StudentQueueStatus studentQueueStatus;

    @BeforeEach
    public void setupMocks() {
        MockitoAnnotations.initMocks(this);

        employee = new Employee("e1", "c1", Role.SWE);
        studentQueueStatus = new StudentQueueStatus("student1", "c1", "s1", Role.SWE);

        virtualQueueStatus = new QueueStatus("c1", "vq1", QueueType.VIRTUAL, Role.SWE);
        windowQueueStatus = new QueueStatus("c1", "wq1", QueueType.WINDOW, Role.SWE);
        windowQueueStatus.setEmployee(employee);

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
                new Student("s1", "student1"));

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

        assertEquals(windowSize + physicalSize, MAX_EMPLOYEE_QUEUE_SIZE);

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
        doReturn(physicalSize).when(physicalQueueWorkflow).size(anyString());
        doReturn(windowSize).when(windowQueueWorkflow).size(anyString());
        doReturn(employee).when(employeeHashOperations).get(anyString(), any());

        JoinQueueResponse response = queueService.joinVirtualQueue("c1", Role.SWE,
                new Student("s1", "student1"));

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
    public void testJoinVirtualQueueHeadWithEmployeeSpace() {
        int windowPosition = 1;
        long physicalSize = 1L;

        virtualQueueStatus.setPosition(1);
        windowQueueStatus.setPosition(windowPosition);

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
        doReturn(physicalSize).when(physicalQueueWorkflow).size(anyString());
        doReturn(studentQueueStatus).when(virtualQueueWorkflow).leaveQueue(anyString(), anyString(),
                any());
        doReturn(windowQueueStatus).when(windowQueueWorkflow).joinQueue(anyString(), any(), any());
        doReturn(employee).when(employeeHashOperations).get(anyString(), any());

        JoinQueueResponse response = queueService.joinVirtualQueue("c1", Role.SWE,
                new Student("s1", "student1"));

        verify(validationService).checkValidStudentId(anyString());
        verify(validationService).checkValidCompanyId(anyString());
        verify(virtualQueueWorkflow).joinQueue(anyString(), any(), any());

        assertNotNull(response);
        assertNotNull(response.getQueueStatus());

        assertEquals(response.getQueueStatus().getCompanyId(), "c1");
        assertEquals(response.getQueueStatus().getQueueId(), "wq1");
        assertEquals(response.getQueueStatus().getRole(), Role.SWE);
        assertEquals(response.getQueueStatus().getPosition(), physicalSize + windowPosition);
        assertEquals(response.getQueueStatus().getQueueType(), QueueType.WINDOW);
        assertEquals(response.getQueueStatus().getEmployee(), employee);
        assertNotEquals(response.getQueueStatus().getWaitTime(), 0);
    }
}

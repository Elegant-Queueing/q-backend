package com.careerfair.q.service.queue.implementation;

import com.careerfair.q.model.redis.Employee;
import com.careerfair.q.model.redis.Student;
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

    private QueueStatus queueStatus;
    private Employee employee;

    @BeforeEach
    public void setupMocks() {
        MockitoAnnotations.initMocks(this);

        queueStatus = new QueueStatus("c1", "vq1", QueueType.VIRTUAL, Role.SWE);
        employee = new Employee("e1", "c1", Role.SWE);

        when(employeeRedisTemplate.opsForHash()).thenReturn(employeeHashOperations);
        when(studentRedisTemplate.opsForHash()).thenReturn(studentHashOperations);
    }

    @Test
    public void testJoinVirtualQueue() {
        queueStatus.setPosition(1);
        employee.setTotalTimeSpent(5);
        employee.setNumRegisteredStudents(1);

        Set<String> employees = Sets.newHashSet(Collections.singleton("e1"));
        VirtualQueueData virtualQueueData = new VirtualQueueData("vq1", employees);

        doNothing().when(validationService).checkValidCompanyId(anyString());
        doNothing().when(validationService).checkValidStudentId(anyString());
        doReturn(queueStatus).when(virtualQueueWorkflow).joinQueue(anyString(), any(Role.class),
                any(Student.class));
        doReturn(virtualQueueData).when(virtualQueueWorkflow).getVirtualQueueData(anyString(),
                any(Role.class));
        doReturn(1L).when(physicalQueueWorkflow).size(anyString());
        doReturn(4L).when(windowQueueWorkflow).size(anyString());
        doReturn(employee).when(employeeHashOperations).get(anyString(), any());

        JoinQueueResponse response = queueService.joinVirtualQueue("c1", Role.SWE,
                new Student("s1", "student1"));

        verify(validationService).checkValidStudentId(anyString());
        verify(validationService).checkValidCompanyId(anyString());
        verify(virtualQueueWorkflow).joinQueue(anyString(), any(Role.class), any(Student.class));

        assertNotNull(response);
        assertNotNull(response.getQueueStatus());

        assertEquals(response.getQueueStatus().getCompanyId(), "c1");
        assertEquals(response.getQueueStatus().getQueueId(), "vq1");
        assertEquals(response.getQueueStatus().getRole(), Role.SWE);
        assertEquals(response.getQueueStatus().getPosition(), MAX_EMPLOYEE_QUEUE_SIZE + 1);
        assertEquals(response.getQueueStatus().getQueueType(), QueueType.VIRTUAL);
        assertNotEquals(response.getQueueStatus().getWaitTime(), 0);
        assertNull(response.getQueueStatus().getEmployee());
    }
}

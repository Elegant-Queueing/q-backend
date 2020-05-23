package com.careerfair.q.service.queue.implementation;

import com.careerfair.q.model.redis.Employee;
import com.careerfair.q.model.redis.Student;
import com.careerfair.q.model.redis.StudentQueueStatus;
import com.careerfair.q.model.redis.VirtualQueueData;
import com.careerfair.q.service.database.FirebaseService;
import com.careerfair.q.service.queue.response.*;
import com.careerfair.q.service.validation.ValidationService;
import com.careerfair.q.util.enums.QueueType;
import com.careerfair.q.util.enums.Role;
import com.careerfair.q.util.exception.InvalidRequestException;
import com.careerfair.q.workflow.queue.employee.physical.PhysicalQueueWorkflow;
import com.careerfair.q.workflow.queue.employee.window.WindowQueueWorkflow;
import com.careerfair.q.workflow.queue.virtual.VirtualQueueWorkflow;
import org.assertj.core.util.Lists;
import org.assertj.core.util.Sets;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.Spy;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.RedisTemplate;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Set;

import static com.careerfair.q.util.constant.Queue.INITIAL_TIME_SPENT;
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

    @Test
    public void testGetQueueStatusVirtual() {
        int position = 2;
        int numStudents = 1;
        long timeSpent = 5;
        int expectedWaitTime = (int) ((MAX_EMPLOYEE_QUEUE_SIZE + position - 1.) * timeSpent / numStudents);

        Set<String> employees = Sets.newHashSet(Collections.singleton("e1"));
        VirtualQueueData virtualQueueData = new VirtualQueueData("vq1", employees);

        employee.setTotalTimeSpent(timeSpent);
        employee.setNumRegisteredStudents(numStudents);
        studentQueueStatus.setQueueType(QueueType.VIRTUAL);
        virtualQueueStatus.setPosition(position);

        doReturn(studentQueueStatus).when(studentHashOperations).get(anyString(), any());
        doReturn(virtualQueueStatus).when(virtualQueueWorkflow).getQueueStatus(any());
        doReturn(virtualQueueData).when(virtualQueueWorkflow).getVirtualQueueData(anyString(),
                any());
        doReturn(employee).when(employeeHashOperations).get(anyString(), any());

        GetQueueStatusResponse response = queueService.getQueueStatus("s1");

        assertNotNull(response);
        assertNotNull(response.getQueueStatus());

        assertEquals(response.getQueueStatus().getCompanyId(), "c1");
        assertEquals(response.getQueueStatus().getRole(), Role.SWE);
        assertEquals(response.getQueueStatus().getQueueId(), "vq1");
        assertEquals(response.getQueueStatus().getPosition(), position + MAX_EMPLOYEE_QUEUE_SIZE);
        assertEquals(response.getQueueStatus().getQueueType(), QueueType.VIRTUAL);
        assertEquals(response.getQueueStatus().getWaitTime(), expectedWaitTime);
        assertNull(response.getQueueStatus().getEmployee());
    }

    @Test
    public void testGetQueueStatusWindow() {
        int position = 2;
        int numStudents = 1;
        int timeSpent = 5;

        long physicalSize = 4L;
        int expectedWaitTime = (int) ((physicalSize + position - 1.) * timeSpent / numStudents);

        employee.setTotalTimeSpent(timeSpent);
        employee.setNumRegisteredStudents(numStudents);
        studentQueueStatus.setQueueType(QueueType.WINDOW);
        windowQueueStatus.setPosition(position);

        doReturn(studentQueueStatus).when(studentHashOperations).get(anyString(), any());
        doReturn(windowQueueStatus).when(windowQueueWorkflow).getQueueStatus(any());
        doReturn(physicalSize).when(physicalQueueWorkflow).size(anyString());
        doReturn(employee).when(employeeHashOperations).get(anyString(), any());

        GetQueueStatusResponse response = queueService.getQueueStatus("s1");

        assertNotNull(response);
        assertNotNull(response.getQueueStatus());

        assertEquals(response.getQueueStatus().getCompanyId(), "c1");
        assertEquals(response.getQueueStatus().getRole(), Role.SWE);
        assertEquals(response.getQueueStatus().getQueueId(), "wq1");
        assertEquals(response.getQueueStatus().getPosition(), position + physicalSize);
        assertEquals(response.getQueueStatus().getQueueType(), QueueType.WINDOW);
        assertEquals(response.getQueueStatus().getWaitTime(), expectedWaitTime);
        assertEquals(response.getQueueStatus().getEmployee(), employee);
    }

    @Test
    public void testGetQueueStatusPhysical() {
        int position = 2;
        int numStudents = 1;
        int timeSpent = 5;

        int expectedWaitTime = (int) ((position - 1.) * timeSpent / numStudents);

        employee.setTotalTimeSpent(timeSpent);
        employee.setNumRegisteredStudents(numStudents);
        studentQueueStatus.setQueueType(QueueType.PHYSICAL);
        physicalQueueStatus.setPosition(position);

        doReturn(studentQueueStatus).when(studentHashOperations).get(anyString(), any());
        doReturn(physicalQueueStatus).when(physicalQueueWorkflow).getQueueStatus(any());
        doReturn(employee).when(employeeHashOperations).get(anyString(), any());

        GetQueueStatusResponse response = queueService.getQueueStatus("s1");

        assertNotNull(response);
        assertNotNull(response.getQueueStatus());

        assertEquals(response.getQueueStatus().getCompanyId(), "c1");
        assertEquals(response.getQueueStatus().getRole(), Role.SWE);
        assertEquals(response.getQueueStatus().getQueueId(), "pq1");
        assertEquals(response.getQueueStatus().getPosition(), position);
        assertEquals(response.getQueueStatus().getQueueType(), QueueType.PHYSICAL);
        assertEquals(response.getQueueStatus().getWaitTime(), expectedWaitTime);
        assertEquals(response.getQueueStatus().getEmployee(), employee);
    }

    @Test
    public void testPauseQueue() {
        EmployeeQueueData employeeQueueData = new EmployeeQueueData(Lists.newArrayList(), 0,
                INITIAL_TIME_SPENT);

        doNothing().when(virtualQueueWorkflow).pauseQueueForEmployee(anyString());
        doReturn(employeeQueueData).when(physicalQueueWorkflow).getEmployeeQueueData(anyString());

        PauseQueueResponse response = queueService.pauseQueue("e1");

        assertNotNull(response);
        assertNotNull(response.getEmployeeQueueData());

        assertEquals(response.getEmployeeQueueData().getStudents(), Lists.newArrayList());
        assertEquals(response.getEmployeeQueueData().getAverageTimePerStudent(), INITIAL_TIME_SPENT,
                0.0001);
        assertEquals(response.getEmployeeQueueData().getNumRegisteredStudents(), 0);
    }

    @Test
    public void testRegisterStudentWithNoVirtualStudent() {
        EmployeeQueueData employeeQueueData = new EmployeeQueueData(Lists.newArrayList(), 1, 10);

        doReturn(employeeQueueData).when(physicalQueueWorkflow).registerStudent(anyString(),
                anyString());
        doNothing().when(firebaseService).registerStudent(anyString(), anyString());
        doReturn(employee).when(employeeHashOperations).get(anyString(), any());
        doReturn(null).when(virtualQueueWorkflow).getStudentAtHead(anyString(), any());

        RemoveStudentResponse response = queueService.registerStudent(anyString(), anyString());

        verify(firebaseService).registerStudent(anyString(), anyString());
        verify(virtualQueueWorkflow, never()).leaveQueue(anyString(), anyString(), any());
        verify(windowQueueWorkflow, never()).joinQueue(anyString(), any(), any());

        assertNotNull(response);
        assertNotNull(response.getEmployeeQueueData());

        assertEquals(response.getEmployeeQueueData().getNumRegisteredStudents(), 1);
        assertEquals(response.getEmployeeQueueData().getAverageTimePerStudent(), 10, 0.001);
    }

    @Test
    public void testRegisterStudentWithVirtualStudent() {
        EmployeeQueueData employeeQueueData = new EmployeeQueueData(Lists.newArrayList(), 1, 10);

        doReturn(employeeQueueData).when(physicalQueueWorkflow).registerStudent(anyString(),
                anyString());
        doNothing().when(firebaseService).registerStudent(anyString(), anyString());
        doReturn(employee).when(employeeHashOperations).get(anyString(), any());
        doReturn(student).when(virtualQueueWorkflow).getStudentAtHead(anyString(), any());
        doReturn(studentQueueStatus).when(virtualQueueWorkflow).leaveQueue(anyString(), anyString(),
                any());
        doReturn(windowQueueStatus).when(windowQueueWorkflow).joinQueue(anyString(), any(), any());

        RemoveStudentResponse response = queueService.registerStudent(anyString(), anyString());

        verify(firebaseService).registerStudent(anyString(), anyString());
        verify(virtualQueueWorkflow).leaveQueue(anyString(), anyString(), any());
        verify(windowQueueWorkflow).joinQueue(anyString(), any(), any());

        assertNotNull(response);
        assertNotNull(response.getEmployeeQueueData());

        assertEquals(response.getEmployeeQueueData().getNumRegisteredStudents(), 1);
        assertEquals(response.getEmployeeQueueData().getAverageTimePerStudent(), 10, 0.001);
    }

    @Test
    public void testSkipStudentWithNoVirtualStudent() {
        EmployeeQueueData employeeQueueData = new EmployeeQueueData(Lists.newArrayList(), 0,
                INITIAL_TIME_SPENT);

        doReturn(employeeQueueData).when(physicalQueueWorkflow).skipStudent(anyString(),
                anyString());
        doReturn(employee).when(employeeHashOperations).get(anyString(), any());
        doReturn(null).when(virtualQueueWorkflow).getStudentAtHead(anyString(), any());

        RemoveStudentResponse response = queueService.skipStudent(anyString(), anyString());

        verify(firebaseService, never()).registerStudent(anyString(), anyString());
        verify(virtualQueueWorkflow, never()).leaveQueue(anyString(), anyString(), any());
        verify(windowQueueWorkflow, never()).joinQueue(anyString(), any(), any());

        assertNotNull(response);
        assertNotNull(response.getEmployeeQueueData());

        assertEquals(response.getEmployeeQueueData().getNumRegisteredStudents(), 0);
        assertEquals(response.getEmployeeQueueData().getAverageTimePerStudent(), INITIAL_TIME_SPENT,
                0.001);
    }

    @Test
    public void testSkipStudentWithVirtualStudent() {
        EmployeeQueueData employeeQueueData = new EmployeeQueueData(Lists.newArrayList(), 0,
                INITIAL_TIME_SPENT);

        doReturn(employeeQueueData).when(physicalQueueWorkflow).skipStudent(anyString(),
                anyString());
        doReturn(employee).when(employeeHashOperations).get(anyString(), any());
        doReturn(student).when(virtualQueueWorkflow).getStudentAtHead(anyString(), any());
        doReturn(studentQueueStatus).when(virtualQueueWorkflow).leaveQueue(anyString(), anyString(),
                any());
        doReturn(windowQueueStatus).when(windowQueueWorkflow).joinQueue(anyString(), any(), any());

        RemoveStudentResponse response = queueService.skipStudent(anyString(), anyString());

        verify(firebaseService, never()).registerStudent(anyString(), anyString());
        verify(virtualQueueWorkflow).leaveQueue(anyString(), anyString(), any());
        verify(windowQueueWorkflow).joinQueue(anyString(), any(), any());

        assertNotNull(response);
        assertNotNull(response.getEmployeeQueueData());

        assertEquals(response.getEmployeeQueueData().getNumRegisteredStudents(), 0);
        assertEquals(response.getEmployeeQueueData().getAverageTimePerStudent(), INITIAL_TIME_SPENT,
                0.001);
    }

    @Test
    public void testGetEmployeeQueueData() {
        EmployeeQueueData employeeQueueData = new EmployeeQueueData(Lists.newArrayList(), 0,
                INITIAL_TIME_SPENT);

        doReturn(employeeQueueData).when(physicalQueueWorkflow).getEmployeeQueueData(anyString());

        GetEmployeeQueueDataResponse response = queueService.getEmployeeQueueData("e1");

        assertNotNull(response);
        assertNotNull(response.getEmployeeQueueData());

        assertEquals(response.getEmployeeQueueData().getNumRegisteredStudents(), 0);
        assertEquals(response.getEmployeeQueueData().getAverageTimePerStudent(), INITIAL_TIME_SPENT,
                0.001);
    }

    @Test
    public void testGetAllEmployees() {
        List<Employee> employees = Lists.newArrayList(employee);
        doReturn(employees).when(employeeHashOperations).values(anyString());

        List<Employee> response = queueService.getAllEmployees();

        assertNotNull(response);
        assertEquals(response.size(), 1);
    }

    @Test
    public void testGetOverallTimeEmployeesExist() {
        long virtualSize = 50L;
        long windowSize = 1L;
        long physicalSize = 2L;
        int numStudents = 1;
        long timeSpent = 5L;

        employee.setNumRegisteredStudents(numStudents);
        employee.setTotalTimeSpent(timeSpent);

        int waitTime = (int) ((physicalSize + windowSize + virtualSize - 1.) * timeSpent /
                numStudents);

        Set<String> employeeIds = Sets.newHashSet(Collections.singleton("e1"));
        VirtualQueueData virtualQueueData = new VirtualQueueData("vq1", employeeIds);

        doReturn(virtualQueueData).when(virtualQueueWorkflow).getVirtualQueueData(anyString(),
                any());
        doReturn(virtualSize).when(virtualQueueWorkflow).size(anyString(), any());
        doReturn(windowSize).when(windowQueueWorkflow).size(anyString());
        doReturn(physicalSize).when(physicalQueueWorkflow).size(anyString());
        doReturn(employee).when(employeeHashOperations).get(anyString(), any());

        int result = queueService.getOverallWaitTime("c1", Role.SWE);

        assertEquals(result, waitTime);
    }

    @Test
    public void testGetOverallTimeNoEmployeeExist() {
        doThrow(new InvalidRequestException("error")).when(virtualQueueWorkflow)
                .getVirtualQueueData(anyString(), any());

        try {
            queueService.getOverallWaitTime("c1", Role.SWE);
            fail();
        } catch (InvalidRequestException ex) {
            assertEquals(ex.getMessage(), "error");
        }
    }

    @Test
    public void testGetVirtualQueueSize() {
        doReturn(1L).when(virtualQueueWorkflow).size(anyString(), any());

        long size = queueService.getVirtualQueueSize("c1", Role.SWE);

        assertEquals(size, 1L);
    }

    @Test
    public void testGetEmployeeQueueSize() {
        doReturn(1L).when(windowQueueWorkflow).size(anyString());
        doReturn(2L).when(windowQueueWorkflow).size(anyString());

        long size = queueService.getEmployeeQueueSpace("e1");

        assertEquals(size, 3L);
        assertTrue(size <= MAX_EMPLOYEE_QUEUE_SIZE);
    }

    @Test
    public void testRemoveStudentFromQueueWithNoStudentAtHead() {
        doReturn(employee).when(employeeHashOperations).get(anyString(), any());
        doReturn(null).when(virtualQueueWorkflow).getStudentAtHead(anyString(), any());

        EmployeeQueueData employeeQueueData = new EmployeeQueueData(Lists.newArrayList(), 0, 1);

        RemoveStudentResponse response = queueService.removeStudentFromQueue("e1",
                employeeQueueData);

        assertNotNull(response);
        assertNotNull(response.getEmployeeQueueData());

        assertEquals(response.getEmployeeQueueData().getNumRegisteredStudents(), 0);
        assertEquals(response.getEmployeeQueueData().getAverageTimePerStudent(), 1, 0.001);
        assertEquals(response.getEmployeeQueueData().getStudents().size(), 0);
    }

    @Test
    public void testRemoveStudentFromQueueWithStudentAtHead() {
        doReturn(employee).when(employeeHashOperations).get(anyString(), any());
        doReturn(student).when(virtualQueueWorkflow).getStudentAtHead(anyString(), any());
        doReturn(studentQueueStatus).when(virtualQueueWorkflow).leaveQueue(anyString(), anyString(),
                any());
        doReturn(windowQueueStatus).when(windowQueueWorkflow).joinQueue(anyString(), any(), any());

        EmployeeQueueData employeeQueueData = new EmployeeQueueData(Lists.newArrayList(), 0, 1);

        RemoveStudentResponse response = queueService.removeStudentFromQueue("e1",
                employeeQueueData);

        assertNotNull(response);
        assertNotNull(response.getEmployeeQueueData());

        assertEquals(response.getEmployeeQueueData().getNumRegisteredStudents(), 0);
        assertEquals(response.getEmployeeQueueData().getAverageTimePerStudent(), 1, 0.001);
        assertEquals(response.getEmployeeQueueData().getStudents().size(), 0);
    }

    @Test
    public void testGetStudentQueueStatusPresent() {
        doReturn(studentQueueStatus).when(studentHashOperations).get(anyString(), any());

        StudentQueueStatus response = queueService.getStudentQueueStatus("s1");

        assertNotNull(response);

        assertEquals(response.getName(), "student1");
        assertEquals(response.getCompanyId(), "c1");
        assertEquals(response.getStudentId(), "s1");
        assertEquals(response.getRole(), Role.SWE);
    }

    @Test
    public void testGetStudentQueueStatusNotPresent() {
        doReturn(null).when(studentHashOperations).get(anyString(), any());

        try {
            queueService.getStudentQueueStatus("s1");
            fail();
        } catch (InvalidRequestException ex) {
            assertEquals(ex.getMessage(), "Student with id=s1 not present in any queue");
        }
    }

    @Test
    public void testGetEmployeeWithMostQueueSpace() {
        Set<String> employees = Sets.newHashSet(Arrays.asList("e1", "e2"));
        VirtualQueueData virtualQueueData = new VirtualQueueData("vq1", employees);

        doReturn(virtualQueueData).when(virtualQueueWorkflow).getVirtualQueueData(anyString(),
                any());
        doReturn(1L, 2L).when(physicalQueueWorkflow).size(anyString());
        doReturn(0L, 2L).when(windowQueueWorkflow).size(anyString());

        String response = queueService.getEmployeeWithMostQueueSpace("c1", Role.SWE);

        assertEquals(response, "e1");
    }

    @Test
    public void testGetEmployeeWithMostQueueSpaceNoEmployee() {
        doThrow(new InvalidRequestException("error")).when(virtualQueueWorkflow)
                .getVirtualQueueData(anyString(), any());

        try {
            queueService.getEmployeeWithMostQueueSpace("c1", Role.SWE);
            fail();
        } catch (InvalidRequestException ex) {
            assertEquals(ex.getMessage(), "error");
        }
    }

    @Test
    public void testGetEmployeeQueueSpace() {
        doReturn(1L).when(physicalQueueWorkflow).size(anyString());
        doReturn(1L).when(windowQueueWorkflow).size(anyString());

        long size = queueService.getEmployeeQueueSpace("e1");

        assertEquals(size, MAX_EMPLOYEE_QUEUE_SIZE - 1L - 1L);
    }

    @Test
    public void testShiftStudentToWindow() {
        doReturn(studentQueueStatus).when(virtualQueueWorkflow).leaveQueue(anyString(), anyString(),
                any());
        doReturn(windowQueueStatus).when(windowQueueWorkflow).joinQueue(anyString(), any(), any());

        QueueStatus queueStatus = queueService.shiftStudentToWindow("c1", "e1", Role.SWE, student);

        assertNotNull(queueStatus);

        assertEquals(queueStatus.getQueueId(), "wq1");
        assertEquals(queueStatus.getRole(), Role.SWE);
        assertEquals(queueStatus.getQueueType(), QueueType.WINDOW);
        assertEquals(queueStatus.getEmployee(), employee);
    }

    @Test
    public void testGetEmployeeWithId() {
        Employee employee = mock(Employee.class);
        doReturn(employee).when(employeeHashOperations).get(anyString(), any());

        Employee result = queueService.getEmployeeWithId("e1");

        assertEquals(result, employee);
    }

    @Test
    public void testGetEmployeeWithIdBad() {
        doReturn(null).when(employeeHashOperations).get(anyString(), any());

        try {
            queueService.getEmployeeWithId("e1");
            fail();
        } catch (InvalidRequestException ex) {
            assertEquals(ex.getMessage(), "No such employee with employee id=e1 exists");
        }
    }

    @Test
    public void testSetOverallPositionAndWaitTimeVirtual() {
        int position = 2;
        virtualQueueStatus.setPosition(position);

        int numStudents = 1;
        long timeSpent = 5L;
        employee.setTotalTimeSpent(timeSpent);
        employee.setNumRegisteredStudents(numStudents);

        Set<String> employees = Sets.newHashSet(Collections.singleton("e1"));
        VirtualQueueData virtualQueueData = new VirtualQueueData("vq1", employees);

        int expectedWaitTime = (int) (((position + MAX_EMPLOYEE_QUEUE_SIZE - 1.) * timeSpent /
                numStudents) / employees.size());

        doReturn(virtualQueueData).when(virtualQueueWorkflow).getVirtualQueueData(anyString(),
                any());
        doReturn(employee).when(employeeHashOperations).get(anyString(), any());

        queueService.setOverallPositionAndWaitTime(virtualQueueStatus);

        verify(virtualQueueWorkflow).getVirtualQueueData(anyString(), any());

        assertEquals(virtualQueueStatus.getCompanyId(), "c1");
        assertEquals(virtualQueueStatus.getQueueId(), "vq1");
        assertEquals(virtualQueueStatus.getRole(), Role.SWE);
        assertEquals(virtualQueueStatus.getPosition(), MAX_EMPLOYEE_QUEUE_SIZE + position);
        assertEquals(virtualQueueStatus.getQueueType(), QueueType.VIRTUAL);
        assertEquals(virtualQueueStatus.getWaitTime(), expectedWaitTime);
        assertNull(virtualQueueStatus.getEmployee());
    }

    @Test
    public void testSetOverallPositionAndWaitTimeWindow() {
        int position = 2;
        windowQueueStatus.setPosition(position);

        int numStudents = 1;
        long timeSpent = 5L;
        employee.setTotalTimeSpent(timeSpent);
        employee.setNumRegisteredStudents(numStudents);

        long physicalSize = 1L;
        int expectedWaitTime = (int) ((position + physicalSize - 1.) * timeSpent / numStudents);

        doReturn(physicalSize).when(physicalQueueWorkflow).size(anyString());

        queueService.setOverallPositionAndWaitTime(windowQueueStatus);

        verify(physicalQueueWorkflow).size(anyString());

        assertEquals(windowQueueStatus.getCompanyId(), "c1");
        assertEquals(windowQueueStatus.getQueueId(), "wq1");
        assertEquals(windowQueueStatus.getRole(), Role.SWE);
        assertEquals(windowQueueStatus.getPosition(), physicalSize + position);
        assertEquals(windowQueueStatus.getQueueType(), QueueType.WINDOW);
        assertEquals(windowQueueStatus.getWaitTime(), expectedWaitTime);
        assertEquals(windowQueueStatus.getEmployee(), employee);
    }

    @Test
    public void testSetOverallPositionAndWaitTimePhysical() {
        int position = 2;
        physicalQueueStatus.setPosition(position);

        int numStudents = 1;
        long timeSpent = 5L;
        employee.setTotalTimeSpent(timeSpent);
        employee.setNumRegisteredStudents(numStudents);

        int expectedWaitTime = (int) ((position - 1.) * timeSpent / numStudents);

        queueService.setOverallPositionAndWaitTime(physicalQueueStatus);

        assertEquals(physicalQueueStatus.getCompanyId(), "c1");
        assertEquals(physicalQueueStatus.getQueueId(), "pq1");
        assertEquals(physicalQueueStatus.getRole(), Role.SWE);
        assertEquals(physicalQueueStatus.getPosition(), position);
        assertEquals(physicalQueueStatus.getQueueType(), QueueType.PHYSICAL);
        assertEquals(physicalQueueStatus.getWaitTime(), expectedWaitTime);
        assertEquals(physicalQueueStatus.getEmployee(), employee);
    }

    @Test
    public void testGetVirtualQueueWaitTime() {
        int position = 5;
        int numStudents = 1;
        long timeSpent = 5L;
        employee.setTotalTimeSpent(timeSpent);
        employee.setNumRegisteredStudents(numStudents);

        Set<String> employees = Sets.newHashSet(Collections.singleton("e1"));
        VirtualQueueData virtualQueueData = new VirtualQueueData("vq1", employees);

        doReturn(virtualQueueData).when(virtualQueueWorkflow).getVirtualQueueData(anyString(),
                any());
        doReturn(employee).when(employeeHashOperations).get(anyString(), any());

        int expectedWaitTime = (int) (((position - 1.) * timeSpent / numStudents) /
                employees.size());

        int waitTime = queueService.getVirtualQueueWaitTime("c1", Role.SWE, position);

        assertEquals(waitTime, expectedWaitTime);
    }

    @Test
    public void testGetEmployeeQueueWaitTime() {
        int numStudents = 1;
        long timeSpent = 5L;
        employee.setTotalTimeSpent(timeSpent);
        employee.setNumRegisteredStudents(numStudents);

        int position = 3;
        int expectedWaitTime = (int) ((position - 1.) * timeSpent / numStudents);

        int waitTime = queueService.getEmployeeQueueWaitTime(employee, position);

        assertEquals(waitTime, expectedWaitTime);
    }

    @Test
    public void testCalcEmployeeAverageTimeNoStudentRegistered() {
        testCalcEmployeeAverageTime(0);
    }

    @Test
    public void testCalcEmployeeAverageTimeStudentRegistered() {
        testCalcEmployeeAverageTime(2);
    }

    private void testCalcEmployeeAverageTime(int numRegistered) {
        long timeSpent = 5L;
        employee.setTotalTimeSpent(timeSpent);
        employee.setNumRegisteredStudents(numRegistered);

        double expectedWaitTime = timeSpent * 1. / Math.max(numRegistered, 1);

        double waitTime = queueService.calcEmployeeAverageTime(employee);

        assertEquals(waitTime, expectedWaitTime, 0.001);
    }

    @Test
    public void testGetIndexFromPosition() {
        testGetIndexFromPosition(0);
        testGetIndexFromPosition(2);
    }

    private void testGetIndexFromPosition(int position) {
        assertEquals(queueService.getIndexFromPosition(position), Math.max(position - 1, 0));
    }
}

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

    private void validateVirtualQueueStatus(QueueStatus queueStatus, int position,
                                            int expectedWaitTime) {
        assertEquals(queueStatus.getCompanyId(), "c1");
        assertEquals(queueStatus.getQueueId(), "vq1");
        assertEquals(queueStatus.getRole(), Role.SWE);
        assertEquals(queueStatus.getPosition(), MAX_EMPLOYEE_QUEUE_SIZE + position);
        assertEquals(queueStatus.getQueueType(), QueueType.VIRTUAL);
        assertEquals(queueStatus.getWaitTime(), expectedWaitTime);
        assertNull(queueStatus.getEmployee());
    }

    private void validateWindowQueueStatus(QueueStatus queueStatus, int position, long physicalSize,
                                           int expectedWaitTime, Employee employee) {
        assertEquals(queueStatus.getCompanyId(), "c1");
        assertEquals(queueStatus.getQueueId(), "wq1");
        assertEquals(queueStatus.getRole(), Role.SWE);
        assertEquals(queueStatus.getPosition(), position + physicalSize);
        assertEquals(queueStatus.getQueueType(), QueueType.WINDOW);
        assertEquals(queueStatus.getWaitTime(), expectedWaitTime);
        assertEquals(queueStatus.getEmployee(), employee);
    }

    private void validatePhysicalQueueStatus(QueueStatus queueStatus, int position,
                                             int expectedWaitTime) {
        assertEquals(queueStatus.getCompanyId(), "c1");
        assertEquals(queueStatus.getQueueId(), "pq1");
        assertEquals(queueStatus.getRole(), Role.SWE);
        assertEquals(queueStatus.getPosition(), position);
        assertEquals(queueStatus.getQueueType(), QueueType.PHYSICAL);
        assertEquals(queueStatus.getEmployee(), employee);
        assertEquals(queueStatus.getWaitTime(), expectedWaitTime);
    }

    private void validateEmployeeQueueData(EmployeeQueueData employeeQueueData, int numStudents,
                                           long timeSpent) {
        assertEquals(employeeQueueData.getAverageTimePerStudent(), timeSpent, 0.001);
        assertEquals(employeeQueueData.getNumRegisteredStudents(), numStudents);
    }

    @Test
    public void testJoinVirtualQueueNotHead() {
        int position = 2;
        virtualQueueStatus.setPosition(position);

        int timeSpent = 5;
        int numStudents = 2;
        employee.setTotalTimeSpent(timeSpent);
        employee.setNumRegisteredStudents(numStudents);

        Set<String> employees = Sets.newHashSet(Collections.singleton("e1"));
        VirtualQueueData virtualQueueData = new VirtualQueueData("vq1", employees);

        int expectedWaitTime = (int) (((position + MAX_EMPLOYEE_QUEUE_SIZE - 1.) * timeSpent /
                numStudents) / employees.size());

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
        validateVirtualQueueStatus(response.getQueueStatus(), position, expectedWaitTime);
    }

    @Test
    public void testJoinVirtualQueueHeadWithNoEmployeeSpace() {
        int position = 1;
        long windowSize = 4L;
        long physicalSize = 1L;

        int numStudents = 2;
        int timeSpent = 5;
        int expectedWaitTime = (int) ((position + windowSize + physicalSize - 1.) * timeSpent /
                numStudents);

        assertEquals(windowSize + physicalSize, MAX_EMPLOYEE_QUEUE_SIZE);

        virtualQueueStatus.setPosition(position);

        QueueStatus queueStatus = testJoinVirtualQueueAtHead(windowSize, physicalSize, numStudents,
                timeSpent);
        validateVirtualQueueStatus(queueStatus, position, expectedWaitTime);
    }

    @Test
    public void testJoinVirtualQueueHeadWithEmployeeSpace() {
        int windowPosition = 3;
        long physicalSize = 0L;

        int numStudents = 1;
        int timeSpent = 5;
        int expectedWaitTime = (int) ((windowPosition + physicalSize - 1.) * timeSpent /
                numStudents);

        virtualQueueStatus.setPosition(1);
        windowQueueStatus.setPosition(windowPosition);

        doReturn(studentQueueStatus).when(virtualQueueWorkflow).leaveQueue(anyString(), anyString(),
                any());
        doReturn(windowQueueStatus).when(windowQueueWorkflow).joinQueue(anyString(), any(), any());

        QueueStatus queueStatus = testJoinVirtualQueueAtHead(0L, physicalSize, numStudents,
                timeSpent);
        validateWindowQueueStatus(queueStatus, windowPosition, physicalSize, expectedWaitTime,
                employee);
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

        return response.getQueueStatus();
    }

    @Test
    public void testJoinEmployeeQueueAtHead() {
        testJoinEmployeeQueue(1);
    }

    @Test
    public void testJoinEmployeeQueueNotAtHead() {
        testJoinEmployeeQueue(2);
    }

    @Test
    public void testJoinEmployeeQueueTooLate() {
        doReturn(studentQueueStatus).when(windowQueueWorkflow).leaveQueue(anyString(), anyString());
        doThrow(new InvalidRequestException("error")).when(physicalQueueWorkflow).joinQueue(
                anyString(), any(), any());

        try {
            queueService.joinEmployeeQueue("e1", "s1");
            fail();
        } catch (InvalidRequestException ex) {
            assertEquals(ex.getMessage(), "error");
        }
    }

    private void testJoinEmployeeQueue(int position) {
        physicalQueueStatus.setPosition(position);

        long timeSpent = 5L;
        int numStudents = 2;
        employee.setTotalTimeSpent(timeSpent);
        employee.setNumRegisteredStudents(numStudents);

        int expectedWaitTime = (int) ((position - 1.) * timeSpent / numStudents);

        doReturn(studentQueueStatus).when(windowQueueWorkflow).leaveQueue(anyString(), anyString());
        doReturn(physicalQueueStatus).when(physicalQueueWorkflow).joinQueue(anyString(), any(),
                any());

        JoinQueueResponse response = queueService.joinEmployeeQueue("e1", "s1");

        verify(windowQueueWorkflow).leaveQueue(anyString(), anyString());
        verify(physicalQueueWorkflow).joinQueue(anyString(), any(), any());

        assertNotNull(response);
        assertNotNull(response.getQueueStatus());
        validatePhysicalQueueStatus(response.getQueueStatus(), position, expectedWaitTime);
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
    public void testLeaveQueueDefaultQueue() {
        try {
            studentQueueStatus.setQueueType(QueueType.DEFAULT);
            doReturn(studentQueueStatus).when(studentHashOperations).get(anyString(), any());
            queueService.leaveQueue("c1", "s1", Role.SWE);
            fail();
        } catch (InvalidRequestException ex) {
            assertEquals(ex.getMessage(), "No such QueueType exists");
        }
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
        validateVirtualQueueStatus(response.getQueueStatus(), position, expectedWaitTime);
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
        validateWindowQueueStatus(response.getQueueStatus(), position, physicalSize,
                expectedWaitTime, employee);
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
        validatePhysicalQueueStatus(response.getQueueStatus(), position, expectedWaitTime);
    }

    @Test
    public void testGetQueueStatusDefault() {
        try {
            studentQueueStatus.setQueueType(QueueType.DEFAULT);
            doReturn(studentQueueStatus).when(studentHashOperations).get(anyString(), any());
            queueService.getQueueStatus("s1");
            fail();
        } catch (InvalidRequestException ex) {
            assertEquals(ex.getMessage(), "No such QueueType exists");
        }
    }

    @Test
    public void testAddQueueNewEmployee() {
        doNothing().when(employeeHashOperations).put(anyString(), any(), any());
        doReturn("vq1").when(virtualQueueWorkflow).addQueue("c1", "e1", Role.SWE);
        doReturn(employee).when(windowQueueWorkflow).addQueue(anyString());
        doReturn(employee).when(physicalQueueWorkflow).addQueue(anyString());
        doReturn(null).when(virtualQueueWorkflow).getStudentAtHead(anyString(), any());

        testAddQueue(null, 0L, 0L);

        verify(employeeHashOperations).put(anyString(), any(), any());
        verify(windowQueueWorkflow).addQueue(anyString());
        verify(physicalQueueWorkflow).addQueue(anyString());
    }

    @Test
    public void testAddQueueReturningEmployeeNoSpace() {
        employee.setWindowQueueId("wq1");
        employee.setPhysicalQueueId("pq1");

        testAddQueue(employee, 2L, 3L);

        verify(employeeHashOperations, never()).put(anyString(), any(), any());
        verify(windowQueueWorkflow, never()).addQueue(anyString());
        verify(physicalQueueWorkflow, never()).addQueue(anyString());
        verify(virtualQueueWorkflow, never()).getStudentAtHead(anyString(), any());
    }

    @Test
    public void testAddQueueReturningEmployeeSpace() {
        employee.setWindowQueueId("wq1");
        employee.setPhysicalQueueId("pq1");

        doReturn(student).when(virtualQueueWorkflow).getStudentAtHead(anyString(), any());
        doReturn(studentQueueStatus).when(virtualQueueWorkflow).leaveQueue(anyString(), anyString(),
                any());
        doReturn(windowQueueStatus).when(windowQueueWorkflow).joinQueue(anyString(), any(), any());

        testAddQueue(employee, 2L, 2L);

        verify(employeeHashOperations, never()).put(anyString(), any(), any());
        verify(windowQueueWorkflow, never()).addQueue(anyString());
        verify(physicalQueueWorkflow, never()).addQueue(anyString());
        verify(virtualQueueWorkflow).getStudentAtHead(anyString(), any());
    }

    private void testAddQueue(Employee employee, long windowSize, long physicalSize) {
        int numStudents = 20;
        long timeSpent = 250L;
        EmployeeQueueData employeeQueueData = new EmployeeQueueData(Lists.newArrayList(),
                numStudents, timeSpent);

        doNothing().when(validationService).checkValidCompanyId(anyString());
        doNothing().when(validationService).checkValidEmployeeId(anyString());
        doNothing().when(validationService).checkEmployeeAssociations(anyString(), anyString(),
                any());
        doReturn(employee).when(employeeHashOperations).get(anyString(), any());
        doReturn("vq1").when(virtualQueueWorkflow).addQueue("c1", "e1", Role.SWE);
        doReturn(windowSize).when(windowQueueWorkflow).size(anyString());
        doReturn(physicalSize).when(physicalQueueWorkflow).size(anyString());
        doReturn(employeeQueueData).when(physicalQueueWorkflow).getEmployeeQueueData(anyString());

        AddQueueResponse response = queueService.addQueue("c1", "e1", Role.SWE);

        verify(validationService).checkValidCompanyId(anyString());
        verify(validationService).checkValidEmployeeId(anyString());
        verify(validationService).checkEmployeeAssociations(anyString(), anyString(), any());

        assertNotNull(response);
        assertNotNull(response.getEmployeeQueueData());
        validateEmployeeQueueData(employeeQueueData, numStudents, timeSpent);
    }

    @Test
    public void testAddQueueEmployeeHasQueueOpen() {
        employee.setVirtualQueueId("vq1");

        doNothing().when(validationService).checkValidCompanyId(anyString());
        doNothing().when(validationService).checkValidEmployeeId(anyString());
        doNothing().when(validationService).checkEmployeeAssociations(anyString(), anyString(),
                any());
        doReturn(employee).when(employeeHashOperations).get(anyString(), any());
        doThrow(new InvalidRequestException("error")).when(virtualQueueWorkflow).addQueue("c1",
                "e1", Role.SWE);

        try {
            queueService.addQueue("c1", "e1", Role.SWE);
            fail();
        } catch (InvalidRequestException ex) {
            assertEquals(ex.getMessage(), "error");
        }
    }

    @Test
    public void testPauseQueue() {
        int numStudents = 0;
        long timeSpent = INITIAL_TIME_SPENT;
        EmployeeQueueData employeeQueueData = new EmployeeQueueData(Lists.newArrayList(),
                numStudents, timeSpent);

        doNothing().when(virtualQueueWorkflow).pauseQueueForEmployee(anyString());
        doReturn(employeeQueueData).when(physicalQueueWorkflow).getEmployeeQueueData(anyString());

        PauseQueueResponse response = queueService.pauseQueue("e1");

        assertNotNull(response);
        assertNotNull(response.getEmployeeQueueData());

        assertEquals(response.getEmployeeQueueData().getStudents().size(), 0);
        validateEmployeeQueueData(employeeQueueData, numStudents, timeSpent);
    }

    @Test
    public void testRegisterStudentWithNoVirtualStudent() {
        int numStudents = 2;
        long timeSpent = 10L;
        EmployeeQueueData employeeQueueData = new EmployeeQueueData(Lists.newArrayList(),
                numStudents, timeSpent);

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
        validateEmployeeQueueData(employeeQueueData, numStudents, timeSpent);
    }

    @Test
    public void testRegisterStudentWithVirtualStudent() {
        int numStudents = 1;
        long timeSpent = 7L;
        EmployeeQueueData employeeQueueData = new EmployeeQueueData(Lists.newArrayList(),
                numStudents, timeSpent);

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
        validateEmployeeQueueData(employeeQueueData, numStudents, timeSpent);
    }

    @Test
    public void testSkipStudentWithNoVirtualStudent() {
        int numStudents = 0;
        long timeSpent = INITIAL_TIME_SPENT;
        EmployeeQueueData employeeQueueData = new EmployeeQueueData(Lists.newArrayList(),
                numStudents, timeSpent);

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
        validateEmployeeQueueData(employeeQueueData, numStudents, timeSpent);
    }

    @Test
    public void testSkipStudentWithVirtualStudent() {
        int numStudents = 0;
        long timeSpent = INITIAL_TIME_SPENT;
        EmployeeQueueData employeeQueueData = new EmployeeQueueData(Lists.newArrayList(),
                numStudents, timeSpent);

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
        validateEmployeeQueueData(employeeQueueData, numStudents, timeSpent);
    }

    @Test
    public void testGetEmployeeQueueData() {
        int numStudents = 15;
        long timeSpent = INITIAL_TIME_SPENT;
        EmployeeQueueData employeeQueueData = new EmployeeQueueData(Lists.newArrayList(),
                numStudents, timeSpent);

        doReturn(employeeQueueData).when(physicalQueueWorkflow).getEmployeeQueueData(anyString());

        GetEmployeeQueueDataResponse response = queueService.getEmployeeQueueData("e1");

        assertNotNull(response);
        assertNotNull(response.getEmployeeQueueData());
        validateEmployeeQueueData(employeeQueueData, numStudents, timeSpent);
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

        int numStudents = 0;
        long timeSpent = 0;
        EmployeeQueueData employeeQueueData = new EmployeeQueueData(Lists.newArrayList(),
                numStudents, timeSpent);

        RemoveStudentResponse response = queueService.removeStudentFromQueue("e1",
                employeeQueueData);

        assertNotNull(response);
        assertNotNull(response.getEmployeeQueueData());

        assertEquals(response.getEmployeeQueueData().getStudents().size(), 0);
        validateEmployeeQueueData(employeeQueueData, numStudents, timeSpent);
    }

    @Test
    public void testRemoveStudentFromQueueWithStudentAtHead() {
        doReturn(employee).when(employeeHashOperations).get(anyString(), any());
        doReturn(student).when(virtualQueueWorkflow).getStudentAtHead(anyString(), any());
        doReturn(studentQueueStatus).when(virtualQueueWorkflow).leaveQueue(anyString(), anyString(),
                any());
        doReturn(windowQueueStatus).when(windowQueueWorkflow).joinQueue(anyString(), any(), any());

        int numStudents = 0;
        long timeSpent = INITIAL_TIME_SPENT;
        EmployeeQueueData employeeQueueData = new EmployeeQueueData(Lists.newArrayList(),
                numStudents, timeSpent);

        RemoveStudentResponse response = queueService.removeStudentFromQueue("e1",
                employeeQueueData);

        assertNotNull(response);
        assertNotNull(response.getEmployeeQueueData());

        assertEquals(response.getEmployeeQueueData().getStudents().size(), 0);
        validateEmployeeQueueData(employeeQueueData, numStudents, timeSpent);
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
        validateVirtualQueueStatus(virtualQueueStatus, position, expectedWaitTime);
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
        validateWindowQueueStatus(windowQueueStatus, position, physicalSize, expectedWaitTime,
                employee);
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
        validatePhysicalQueueStatus(physicalQueueStatus, position, expectedWaitTime);
    }

    @Test
    public void testSetOverallPositionAndWaitTimeDefault() {
        try {
            QueueStatus queueStatus = new QueueStatus("c1", "dq1", QueueType.DEFAULT, Role.SWE);
            queueService.setOverallPositionAndWaitTime(queueStatus);
            fail();
        } catch (InvalidRequestException ex) {
            assertEquals(ex.getMessage(), "QueueType mismatch");
        }
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
        assertEquals(queueService.getIndexFromPosition(0), 0);
        assertEquals(queueService.getIndexFromPosition(4), 3);
    }
}

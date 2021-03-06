package com.careerfair.q.workflow.queue.employee.window.implementation;

import com.careerfair.q.model.redis.Employee;
import com.careerfair.q.model.redis.Student;
import com.careerfair.q.model.redis.StudentQueueStatus;
import com.careerfair.q.service.queue.response.QueueStatus;
import com.careerfair.q.util.enums.QueueType;
import com.careerfair.q.util.enums.Role;
import com.careerfair.q.util.exception.InvalidRequestException;
import org.assertj.core.util.Lists;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.Spy;
import org.mockito.stubbing.Answer;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.ListOperations;
import org.springframework.data.redis.core.RedisTemplate;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

public class WindowQueueWorkflowTest {

    @Spy
    private RedisTemplate<String, String> employeeRedisTemplate;
    @Spy
    private RedisTemplate<String, Student> queueRedisTemplate;
    @Spy
    private RedisTemplate<String, String> studentRedisTemplate;

    @Mock
    private HashOperations<String, Object, Object> employeeHashOperations;
    @Mock
    private HashOperations<String, Object, Object> studentHashOperations;
    @Mock
    private ListOperations<String, Student> queueListOperations;

    @InjectMocks
    private final WindowQueueWorkflowImpl windowQueueWorkflow = new WindowQueueWorkflowImpl();

    private Employee employee;
    private Student student;
    private StudentQueueStatus studentQueueStatus;

    @BeforeEach
    public void setupMock() {
        MockitoAnnotations.initMocks(this);

        employee = new Employee("e1", "c1", Role.SWE);
        student = new Student("s1", "student1");
        studentQueueStatus = new StudentQueueStatus("student1", "c1", "s1", Role.SWE);

        when(employeeRedisTemplate.opsForHash()).thenReturn(employeeHashOperations);
        when(studentRedisTemplate.opsForHash()).thenReturn(studentHashOperations);
        when(queueRedisTemplate.opsForList()).thenReturn(queueListOperations);
    }

    @Test
    public void testJoinQueue() {
        employee.setWindowQueueId("wq1");

        List<Student> students = Lists.newArrayList();

        doReturn(employee).when(employeeHashOperations).get(anyString(), any());
        doAnswer(addStudentAnswer(students)).when(queueListOperations).range(anyString(), anyLong(),
                anyLong());
        doNothing().when(studentHashOperations).put(anyString(), any(), any());
        doReturn(1L).when(queueListOperations).rightPush(anyString(), any());
        doReturn(1L).when(queueListOperations).size(anyString());

        QueueStatus queueStatus = windowQueueWorkflow.joinQueue("e1", student,
                studentQueueStatus);

        assertNotNull(studentQueueStatus.getJoinedWindowQueueAt());
        assertEquals(studentQueueStatus.getEmployeeId(), employee.getId());

        assertEquals(queueStatus.getQueueId(), employee.getWindowQueueId());
        assertEquals(queueStatus.getQueueType(), QueueType.WINDOW);
        assertEquals(queueStatus.getRole(), Role.SWE);
        assertEquals(queueStatus.getPosition(), 1);
        assertEquals(queueStatus.getEmployee(), employee);
    }

    @Test
    public void testLeaveQueue() {
        employee.setWindowQueueId("wq1");

        Student newStudent = new Student("s2", "student2");
        List<Student> students = Lists.newArrayList(newStudent, student);

        doReturn(employee).when(employeeHashOperations).get(anyString(), any());
        doAnswer(removeStudentAnswer(students)).when(queueListOperations)
                .range(anyString(), anyLong(), anyLong());
        doReturn(studentQueueStatus).when(studentHashOperations).get(anyString(), any());
        doReturn(1L).when(studentHashOperations).delete(anyString(), any());
        doReturn(1L).when(queueListOperations).remove(anyString(), anyLong(), any());

        windowQueueWorkflow.leaveQueue("e1", "s1");

        verify(queueListOperations).remove(anyString(), anyLong(), any());
        verify(queueListOperations, never()).leftPop(anyString());
    }

    @Test
    public void testAddQueue() {
        doReturn(employee).when(employeeHashOperations).get(anyString(), any());
        doNothing().when(employeeHashOperations).put(anyString(), any(), any());

        Employee employee = windowQueueWorkflow.addQueue(this.employee.getId());

        assertNotNull(employee.getWindowQueueId());
    }

    @Test
    public void testAddQueueWithQueueExisting() {
        employee.setWindowQueueId("wq1");

        doReturn(employee).when(employeeHashOperations).get(anyString(), any());

        try {
            windowQueueWorkflow.addQueue(employee.getId());
            fail();
        } catch (InvalidRequestException ex) {
            assertEquals(ex.getMessage(),
                    "Employee with employee id=e1 is associated with window queue with id=wq1");
        }
    }

    @Test
    public void testRemoveQueueNonEmpty() {
        employee.setWindowQueueId("wq1");
        studentQueueStatus.setEmployeeId("e1");

        List<Student> students = Lists.newArrayList(student);

        doReturn(employee).when(employeeHashOperations).get(anyString(), any());
        doReturn(1L).when(queueListOperations).size(anyString());
        doReturn(students).when(queueListOperations).range(anyString(), anyLong(), anyLong());
        doReturn(student).when(queueListOperations).leftPop(anyString());
        doNothing().when(employeeHashOperations).put(anyString(), any(), any());
        doReturn(true).when(queueRedisTemplate).delete(anyString());

        Employee employee = windowQueueWorkflow.removeQueue(this.employee.getId(), false);

        verify(queueListOperations, atLeastOnce()).leftPop(anyString());
        assertNull(employee.getWindowQueueId());
    }

    @Test
    public void testRemoveQueueEmpty() {
        employee.setWindowQueueId("wq1");
        studentQueueStatus.setEmployeeId("e1");

        List<Student> students = Lists.newArrayList();

        doReturn(employee).when(employeeHashOperations).get(anyString(), any());
        doReturn(0L).when(queueListOperations).size(anyString());
        doReturn(students).when(queueListOperations).range(anyString(), anyLong(), anyLong());
        doNothing().when(employeeHashOperations).put(anyString(), any(), any());
        doReturn(true).when(queueRedisTemplate).delete(anyString());

        Employee employee = windowQueueWorkflow.removeQueue(this.employee.getId(), true);

        verify(queueListOperations, never()).leftPop(anyString());
        assertNull(employee.getWindowQueueId());
    }

    @Test
    public void testSize() {
        employee.setWindowQueueId("wq1");

        doReturn(employee).when(employeeHashOperations).get(anyString(), any());
        doReturn(1L).when(queueListOperations).size(anyString());

        Long size = windowQueueWorkflow.size("e1");

        assertEquals(size, 1L);
    }

    @Test
    public void testGetQueueStatus() {
        employee.setWindowQueueId("wq1");

        studentQueueStatus.setEmployeeId("e1");
        studentQueueStatus.setQueueType(QueueType.WINDOW);
        studentQueueStatus.setQueueId("wq1");

        List<Student> students = Lists.newArrayList(student);

        doReturn(employee).when(employeeHashOperations).get(anyString(), any());
        doReturn(students).when(queueListOperations).range(anyString(), anyLong(), anyLong());

        QueueStatus queueStatus = windowQueueWorkflow.getQueueStatus(studentQueueStatus);

        assertEquals(queueStatus.getPosition(), 1);
    }

    @Test
    public void testGetQueueStatusBadQueueType() {
        studentQueueStatus.setQueueType(QueueType.PHYSICAL);

        try {
            windowQueueWorkflow.getQueueStatus(studentQueueStatus);
            fail();
        } catch (InvalidRequestException ex) {
            assertEquals(ex.getMessage(), "QueueType in studentQueueStatus != WINDOW");
        }
    }

    @Test
    public void testUpdateQueueStatus() {
        employee.setWindowQueueId("wq1");

        windowQueueWorkflow.updateStudentQueueStatus(studentQueueStatus, employee);

        assertEquals(studentQueueStatus.getQueueType(), QueueType.WINDOW);
        assertEquals(studentQueueStatus.getQueueId(), "wq1");
        assertEquals(studentQueueStatus.getEmployeeId(), "e1");
        assertNotNull(studentQueueStatus.getJoinedWindowQueueAt());
    }

    @Test
    public void testCheckQueueAssociated() {
        employee.setWindowQueueId("wq1");

        String queueId = windowQueueWorkflow.checkQueueAssociated(employee);

        assertEquals(queueId, "wq1");
    }

    @Test
    public void testCheckQueueAssociatedBadQueueId() {
        try {
            windowQueueWorkflow.checkQueueAssociated(employee);
            fail();
        } catch (InvalidRequestException ex) {
            assertEquals(ex.getMessage(),
                    "Employee with employee id=e1 is not associated with any window queue");
        }
    }

    private Answer<List<Student>> addStudentAnswer(List<Student> students) {
        return invocationOnMock -> {
            List<Student> temp = Lists.newArrayList(students);
            students.add(student);
            return temp;
        };
    }

    private Answer<List<Student>> removeStudentAnswer(List<Student> students) {
        return invocationOnMock -> {
            List<Student> temp = Lists.newArrayList(students);
            students.remove(student);
            return temp;
        };
    }
}

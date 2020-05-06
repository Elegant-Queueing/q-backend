package com.careerfair.q.workflow.queue.physical;

import com.careerfair.q.model.redis.Employee;
import com.careerfair.q.model.redis.Student;
import com.careerfair.q.model.redis.StudentQueueStatus;
import com.careerfair.q.service.database.StudentFirebase;
import com.careerfair.q.service.queue.response.EmployeeQueueData;
import com.careerfair.q.service.queue.response.QueueStatus;
import com.careerfair.q.util.enums.QueueType;
import com.careerfair.q.util.enums.Role;
import com.careerfair.q.workflow.queue.physical.implementation.PhysicalQueueWorkflowImpl;
import com.google.cloud.Timestamp;
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
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

public class PhysicalQueueWorkflowTest {

    @Spy
    private RedisTemplate<String, String> employeeRedisTemplate;
    @Spy
    private RedisTemplate<String, Student> queueRedisTemplate;
    @Spy
    private RedisTemplate<String, String> studentRedisTemplate;

    @Mock
    private HashOperations<String, Object, Object> employeeHashOperations;
    @Mock
    private HashOperations<String, Object, Object> studentQueueHashOperations;
    @Mock
    private ListOperations<String, Student> queueListOperations;

    @Mock
    private StudentFirebase studentFirebase;

    @Mock
    private Employee employee;
    @Mock
    private Student student;
    @Mock
    private StudentQueueStatus studentQueueStatus;

    @InjectMocks
    private PhysicalQueueWorkflow physicalQueueWorkflow = new PhysicalQueueWorkflowImpl();

    @BeforeEach
    public void setupMock() {
        MockitoAnnotations.initMocks(this);

        employee = new Employee("e1", "c1", Role.SWE);
        student = new Student("s1", "student1");
        studentQueueStatus = new StudentQueueStatus("c1", "s1", Role.SWE);

        when(employeeRedisTemplate.opsForHash()).thenReturn(employeeHashOperations);
        when(studentRedisTemplate.opsForHash()).thenReturn(studentQueueHashOperations);
        when(queueRedisTemplate.opsForList()).thenReturn(queueListOperations);
    }

    @Test
    public void testJoinQueue() {
        employee.setPhysicalQueueId("pq1");
        studentQueueStatus.setEmployeeId(employee.getId());

        doReturn(employee).when(employeeHashOperations).get(anyString(), any());
        doReturn(studentQueueStatus).when(studentQueueHashOperations).get(anyString(), any());
        doReturn(1L).when(queueListOperations).rightPush(anyString(), any());
        doReturn(1L).when(queueListOperations).size(anyString());

        QueueStatus queueStatus = physicalQueueWorkflow.joinQueue("e1", student);

        assertEquals(queueStatus.getQueueId(), employee.getPhysicalQueueId());
        assertEquals(queueStatus.getQueueType(), QueueType.PHYSICAL);
        assertEquals(queueStatus.getRole(), Role.SWE);
        assertEquals(queueStatus.getPosition(), 1);
        assertEquals(queueStatus.getEmployee(), employee);
    }

    @Test
    public void testLeaveQueue() {
        employee.setPhysicalQueueId("pq1");

        Student newStudent = new Student("s2", "student2");
        List<Student> students = Lists.newArrayList(student, newStudent);

        doReturn(employee).when(employeeHashOperations).get(anyString(), any());
        doAnswer(removeStudentAnswer(students)).when(queueListOperations)
                .range(anyString(), anyLong(), anyLong());
        doReturn(studentQueueStatus).when(studentQueueHashOperations).get(anyString(), any());
        doReturn(1L).when(studentQueueHashOperations).delete(anyString(), any());
        doReturn(1L).when(queueListOperations).remove(anyString(), anyLong(), any());

        physicalQueueWorkflow.leaveQueue("e1", "s1");

        verify(employeeHashOperations).get(anyString(), any());
        verify(queueListOperations).range(anyString(), anyLong(), anyLong());
        verify(studentQueueHashOperations).get(anyString(), any());
        verify(studentQueueHashOperations).delete(anyString(), any());
        verify(queueListOperations).remove(anyString(), anyLong(), any());
    }

    @Test
    public void testAddQueue() {
        doReturn(employee).when(employeeHashOperations).get(anyString(), any());
        doNothing().when(employeeHashOperations).put(anyString(), any(), any());

        Employee employee = physicalQueueWorkflow.addQueue(this.employee.getId());

        assertNotNull(employee.getPhysicalQueueId());
    }

    @Test
    public void testRemoveQueueNonEmpty() {
        employee.setPhysicalQueueId("pq1");
        studentQueueStatus.setEmployeeId(employee.getId());

        List<Student> students = Lists.newArrayList(student);

        doReturn(employee).when(employeeHashOperations).get(anyString(), any());
        doReturn(1L).when(queueListOperations).size(anyString());
        doReturn(students).when(queueListOperations).range(anyString(), anyLong(), anyLong());
        doNothing().when(employeeHashOperations).put(anyString(), any(), any());

        Employee employee = physicalQueueWorkflow.removeQueue(this.employee.getId(), false);

        verify(queueListOperations, atLeastOnce()).leftPop(anyString());
        assertNull(employee.getPhysicalQueueId());
    }

    @Test
    public void testRemoveQueueEmpty() {
        employee.setPhysicalQueueId("pq1");
        studentQueueStatus.setEmployeeId(employee.getId());

        List<Student> students = Lists.newArrayList();

        doReturn(employee).when(employeeHashOperations).get(anyString(), any());
        doReturn(0L).when(queueListOperations).size(anyString());
        doReturn(students).when(queueListOperations).range(anyString(), anyLong(), anyLong());
        doNothing().when(employeeHashOperations).put(anyString(), any(), any());

        Employee employee = physicalQueueWorkflow.removeQueue(this.employee.getId(), true);

        verify(queueListOperations, never()).leftPop(anyString());
        assertNull(employee.getPhysicalQueueId());
    }

    @Test
    public void testRegisterStudent() throws InterruptedException {
        employee.setPhysicalQueueId("pq1");
        studentQueueStatus.setJoinedPhysicalQueueAt(Timestamp.now());

        Student newStudent = new Student("s2", "student2");
        List<Student> students = Lists.newArrayList(student, newStudent);

        doReturn(employee).when(employeeHashOperations).get(anyString(), any());
        doAnswer(removeStudentAnswer(students)).when(queueListOperations)
                .range(anyString(), anyLong(), anyLong());
        doReturn(studentQueueStatus).when(studentQueueHashOperations).get(anyString(), any());
        doReturn(1L).when(studentQueueHashOperations).delete(anyString(), any());
        doReturn(student).when(queueListOperations).leftPop(anyString());
        doReturn(true).when(studentFirebase).registerStudent(any(), any());

        Thread.sleep(1000);

        EmployeeQueueData employeeQueueData = physicalQueueWorkflow.registerStudent("e1", "s1");

        verify(queueListOperations).leftPop(anyString());
        verify(queueListOperations, never()).remove(anyString(), anyLong(), any());

        assertNotNull(employeeQueueData);
        assertEquals(employeeQueueData.getStudents(), students);
        assertEquals(employeeQueueData.getNumRegisteredStudents(), 1);
        assertNotEquals(employeeQueueData.getAverageTimePerStudent(), 0);
    }

    @Test
    public void testRemoveStudentFromQueue() {
        employee.setPhysicalQueueId("pq1");

        Student newStudent = new Student("s2", "student2");
        List<Student> students = Lists.newArrayList(student, newStudent);

        doReturn(employee).when(employeeHashOperations).get(anyString(), any());
        doAnswer(removeStudentAnswer(students)).when(queueListOperations)
                .range(anyString(), anyLong(), anyLong());
        doReturn(studentQueueStatus).when(studentQueueHashOperations).get(anyString(), any());
        doReturn(1L).when(studentQueueHashOperations).delete(anyString(), any());
        doReturn(student).when(queueListOperations).leftPop(anyString());

        EmployeeQueueData employeeQueueData = physicalQueueWorkflow.removeStudentFromQueue("e1",
                "s1");

        verify(queueListOperations).leftPop(anyString());
        verify(queueListOperations, never()).remove(anyString(), anyLong(), any());

        assertNotNull(employeeQueueData);
        assertEquals(employeeQueueData.getStudents(), students);
        assertEquals(employeeQueueData.getNumRegisteredStudents(), 0);
        assertEquals(employeeQueueData.getAverageTimePerStudent(), 0);
    }

    @Test
    public void testGetEmployeeQueueData() {
        employee.setPhysicalQueueId("pq1");

        doReturn(employee).when(employeeHashOperations).get(anyString(), any());
        doReturn(Lists.newArrayList()).when(queueListOperations).range(anyString(), anyLong(),
                anyLong());

        EmployeeQueueData employeeQueueData = physicalQueueWorkflow.getEmployeeQueueData("e1");

        assertNotNull(employeeQueueData);
        assertEquals(employeeQueueData.getStudents().size(), 0);
        assertEquals(employeeQueueData.getNumRegisteredStudents(), 0);
        assertEquals(employeeQueueData.getAverageTimePerStudent(), 0);

    }

    private Answer removeStudentAnswer(List<Student> students) {
        return invocationOnMock -> {
            List<Student> temp = Lists.newArrayList(students);
            students.remove(student);
            return temp;
        };
    }
}

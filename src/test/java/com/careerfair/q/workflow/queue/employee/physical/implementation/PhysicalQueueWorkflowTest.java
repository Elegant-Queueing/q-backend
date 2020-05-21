package com.careerfair.q.workflow.queue.employee.physical.implementation;

import com.careerfair.q.model.redis.Employee;
import com.careerfair.q.model.redis.Student;
import com.careerfair.q.model.redis.StudentQueueStatus;
import com.careerfair.q.service.queue.response.EmployeeQueueData;
import com.careerfair.q.service.queue.response.QueueStatus;
import com.careerfair.q.util.enums.QueueType;
import com.careerfair.q.util.enums.Role;
import com.careerfair.q.util.exception.InvalidRequestException;
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

import static com.careerfair.q.util.constant.Queue.*;
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
    private HashOperations<String, Object, Object> studentHashOperations;
    @Mock
    private ListOperations<String, Student> queueListOperations;

    @Mock
    private Employee employee;
    @Mock
    private Student student;
    @Mock
    private StudentQueueStatus studentQueueStatus;

    @InjectMocks
    private final PhysicalQueueWorkflowImpl physicalQueueWorkflow = new PhysicalQueueWorkflowImpl();

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
        employee.setPhysicalQueueId("pq1");
        studentQueueStatus.setEmployeeId("e1");

        Timestamp joinedWindowQueueAt = Timestamp.now();
        studentQueueStatus.setJoinedWindowQueueAt(joinedWindowQueueAt);

        List<Student> students = Lists.newArrayList();

        doReturn(employee).when(employeeHashOperations).get(anyString(), any());
        doAnswer(addStudentAnswer(students)).when(queueListOperations).range(anyString(), anyLong(),
                anyLong());
        doNothing().when(studentHashOperations).put(anyString(), any(), any());
        doReturn(1L).when(queueListOperations).rightPush(anyString(), any());
        doReturn(1L).when(queueListOperations).size(anyString());

        QueueStatus queueStatus = physicalQueueWorkflow.joinQueue("e1", student,
                studentQueueStatus);

        assertEquals(studentQueueStatus.getJoinedWindowQueueAt(), joinedWindowQueueAt);
        assertNotNull(studentQueueStatus.getJoinedPhysicalQueueAt());
        assertEquals(studentQueueStatus.getPositionWhenJoinedPhysicalQueue(), 1L);

        assertEquals(queueStatus.getQueueId(), employee.getPhysicalQueueId());
        assertEquals(queueStatus.getQueueType(), QueueType.PHYSICAL);
        assertEquals(queueStatus.getRole(), Role.SWE);
        assertEquals(queueStatus.getPosition(), 1);
        assertEquals(queueStatus.getEmployee(), employee);
    }

    @Test
    public void testJoinQueueBadEmployeeId() {
        studentQueueStatus.setEmployeeId("e1");

        try {
            physicalQueueWorkflow.joinQueue("e2", student, studentQueueStatus);
            fail();
        } catch (InvalidRequestException ex) {
            assertEquals(ex.getMessage(),
                    "Mismatch between employee id=e2 and assigned employee id=e1");
        }
    }

    @Test
    public void testJoinQueueScanNotInTime() {
        employee.setPhysicalQueueId("pq1");
        studentQueueStatus.setEmployeeId("e1");

        Timestamp joinedWindowQueueAt = Timestamp.ofTimeSecondsAndNanos(
                Timestamp.now().getSeconds() - 2 * (WINDOW - BUFFER), 0);
        studentQueueStatus.setJoinedWindowQueueAt(joinedWindowQueueAt);

        try {
            physicalQueueWorkflow.joinQueue("e1", student, studentQueueStatus);
            fail();
        } catch (InvalidRequestException ex) {
            assertEquals(ex.getMessage(), "Student with student id=" + student.getId() +
                    " did not scan the code of employee with employee id=e1 in time");
        }
    }

    @Test
    public void testLeaveQueue() {
        employee.setPhysicalQueueId("pq1");

        Student newStudent = new Student("s2", "student2");
        List<Student> students = Lists.newArrayList(student, newStudent);

        doReturn(employee).when(employeeHashOperations).get(anyString(), any());
        doAnswer(removeStudentAnswer(students)).when(queueListOperations)
                .range(anyString(), anyLong(), anyLong());
        doReturn(studentQueueStatus).when(studentHashOperations).get(anyString(), any());
        doReturn(1L).when(studentHashOperations).delete(anyString(), any());
        doReturn(1L).when(queueListOperations).remove(anyString(), anyLong(), any());

        physicalQueueWorkflow.leaveQueue("e1", "s1");

        verify(queueListOperations).remove(anyString(), anyLong(), any());
        verify(queueListOperations, never()).leftPop(anyString());
    }

    @Test
    public void testAddQueue() {
        doReturn(employee).when(employeeHashOperations).get(anyString(), any());
        doNothing().when(employeeHashOperations).put(anyString(), any(), any());

        Employee employee = physicalQueueWorkflow.addQueue(this.employee.getId());

        assertNotNull(employee.getPhysicalQueueId());
        assertEquals(employee.getTotalTimeSpent(), INITIAL_TIME_SPENT);
        assertEquals(employee.getNumRegisteredStudents(), 0);
    }

    @Test
    public void testAddQueueQueueAlreadyExist() {
        employee.setPhysicalQueueId("pq1");

        doReturn(employee).when(employeeHashOperations).get(anyString(), any());

        try {
            physicalQueueWorkflow.addQueue(employee.getId());
            fail();
        } catch (InvalidRequestException ex) {
            assertEquals(ex.getMessage(),
                    "Employee with employee id=e1 is associated with physical queue with id=pq1");
        }
    }

    @Test
    public void testRemoveQueueNonEmpty() {
        employee.setPhysicalQueueId("pq1");
        studentQueueStatus.setEmployeeId("e1");

        List<Student> students = Lists.newArrayList(student);

        doReturn(employee).when(employeeHashOperations).get(anyString(), any());
        doReturn(1L).when(queueListOperations).size(anyString());
        doReturn(students).when(queueListOperations).range(anyString(), anyLong(), anyLong());
        doReturn(student).when(queueListOperations).leftPop(anyString());
        doNothing().when(employeeHashOperations).put(anyString(), any(), any());
        doReturn(true).when(queueRedisTemplate).delete(anyString());

        Employee employee = physicalQueueWorkflow.removeQueue(this.employee.getId(), false);

        verify(queueListOperations, atLeastOnce()).leftPop(anyString());
        assertNull(employee.getPhysicalQueueId());
    }

    @Test
    public void testRemoveQueueEmpty() {
        employee.setPhysicalQueueId("pq1");
        studentQueueStatus.setEmployeeId("e1");

        List<Student> students = Lists.newArrayList();

        doReturn(employee).when(employeeHashOperations).get(anyString(), any());
        doReturn(0L).when(queueListOperations).size(anyString());
        doReturn(students).when(queueListOperations).range(anyString(), anyLong(), anyLong());
        doNothing().when(employeeHashOperations).put(anyString(), any(), any());
        doReturn(true).when(queueRedisTemplate).delete(anyString());

        Employee employee = physicalQueueWorkflow.removeQueue(this.employee.getId(), true);

        verify(queueListOperations, never()).leftPop(anyString());
        assertNull(employee.getPhysicalQueueId());
    }

    @Test
    public void testRegisterStudent() throws InterruptedException {
        employee.setPhysicalQueueId("pq1");
        studentQueueStatus.setJoinedPhysicalQueueAt(Timestamp.now());
        studentQueueStatus.setPositionWhenJoinedPhysicalQueue(1);

        Student newStudent = new Student("s2", "student2");
        List<Student> students = Lists.newArrayList(student, newStudent);

        doReturn(employee).when(employeeHashOperations).get(anyString(), any());
        doAnswer(removeStudentAnswer(students)).when(queueListOperations)
                .range(anyString(), anyLong(), anyLong());
        doReturn(studentQueueStatus).when(studentHashOperations).get(anyString(), any());
        doReturn(1L).when(studentHashOperations).delete(anyString(), any());
        doReturn(student).when(queueListOperations).leftPop(anyString());

        Thread.sleep(1000);

        EmployeeQueueData employeeQueueData = physicalQueueWorkflow.registerStudent("e1", "s1");

        verify(queueListOperations).leftPop(anyString());
        verify(queueListOperations, never()).remove(anyString(), anyLong(), any());

        assertNotNull(employeeQueueData);
        assertEquals(employeeQueueData.getStudents(), students);
        assertEquals(employeeQueueData.getNumRegisteredStudents(), 1);
        assertNotEquals(employeeQueueData.getAverageTimePerStudent(), INITIAL_TIME_SPENT);
    }

    @Test
    public void testSkipStudent() {
        employee.setPhysicalQueueId("pq1");

        Student newStudent = new Student("s2", "student2");
        List<Student> students = Lists.newArrayList(student, newStudent);

        doReturn(employee).when(employeeHashOperations).get(anyString(), any());
        doAnswer(removeStudentAnswer(students)).when(queueListOperations)
                .range(anyString(), anyLong(), anyLong());
        doReturn(studentQueueStatus).when(studentHashOperations).get(anyString(), any());
        doReturn(1L).when(studentHashOperations).delete(anyString(), any());
        doReturn(student).when(queueListOperations).leftPop(anyString());

        EmployeeQueueData employeeQueueData = physicalQueueWorkflow.skipStudent("e1",
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

    @Test
    public void testSize() {
        employee.setPhysicalQueueId("pq1");

        doReturn(employee).when(employeeHashOperations).get(anyString(), any());
        doReturn(1L).when(queueListOperations).size(anyString());

        Long size = physicalQueueWorkflow.size("e1");

        assertEquals(size, 1L);
    }

    @Test
    public void testGetQueueStatus() {
        employee.setWindowQueueId("pq1");

        studentQueueStatus.setEmployeeId("e1");
        studentQueueStatus.setQueueType(QueueType.PHYSICAL);
        studentQueueStatus.setQueueId("pq1");

        List<Student> students = Lists.newArrayList(student);

        doReturn(employee).when(employeeHashOperations).get(anyString(), any());
        doReturn(students).when(queueListOperations).range(anyString(), anyLong(), anyLong());

        QueueStatus queueStatus = physicalQueueWorkflow.getQueueStatus(studentQueueStatus);

        assertEquals(queueStatus.getPosition(), 1);
    }

    @Test
    public void testGetQueueStatusBadQueueType() {
        studentQueueStatus.setQueueType(QueueType.WINDOW);

        try {
            physicalQueueWorkflow.getQueueStatus(studentQueueStatus);
            fail();
        } catch (InvalidRequestException ex) {
            assertEquals(ex.getMessage(), "QueueType in studentQueueStatus != PHYSICAL");
        }
    }

    @Test
    public void testUpdateQueueStatus() {
        employee.setPhysicalQueueId("pq1");

        physicalQueueWorkflow.updateStudentQueueStatus(studentQueueStatus, employee);

        assertEquals(studentQueueStatus.getQueueType(), QueueType.PHYSICAL);
        assertEquals(studentQueueStatus.getQueueId(), "pq1");
        assertNotNull(studentQueueStatus.getJoinedPhysicalQueueAt());
    }

    @Test
    public void testCheckQueueAssociated() {
        employee.setPhysicalQueueId("pq1");

        String queueId = physicalQueueWorkflow.checkQueueAssociated(employee);

        assertEquals(queueId, "pq1");
    }

    @Test
    public void testCheckQueueAssociatedBadQueueId() {
        try {
            physicalQueueWorkflow.checkQueueAssociated(employee);
            fail();
        } catch (InvalidRequestException ex) {
            assertEquals(ex.getMessage(),
                    "Employee with employee id=e1 is not associated with any physical queue");
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

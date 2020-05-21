package com.careerfair.q.workflow.queue.employee;

import com.careerfair.q.model.redis.Employee;
import com.careerfair.q.model.redis.Student;
import com.careerfair.q.model.redis.StudentQueueStatus;
import com.careerfair.q.service.queue.response.QueueStatus;
import com.careerfair.q.util.enums.Role;
import com.careerfair.q.util.exception.InvalidRequestException;
import org.assertj.core.util.Lists;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.Spy;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.ListOperations;
import org.springframework.data.redis.core.RedisTemplate;

import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

public class AbstractEmployeeQueueWorkflowTest {

    /**
     * Class used to test the methods in AbstractEmployeeQueueWorkflow
     */
    private static class EmployeeQueueWorkflow extends AbstractEmployeeQueueWorkflow {

        @Override
        protected String checkQueueAssociated(Employee employee) {
            return "eq1";
        }

        @Override
        protected void updateStudentQueueStatus(StudentQueueStatus studentQueueStatus,
                                                Employee employee) {
            studentQueueStatus.setEmployeeId(employee.getId());
            studentQueueStatus.setQueueId("eq1");
        }
    }


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
    private final AbstractEmployeeQueueWorkflow employeeQueueWorkflow = new EmployeeQueueWorkflow();

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
    public void testAddStudentNotPresent() {
        List<Student> students = Lists.newArrayList();

        doReturn(students).when(queueListOperations).range(anyString(), anyLong(), anyLong());
        doNothing().when(studentHashOperations).put(anyString(), any(), any());
        doReturn(1L).when(queueListOperations).rightPush(anyString(), any(Student.class));

        StudentQueueStatus studentQueueStatus = employeeQueueWorkflow.addStudent(employee, student,
                this.studentQueueStatus);

        assertEquals(studentQueueStatus.getEmployeeId(), "e1");
        assertEquals(studentQueueStatus.getQueueId(), "eq1");
    }

    @Test
    public void testAddStudentPresent() {
        List<Student> students = Lists.newArrayList(student);

        doReturn(students).when(queueListOperations).range(anyString(), anyLong(), anyLong());

        try {
            employeeQueueWorkflow.addStudent(employee, student, studentQueueStatus);
            fail();
        } catch (InvalidRequestException ex) {
            assertEquals(ex.getMessage(), "Student with student id=" + student.getId() +
                    " is already present in the queue of the employee with employee id=" +
                    employee.getId());
        }
    }

    @Test
    public void testRemoveStudentFirstPresentAndRequired() {
        List<Student> students = Lists.newArrayList(student);

        doReturn(students).when(queueListOperations).range(anyString(), anyLong(), anyLong());
        doReturn(studentQueueStatus).when(studentHashOperations).get(anyString(), any());
        doReturn(1L).when(studentHashOperations).delete(any(), any());
        doReturn(student).when(queueListOperations).leftPop(anyString());

        StudentQueueStatus studentQueueStatus = employeeQueueWorkflow.removeStudent("e1", "eq1",
                "s1", true);

        verify(queueListOperations).leftPop(anyString());
        verify(queueListOperations, never()).remove(anyString(), anyLong(), any());

        assertEquals(studentQueueStatus, this.studentQueueStatus);
    }

    @Test
    public void testRemoveStudentNotPresent() {
        List<Student> students = Lists.newArrayList();

        doReturn(students).when(queueListOperations).range(anyString(), anyLong(), anyLong());

        try {
            employeeQueueWorkflow.removeStudent("e1", "eq1", "s1", true);
            fail();
        } catch (InvalidRequestException ex) {
            assertEquals(ex.getMessage(), "Student with student id=s1 is not present in the " +
                    "queue of employee with employee id=e1");
        }
    }

    @Test
    public void testRemoveStudentNotFirstPresentButRequired() {
        List<Student> students = Lists.newArrayList(new Student("s2", "student2"), student);

        doReturn(students).when(queueListOperations).range(anyString(), anyLong(), anyLong());

        try {
            employeeQueueWorkflow.removeStudent("e1", "eq1", "s1", true);
            fail();
        } catch (InvalidRequestException ex) {
            assertEquals(ex.getMessage(), "Student with student id=s1 is not at the head of the " +
                    "queue of employee with employee id=e1");
        }
    }

    @Test
    public void testRemoveStudentFirstPresentButNotRequired() {
        List<Student> students = Lists.newArrayList(student);

        doReturn(students).when(queueListOperations).range(anyString(), anyLong(), anyLong());
        doReturn(studentQueueStatus).when(studentHashOperations).get(anyString(), any());
        doReturn(1L).when(studentHashOperations).delete(any(), any());
        doReturn(1L).when(queueListOperations).remove(anyString(), anyLong(), any());

        StudentQueueStatus studentQueueStatus = employeeQueueWorkflow.removeStudent("e1", "eq1",
                "s1", false);

        verify(queueListOperations, never()).leftPop(anyString());
        verify(queueListOperations).remove(anyString(), anyLong(), any());

        assertEquals(studentQueueStatus, this.studentQueueStatus);
    }

    @Test
    public void testRemoveStudentNotFirstPresentAndNotRequired() {
        List<Student> students = Lists.newArrayList(new Student("s2", "student2"), student);

        doReturn(students).when(queueListOperations).range(anyString(), anyLong(), anyLong());
        doReturn(studentQueueStatus).when(studentHashOperations).get(anyString(), any());
        doReturn(1L).when(studentHashOperations).delete(any(), any());
        doReturn(1L).when(queueListOperations).remove(anyString(), anyLong(), any());

        StudentQueueStatus studentQueueStatus = employeeQueueWorkflow.removeStudent("e1", "eq1",
                "s1", false);

        verify(queueListOperations, never()).leftPop(anyString());
        verify(queueListOperations).remove(anyString(), anyLong(), any());

        assertEquals(studentQueueStatus, this.studentQueueStatus);
    }

    @Test
    public void testRemoveQueueEmptyAndRequired() {
        List<Student> students = Lists.newArrayList();

        doReturn(0L).when(queueListOperations).size(anyString());
        doReturn(students).when(queueListOperations).range(any(), anyLong(), anyLong());
        doReturn(true).when(queueRedisTemplate).delete(anyString());

        employeeQueueWorkflow.removeQueue(employee, true);

        verify(queueListOperations, never()).leftPop(anyString());
        verify(queueListOperations, never()).remove(anyString(), anyLong(), any());
        verify(queueRedisTemplate).delete(anyString());
    }

    @Test
    public void testRemoveQueueEmptyButNotRequired() {
        List<Student> students = Lists.newArrayList();

        doReturn(0L).when(queueListOperations).size(anyString());
        doReturn(students).when(queueListOperations).range(any(), anyLong(), anyLong());
        doReturn(true).when(queueRedisTemplate).delete(anyString());

        employeeQueueWorkflow.removeQueue(employee, false);

        verify(queueListOperations, never()).leftPop(anyString());
        verify(queueListOperations, never()).remove(anyString(), anyLong(), any());
        verify(queueRedisTemplate).delete(anyString());
    }

    @Test
    public void testRemoveQueueNotEmptyButRequired() {
        doReturn(1L).when(queueListOperations).size(anyString());

        try {
            employeeQueueWorkflow.removeQueue(employee, true);
            fail();
        } catch (InvalidRequestException ex) {
            assertEquals(ex.getMessage(), "Queue with id=eq1 is not empty");
        }
    }

    @Test
    public void testRemoveQueueNotEmptyAndNotRequired() {
        List<Student> students = Lists.newArrayList(student);

        doReturn(0L).when(queueListOperations).size(anyString());
        doReturn(students).when(queueListOperations).range(any(), anyLong(), anyLong());
        doReturn(true).when(queueRedisTemplate).delete(anyString());

        employeeQueueWorkflow.removeQueue(employee, false);

        verify(queueListOperations, atLeastOnce()).leftPop(anyString());
        verify(queueListOperations, never()).remove(anyString(), anyLong(), any());
        verify(queueRedisTemplate).delete(anyString());
    }

    @Test
    public void testCreateQueueStatus() {
        studentQueueStatus.setQueueId("eq1");

        QueueStatus queueStatus = employeeQueueWorkflow.createQueueStatus(studentQueueStatus,
                employee, 1);

        assertEquals(queueStatus.getQueueId(), "eq1");
        assertEquals(queueStatus.getCompanyId(), "c1");
        assertEquals(queueStatus.getRole(), Role.SWE);
        assertEquals(queueStatus.getPosition(), 1);
        assertEquals(queueStatus.getEmployee(), employee);
    }

    @Test
    public void testSize() {
        doReturn(employee).when(employeeHashOperations).get(anyString(), any());
        doReturn(1L).when(queueListOperations).size(anyString());

        Long size = employeeQueueWorkflow.size("e1");

        Assertions.assertEquals(size, 1L);
    }

    @Test
    public void testGetQueueStatus() {
        List<Student> students = Lists.newArrayList(student);
        studentQueueStatus.setQueueId("eq1");

        doReturn(employee).when(employeeHashOperations).get(anyString(), any());
        doReturn(students).when(queueListOperations).range(any(), anyLong(), anyLong());

        QueueStatus queueStatus = employeeQueueWorkflow.createQueueStatus(studentQueueStatus,
                employee, 1);

        assertEquals(queueStatus.getQueueId(), "eq1");
        assertEquals(queueStatus.getCompanyId(), "c1");
        assertEquals(queueStatus.getRole(), Role.SWE);
        assertEquals(queueStatus.getPosition(), 1);
        assertEquals(queueStatus.getEmployee(), employee);
    }
}

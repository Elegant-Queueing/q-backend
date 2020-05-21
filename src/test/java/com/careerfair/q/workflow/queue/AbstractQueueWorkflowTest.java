package com.careerfair.q.workflow.queue;

import com.careerfair.q.model.redis.Employee;
import com.careerfair.q.model.redis.Student;
import com.careerfair.q.model.redis.StudentQueueStatus;
import com.careerfair.q.util.enums.Role;
import com.careerfair.q.util.exception.InvalidRequestException;
import org.assertj.core.util.Lists;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.Spy;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.RedisTemplate;

import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

public class AbstractQueueWorkflowTest {

    /**
     * Class used to test the methods in QueueWorkflow
     */
    private static class QueueWorkflow extends AbstractQueueWorkflow { }


    @Spy
    private RedisTemplate<String, String> employeeRedisTemplate;
    @Spy
    private RedisTemplate<String, String> studentRedisTemplate;

    @Mock
    private HashOperations<String, Object, Object> employeeHashOperations;
    @Mock
    private HashOperations<String, Object, Object> studentHashOperations;

    @InjectMocks
    private final AbstractQueueWorkflow queueWorkflow = new QueueWorkflow();

    @BeforeEach
    public void setupMock() {
        MockitoAnnotations.initMocks(this);

        when(employeeRedisTemplate.opsForHash()).thenReturn(employeeHashOperations);
        when(studentRedisTemplate.opsForHash()).thenReturn(studentHashOperations);
    }

    @Test
    public void testGetEmployeeWithId() {
        Employee employee = mock(Employee.class);
        doReturn(employee).when(employeeHashOperations).get(anyString(), any());

        Employee result = queueWorkflow.getEmployeeWithId("e1");

        assertEquals(result, employee);
    }

    @Test
    public void testGetEmployeeWithIdBad() {
        doReturn(null).when(employeeHashOperations).get(anyString(), any());

        try {
            queueWorkflow.getEmployeeWithId("e1");
            fail();
        } catch (InvalidRequestException ex) {
            assertEquals(ex.getMessage(), "No such employee with employee id=e1 exists");
        }
    }

    @Test
    public void testGetStudentQueueStatus() {
        StudentQueueStatus studentQueueStatus = mock(StudentQueueStatus.class);
        doReturn(studentQueueStatus).when(studentHashOperations).get(anyString(), any());

        StudentQueueStatus result = queueWorkflow.getStudentQueueStatus("s1");

        assertEquals(result, studentQueueStatus);
    }

    @Test
    public void testGetStudentQueueStatusBad() {
        doReturn(null).when(studentHashOperations).get(anyString(), any());

        try {
            queueWorkflow.getStudentQueueStatus("s1");
            fail();
        } catch (InvalidRequestException ex) {
            assertEquals(ex.getMessage(), "No student with student id=s1 is present in a queue");
        }
    }

    @Test
    public void testGetStudentExistsIndex() {
        List<Student> students = Lists.newArrayList(new Student("s1", "student1"));

        int index = queueWorkflow.getStudentIndex("s1", students);

        assertEquals(index, 0);
    }

    @Test
    public void testGetStudentNonExistentIndex() {
        List<Student> students = Lists.newArrayList();

        int index = queueWorkflow.getStudentIndex("s1", students);

        assertEquals(index, -1);
    }

    @Test
    public void testInitialCalcEmployeeAverageTime() {
        Employee employee = new Employee("e1", "c1", Role.SWE);
        employee.setTotalTimeSpent(5L);
        employee.setNumRegisteredStudents(0);

        double averageTime = queueWorkflow.calcEmployeeAverageTime(employee);

        assertEquals(averageTime, 5, 0.0001);
    }

    @Test
    public void testCalcEmployeeAverageTime() {
        Employee employee = new Employee("e1", "c1", Role.SWE);
        employee.setTotalTimeSpent(5L);
        employee.setNumRegisteredStudents(2);

        double averageTime = queueWorkflow.calcEmployeeAverageTime(employee);

        assertEquals(averageTime, 2.5, 0.0001);
    }
}

package com.careerfair.q.workflow.queue.employee.physical.implementation;

import com.careerfair.q.model.redis.Employee;
import com.careerfair.q.model.redis.Student;
import com.careerfair.q.model.redis.StudentQueueStatus;
import com.careerfair.q.service.queue.response.EmployeeQueueData;
import com.careerfair.q.service.queue.response.QueueStatus;
import com.careerfair.q.util.enums.QueueType;
import com.careerfair.q.util.exception.InvalidRequestException;
import com.careerfair.q.workflow.queue.employee.AbstractEmployeeQueueWorkflow;
import com.careerfair.q.workflow.queue.employee.physical.PhysicalQueueWorkflow;
import com.google.cloud.Timestamp;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.util.List;

import static com.careerfair.q.util.constant.Queue.*;

@Component
public class PhysicalQueueWorkflowImpl extends AbstractEmployeeQueueWorkflow
        implements PhysicalQueueWorkflow {

    @Autowired private RedisTemplate<String, String> employeeRedisTemplate;

    @Override
    public QueueStatus joinQueue(String employeeId, Student student,
                                 StudentQueueStatus studentWindowQueueStatus) {
        if (!employeeId.equals(studentWindowQueueStatus.getEmployeeId())) {
            throw new InvalidRequestException("Mismatch between employee id=" + employeeId +
                    " and assigned employee id=" + studentWindowQueueStatus.getEmployeeId());
        }
        if (studentWindowQueueStatus.getJoinedWindowQueueAt().getSeconds() + WINDOW + BUFFER <
                Timestamp.now().getSeconds()) {
            throw new InvalidRequestException("Student with student id=" + student.getId() +
                    " did not scan the code of employee with employee id=" + employeeId +
                    " in time");
        }

        Employee employee = getEmployeeWithId(employeeId);
        StudentQueueStatus studentQueueStatus = addStudent(employee, student,
                studentWindowQueueStatus);

        long currentPosition = size(employeeId);
        studentQueueStatus.setPositionWhenJoinedPhysicalQueue(currentPosition);
        studentRedisTemplate.opsForHash().put(STUDENT_CACHE_NAME, student.getId(),
                studentQueueStatus);
        return createQueueStatus(studentQueueStatus, employee, currentPosition);
    }

    @Override
    public void leaveQueue(String employeeId, String studentId) {
        Employee employee = getEmployeeWithId(employeeId);
        removeStudent(employeeId, checkQueueAssociated(employee), studentId, false);
    }

    @Override
    public Employee addQueue(String employeeId) {
        Employee employee = getEmployeeWithId(employeeId);

        if (employee.getPhysicalQueueId() != null) {
            throw new InvalidRequestException("Employee with employee id=" + employeeId +
                    " is associated with physical queue with id=" + employee.getPhysicalQueueId());
        }

        employee.setPhysicalQueueId(generateRandomId());
        employee.setTotalTimeSpent(INITIAL_TIME_SPENT);
        employeeRedisTemplate.opsForHash().put(EMPLOYEE_CACHE_NAME, employeeId, employee);
        return employee;
    }

    @Override
    public Employee removeQueue(String employeeId, boolean isEmpty) {
        Employee employee = getEmployeeWithId(employeeId);
        removeQueue(employee, isEmpty);
        employee.setPhysicalQueueId(null);
        employeeRedisTemplate.opsForHash().put(EMPLOYEE_CACHE_NAME, employeeId, employee);
        return employee;
    }

    @Override
    public EmployeeQueueData registerStudent(String employeeId, String studentId) {
        Employee employee = getEmployeeWithId(employeeId);
        StudentQueueStatus studentQueueStatus = removeStudent(employeeId,
                checkQueueAssociated(employee), studentId, true);

        updateRedisEmployee(employee, studentQueueStatus);
        employeeRedisTemplate.opsForHash().put(EMPLOYEE_CACHE_NAME, employeeId, employee);

        return getEmployeeQueueData(employeeId);
    }

    @Override
    public EmployeeQueueData skipStudent(String employeeId, String studentId) {
        Employee employee = getEmployeeWithId(employeeId);
        removeStudent(employeeId, employee.getPhysicalQueueId(), studentId, true);
        return getEmployeeQueueData(employeeId);
    }

    @Override
    public EmployeeQueueData getEmployeeQueueData(String employeeId) {
        Employee employee = getEmployeeWithId(employeeId);
        List<Student> students = queueRedisTemplate.opsForList()
                .range(checkQueueAssociated(employee), 0L, -1L);

        return new EmployeeQueueData(students, employee.getNumRegisteredStudents(),
                calcEmployeeAverageTime(employee));
    }

    @Override
    public Long size(String employeeId) {
        return super.size(employeeId);
    }

    @Override
    public QueueStatus getQueueStatus(StudentQueueStatus studentQueueStatus) {
        if (studentQueueStatus.getQueueType() != QueueType.PHYSICAL) {
            throw new InvalidRequestException("QueueType in studentQueueStatus != PHYSICAL");
        }

        return super.getQueueStatus(studentQueueStatus);
    }

    @Override
    protected void updateStudentQueueStatus(StudentQueueStatus studentQueueStatus,
                                            Employee employee) {
        studentQueueStatus.setQueueId(employee.getPhysicalQueueId());
        studentQueueStatus.setQueueType(QueueType.PHYSICAL);
        studentQueueStatus.setJoinedPhysicalQueueAt(Timestamp.now());
    }

    @Override
    protected String checkQueueAssociated(Employee employee) throws InvalidRequestException {
        String physicalQueueId = employee.getPhysicalQueueId();
        if (physicalQueueId == null) {
            throw new InvalidRequestException("Employee with employee id=" + employee.getId() +
                    " is not associated with any physical queue");
        }
        return physicalQueueId;
    }

    /**
     * Updates and returns an employee to be stored in Redis
     *
     * @param employee employee to update
     * @param studentQueueStatus student's queue status
     */
    private void updateRedisEmployee(Employee employee, StudentQueueStatus studentQueueStatus) {
        long timeSpent = (Timestamp.now().getSeconds() -
                studentQueueStatus.getJoinedPhysicalQueueAt().getSeconds()) /
                studentQueueStatus.getPositionWhenJoinedPhysicalQueue();
        employee.setNumRegisteredStudents(employee.getNumRegisteredStudents() + 1);
        employee.setTotalTimeSpent(employee.getTotalTimeSpent() + timeSpent);
    }
}

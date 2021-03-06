package com.careerfair.q.service.queue.implementation;

import com.careerfair.q.model.redis.Employee;
import com.careerfair.q.model.redis.Student;
import com.careerfair.q.model.redis.StudentQueueStatus;
import com.careerfair.q.model.redis.VirtualQueueData;
import com.careerfair.q.service.database.FirebaseService;
import com.careerfair.q.service.queue.QueueService;
import com.careerfair.q.service.queue.response.*;
import com.careerfair.q.service.validation.ValidationService;
import com.careerfair.q.util.enums.QueueType;
import com.careerfair.q.util.enums.Role;
import com.careerfair.q.util.exception.InvalidRequestException;
import com.careerfair.q.workflow.queue.employee.physical.PhysicalQueueWorkflow;
import com.careerfair.q.workflow.queue.employee.window.WindowQueueWorkflow;
import com.careerfair.q.workflow.queue.virtual.VirtualQueueWorkflow;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static com.careerfair.q.util.constant.Queue.*;

@Service
public class QueueServiceImpl implements QueueService {

    @Autowired private VirtualQueueWorkflow virtualQueueWorkflow;
    @Autowired private WindowQueueWorkflow windowQueueWorkflow;
    @Autowired private PhysicalQueueWorkflow physicalQueueWorkflow;

    @Autowired private RedisTemplate<String, String> employeeRedisTemplate;
    @Autowired private RedisTemplate<String, String> studentRedisTemplate;
//    @Autowired private RedisTemplate<String, Student> queueRedisTemplate;

    @Autowired private FirebaseService firebaseService;
    @Autowired private ValidationService validationService;

    @Override
    public JoinQueueResponse joinVirtualQueue(String companyId, Role role, Student student) {
        validationService.checkValidCompanyId(companyId);
        validationService.checkValidStudentId(student.getId());

        QueueStatus status = virtualQueueWorkflow.joinQueue(companyId, role, student);

        if (status.getPosition() == 1) {
            String employeeId = getEmployeeWithMostQueueSpace(companyId, role);

            if (employeeId != null) {
                status = shiftStudentToWindow(companyId, employeeId, role, student);
            }
        }

        setOverallPositionAndWaitTime(status);
        return new JoinQueueResponse(status);
    }

    @Override
    public JoinQueueResponse joinEmployeeQueue(String employeeId, String studentId) {
        StudentQueueStatus studentQueueStatus = windowQueueWorkflow.leaveQueue(employeeId,
                studentId);
        Student student = new Student(studentId, studentQueueStatus.getName());
        QueueStatus queueStatus = physicalQueueWorkflow.joinQueue(employeeId, student,
                studentQueueStatus);
        setOverallPositionAndWaitTime(queueStatus);
        return new JoinQueueResponse(queueStatus);
    }

    @Override
    public void leaveQueue(String companyId, String studentId, Role role) {
        StudentQueueStatus studentQueueStatus = getStudentQueueStatus(studentId);
        QueueType queueType = studentQueueStatus.getQueueType();
        String employeeId = studentQueueStatus.getEmployeeId();

        switch (queueType) {

            case VIRTUAL:
                virtualQueueWorkflow.leaveQueue(companyId, studentId, role);
                break;

            case WINDOW:
                windowQueueWorkflow.leaveQueue(employeeId, studentId);

                Student studentAtHead = virtualQueueWorkflow.getStudentAtHead(companyId, role);
                if (studentAtHead != null) {
                    shiftStudentToWindow(companyId, employeeId, role, studentAtHead);
                }
                break;

            case PHYSICAL:
                physicalQueueWorkflow.leaveQueue(studentQueueStatus.getEmployeeId(), studentId);

                studentAtHead = virtualQueueWorkflow.getStudentAtHead(companyId, role);
                if (studentAtHead != null) {
                    shiftStudentToWindow(companyId, employeeId, role, studentAtHead);
                }
                break;

            default:
                throw new InvalidRequestException("No such QueueType exists");
        }
    }

    @Override
    public GetQueueStatusResponse getQueueStatus(String studentId) {
        StudentQueueStatus studentQueueStatus = getStudentQueueStatus(studentId);
        QueueType queueType = studentQueueStatus.getQueueType();
        QueueStatus queueStatus;

        switch (queueType) {

            case VIRTUAL:
                queueStatus = virtualQueueWorkflow.getQueueStatus(studentQueueStatus);
                break;

            case WINDOW:
                queueStatus = windowQueueWorkflow.getQueueStatus(studentQueueStatus);
                break;

            case PHYSICAL:
                queueStatus = physicalQueueWorkflow.getQueueStatus(studentQueueStatus);
                break;

            default:
                throw new InvalidRequestException("No such QueueType exists");
        }

        setOverallPositionAndWaitTime(queueStatus);
        return new GetQueueStatusResponse(queueStatus);
    }

    @Override
    public AddQueueResponse addQueue(String companyId, String employeeId, Role role) {
        validationService.checkValidCompanyId(companyId);
        validationService.checkValidEmployeeId(employeeId);
        validationService.checkEmployeeAssociations(companyId, employeeId, role);

        Employee employee = (Employee) employeeRedisTemplate.opsForHash()
                .get(EMPLOYEE_CACHE_NAME, employeeId);

        if (employee == null) {
            employee = new Employee(employeeId, companyId, role);
            employeeRedisTemplate.opsForHash().put(EMPLOYEE_CACHE_NAME, employeeId, employee);
        }

        virtualQueueWorkflow.addQueue(companyId, employeeId, role);

        if (employee.getWindowQueueId() == null) {
            windowQueueWorkflow.addQueue(employeeId);
        }
        if (employee.getPhysicalQueueId() == null) {
            physicalQueueWorkflow.addQueue(employeeId);
        }

        int space = (int) getEmployeeQueueSpace(employeeId);
        for (int i = 0; i < space ; i++) {
            Student studentAtHead = virtualQueueWorkflow.getStudentAtHead(companyId, role);
            if (studentAtHead == null) {
                break;
            }
            shiftStudentToWindow(companyId, employeeId, role, studentAtHead);
        }

        EmployeeQueueData employeeQueueData = physicalQueueWorkflow.getEmployeeQueueData(
                employeeId);
        return new AddQueueResponse(employeeQueueData);
    }

    @Override
    public PauseQueueResponse pauseQueue(String employeeId) {
        virtualQueueWorkflow.pauseQueueForEmployee(employeeId);
        EmployeeQueueData employeeQueueData = physicalQueueWorkflow.getEmployeeQueueData(
                employeeId);
        return new PauseQueueResponse(employeeQueueData);
    }

    @Override
    public RemoveStudentResponse registerStudent(String employeeId, String studentId) {
        EmployeeQueueData employeeQueueData = physicalQueueWorkflow.registerStudent(employeeId,
                studentId);
        firebaseService.registerStudent(studentId, employeeId);
        return removeStudentFromQueue(employeeId, employeeQueueData);
    }

    @Override
    public RemoveStudentResponse skipStudent(String employeeId, String studentId) {
        EmployeeQueueData employeeQueueData = physicalQueueWorkflow.skipStudent(employeeId,
                studentId);
        return removeStudentFromQueue(employeeId, employeeQueueData);
    }

    @Override
    public GetEmployeeQueueDataResponse getEmployeeQueueData(String employeeId) {
        EmployeeQueueData employeeQueueData = physicalQueueWorkflow.getEmployeeQueueData(
                employeeId);
        return new GetEmployeeQueueDataResponse(employeeQueueData);
    }

    @Override
    public List<Employee> getAllEmployees() {
        return employeeRedisTemplate.opsForHash().values(EMPLOYEE_CACHE_NAME)
                .stream()
                .map(obj -> (Employee) obj)
                .collect(Collectors.toList());
    }

    @Override
    public int getOverallWaitTime(String companyId, Role role) {
        double avgStudentsInEmployeeQueues = virtualQueueWorkflow
                .getVirtualQueueData(companyId, role).getEmployeeIds().stream()
                .mapToLong(this::getEmployeeQueueSize)
                .average()
                .orElse(0);

        int numStudents = (int) (avgStudentsInEmployeeQueues +
                getVirtualQueueSize(companyId, role));
        return getVirtualQueueWaitTime(companyId, role, numStudents);
    }

    @Override
    public long getVirtualQueueSize(String companyId, Role role) {
        return virtualQueueWorkflow.size(companyId, role);
    }

    @Override
    public long getEmployeeQueueSize(String employeeId) {
        return windowQueueWorkflow.size(employeeId) + physicalQueueWorkflow.size(employeeId);
    }

//    @Override
//    public void clearAll() {
//        Collection<String> keys = studentRedisTemplate.keys("*");  // redis magic
//        if (keys != null) {
//            studentRedisTemplate.delete(keys);
//        }
//    }
//
//    @Override
//    public String getAll() {
//        Collection<String> keys = studentRedisTemplate.keys("*");
//        StringBuilder stringBuilder = new StringBuilder();
//        stringBuilder.append("\n\n\n");
//        stringBuilder.append("*****************************\n");
//        stringBuilder.append("All keys:" + keys + "\n");
//        if (keys != null) {
//            for (String key: keys) {
//                stringBuilder.append(key + ":\n");
//                try {
//                    List<Student> list = queueRedisTemplate.opsForList().range(key, 0L, -1L);
//                    stringBuilder.append("\t" + list + "\n");
//                } catch(Exception e) {
//                    // redis magic
//                    Map<Object, Object> map = studentRedisTemplate.opsForHash().entries(key);
//                    for(Object mapKey: map.keySet()) {
//                        stringBuilder.append("\t" + mapKey + ":" + map.get(mapKey) + "\n");
//                    }
//                }
//                stringBuilder.append("--------\n");
//
//            }
//        }
//
//        stringBuilder.append("*****************************\n");
//        return stringBuilder.toString();
//
//    }

    /**
     * Removes the student from the given employee's queue
     *
     * @param employeeId id of the employee
     * @param employeeQueueData data of the queue for the employee
     * @return RemoveStudentResponse
     */
    RemoveStudentResponse removeStudentFromQueue(String employeeId,
                                                 EmployeeQueueData employeeQueueData) {
        Employee employee = getEmployeeWithId(employeeId);
        String companyId = employee.getCompanyId();
        Role role = employee.getRole();

        Student studentAtHead = virtualQueueWorkflow.getStudentAtHead(companyId, role);
        if (studentAtHead != null) {
            shiftStudentToWindow(companyId, employeeId, role, studentAtHead);
        }
        return new RemoveStudentResponse(employeeQueueData);
    }

    /**
     * Get the status of the student in a queue
     *
     * @param studentId id of the student
     * @return StudentQueueStatus
     * @throws InvalidRequestException if student is not present in any queue
     */
    StudentQueueStatus getStudentQueueStatus(String studentId) {
        StudentQueueStatus studentQueueStatus = (StudentQueueStatus) studentRedisTemplate
                .opsForHash().get(STUDENT_CACHE_NAME, studentId);

        if (studentQueueStatus == null) {
            throw new InvalidRequestException("Student with id=" + studentId +
                    " not present in any queue");
        }

        return studentQueueStatus;
    }

    /**
     * Returns the employee with the most queue space available.
     *
     * @param companyId id of the company the employee is associated with
     * @param role role the employee is associated with
     * @return id of the employee with the most queue space available or null if no employee has
     *         queue space available
     */
    String getEmployeeWithMostQueueSpace(String companyId, Role role) {
        VirtualQueueData virtualQueueData = virtualQueueWorkflow.getVirtualQueueData(companyId,
                role);
        Set<String> employeeIds = virtualQueueData.getEmployeeIds();
        String employeeWithMostSpaceId = null;
        long maxSpaceAvailable = 0;

        for (String employeeId: employeeIds) {
            long spaceAvailable = getEmployeeQueueSpace(employeeId);

            if (spaceAvailable > maxSpaceAvailable) {
                maxSpaceAvailable = spaceAvailable;
                employeeWithMostSpaceId = employeeId;
            }
        }

        return employeeWithMostSpaceId;
    }

    /**
     * Returns the available space in employee's window and physical queue combined
     *
     * @param employeeId id of the employee
     * @return available space in employee's window and physical queue combined
     */
    long getEmployeeQueueSpace(String employeeId) {
        return MAX_EMPLOYEE_QUEUE_SIZE - physicalQueueWorkflow.size(employeeId)
                - windowQueueWorkflow.size(employeeId);
    }

    /**
     * Shifts the given student from the virtual queue to the window queue of the employee
     * associated with the given company and role
     *
     * @param companyId id of the company
     * @param employeeId id of the employee
     * @param role role the student opted for
     * @param student student to be shifted to the window queue
     * @return QueueStatus current status of the student in a queue
     */
    QueueStatus shiftStudentToWindow(String companyId, String employeeId, Role role,
                                     Student student) {
        StudentQueueStatus studentQueueStatus = virtualQueueWorkflow.leaveQueue(companyId,
                student.getId(), role);
        return windowQueueWorkflow.joinQueue(employeeId, student, studentQueueStatus);
    }

    /**
     * Checks and returns whether the employee is present at the career fair
     *
     * @param employeeId id of the employee to check for
     * @return Employee
     * @throws InvalidRequestException throws the exception if the employee is not present at the
     *      career fair
     */
    Employee getEmployeeWithId(String employeeId) throws InvalidRequestException {
        Employee employee = (Employee) employeeRedisTemplate.opsForHash().get(EMPLOYEE_CACHE_NAME,
                employeeId);
        if (employee == null) {
            throw new InvalidRequestException("No such employee with employee id=" + employeeId +
                    " exists");
        }
        return employee;
    }

    /**
     * Sets the position and wait time based on the combined structure of multiple queues
     *
     * @param queueStatus current status of the student in the queue
     */
    void setOverallPositionAndWaitTime(QueueStatus queueStatus) {
        QueueType queueType = queueStatus.getQueueType();

        switch (queueType) {

            case VIRTUAL:
                queueStatus.setPosition(queueStatus.getPosition() + MAX_EMPLOYEE_QUEUE_SIZE);
                queueStatus.setWaitTime(getVirtualQueueWaitTime(queueStatus.getCompanyId(),
                        queueStatus.getRole(), queueStatus.getPosition()));
                break;

            case WINDOW:
                Employee employee = queueStatus.getEmployee();
                int position = queueStatus.getPosition() + physicalQueueWorkflow
                        .size(employee.getId()).intValue();
                queueStatus.setPosition(position);
                queueStatus.setWaitTime(getEmployeeQueueWaitTime(employee, position));
                break;

            case PHYSICAL:
                queueStatus.setWaitTime(getEmployeeQueueWaitTime(queueStatus.getEmployee(),
                        queueStatus.getPosition()));
                break;

            default:
                throw new InvalidRequestException("QueueType mismatch");
        }
    }

    /**
     * Returns the wait time for the student in the virtual queue of a given company and role
     *
     * @param companyId id of the company whose queue the student is a part of
     * @param role role associated with the queue the student is a part of
     * @param position current *overall* position of the student
     * @return wait time in seconds
     */
    int getVirtualQueueWaitTime(String companyId, Role role, int position) {
        double sum = 0;
        Set<String> employeeIds = virtualQueueWorkflow.getVirtualQueueData(companyId, role)
                .getEmployeeIds();

        for (String id: employeeIds) {
            Employee employee = getEmployeeWithId(id);
            sum += calcEmployeeAverageTime(employee);
        }

        double waitTime = getIndexFromPosition(position) * sum / employeeIds.size();
        return (int) waitTime;
    }

    /**
     * Calculates and returns the wait time for the employee's queue
     *
     * @param employee employee whose queue's wait time is to be calculated
     * @param position position of the student
     * @return int representing the wait time in seconds
     */
    int getEmployeeQueueWaitTime(Employee employee, int position) {
        return (int) (getIndexFromPosition(position) * calcEmployeeAverageTime(employee));
    }

    /**
     * Calculates and returns the average time spent by the employee talking to a student
     *
     * @param employee The employee whose average time is to be calculated
     * @return double Average time spent by the employee talking to a student
     */
    double calcEmployeeAverageTime(Employee employee) {
        return employee.getTotalTimeSpent() * 1. / Math.max(employee.getNumRegisteredStudents(), 1);
    }

    /**
     * Returns a zero based position
     *
     * @param position one based position
     * @return int zero based position
     */
    int getIndexFromPosition(int position) {
        return Math.max(position - 1, 0);
    }
}

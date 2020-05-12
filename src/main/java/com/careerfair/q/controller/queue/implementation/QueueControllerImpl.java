package com.careerfair.q.controller.queue.implementation;

import com.careerfair.q.controller.queue.QueueController;
import com.careerfair.q.model.redis.Student;
import com.careerfair.q.util.enums.Role;
import com.careerfair.q.service.queue.QueueService;
import com.careerfair.q.service.queue.response.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("queue")
public class QueueControllerImpl implements QueueController {

    @Autowired private QueueService queueService;

    @GetMapping("/wait-time/company-id/{company-id}/role/{role}")
    @Override
    public GetWaitTimeResponse getCompanyWaitTime(@PathVariable("company-id") String companyId,
                                                  @PathVariable("role") Role role) {
        return queueService.getCompanyWaitTime(companyId, role);
    }

    @GetMapping("/wait-time/role/{role}")
    @Override
    public GetWaitTimeResponse getAllCompaniesWaitTime(@PathVariable("role") Role role) {
        return queueService.getAllCompaniesWaitTime(role);
    }

    @PostMapping("/join/company-id/{company-id}/student-id/{student-id}/role/{role}/name/{name}")
    @Override
    public JoinQueueResponse joinQueue(@PathVariable("company-id") String companyId,
                                       @PathVariable("student-id") String studentId,
                                       @PathVariable("role") Role role,
                                       @PathVariable("name") String name) {
        // TODO: remove this vvvvv and accept a student object. Also change post mapping
        Student student = new Student(studentId, name);
        return queueService.joinVirtualQueue(companyId, role, student);
    }

    @PostMapping("/join/employee-id/{employee-id}/student-id/{student-id}")
    @Override
    public JoinQueueResponse joinEmployeeQueue(@PathVariable("employee-id") String employeeId,
                                               @PathVariable("student-id") String studentId) {
        return queueService.joinEmployeeQueue(employeeId, studentId);
    }

    @DeleteMapping("/leave/company-id/{company-id}/student-id/{student-id}/role/{role}")
    @Override
    public void leaveQueue(@PathVariable("company-id") String companyId,
                           @PathVariable("student-id") String studentId,
                           @PathVariable("role") Role role) {
        queueService.leaveQueue(companyId, studentId, role);
    }

    @GetMapping("/status/student-id/{student-id}")
    @Override
    public GetQueueStatusResponse getQueueStatus(@PathVariable("student-id") String studentId) {
        return queueService.getQueueStatus(studentId);
    }

    @PostMapping("/add/company-id/{company-id}/employee-id/{employee-id}/role/{role}")
    @Override
    public AddQueueResponse addQueue(@PathVariable("company-id") String companyId,
                                     @PathVariable("employee-id") String employeeId,
                                     @PathVariable("role") Role role) {
        return queueService.addQueue(companyId, employeeId, role);
    }

    @GetMapping("/data/employee-id/{employee-id}")
    @Override
    public GetEmployeeQueueDataResponse getEmployeeQueueData(@PathVariable("employee-id") String employeeId) {
        return queueService.getEmployeeQueueData(employeeId);
    }

    @PutMapping("/status/employee-id/{employee-id}")
    @Override
    public PauseQueueResponse pauseQueue(@PathVariable("employee-id") String employeeId) {
        return queueService.pauseQueue(employeeId);
    }

    @PostMapping("/register-student/employee-id/{employee-id}/student-id/{student-id}")
    @Override
    public RemoveStudentResponse registerStudent(@PathVariable("employee-id") String employeeId,
                                                 @PathVariable("student-id") String studentId) {
        return queueService.registerStudent(employeeId, studentId);
    }

    @DeleteMapping("/remove-student/employee-id/{employee-id}/student-id/{student-id}")
    @Override
    public RemoveStudentResponse removeStudent(@PathVariable("employee-id") String employeeId,
                                               @PathVariable("student-id") String studentId) {
        return queueService.skipStudent(employeeId, studentId);
    }

    @DeleteMapping("/clearAll")
    public void clearAll() {
        queueService.clearAll();
    }

    @GetMapping("/getAll")
    public String getAll() {
        return queueService.getAll();
    }
}

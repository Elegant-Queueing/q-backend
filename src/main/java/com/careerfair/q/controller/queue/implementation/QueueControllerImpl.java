package com.careerfair.q.controller.queue.implementation;

import com.careerfair.q.controller.queue.QueueController;
import com.careerfair.q.service.queue.QueueService;
import com.careerfair.q.service.queue.request.JoinQueueRequest;
import com.careerfair.q.service.queue.response.*;
import com.careerfair.q.util.enums.Role;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("queue")
public class QueueControllerImpl implements QueueController {

    @Autowired private QueueService queueService;

    @PostMapping("/join/company-id/{company-id}/role/{role}")
    @Override
    public JoinQueueResponse joinQueue(@PathVariable("company-id") String companyId,
                                       @PathVariable("role") Role role,
                                       @RequestBody JoinQueueRequest joinQueueRequest) {
        return queueService.joinVirtualQueue(companyId, role, joinQueueRequest.getStudent());
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
    public GetEmployeeQueueDataResponse getEmployeeQueueData(
            @PathVariable("employee-id") String employeeId) {
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

//    @DeleteMapping("/clearAll")
//    public void clearAll() {
//        queueService.clearAll();
//    }
//
//    @GetMapping("/getAll")
//    public String getAll() {
//        return queueService.getAll();
//    }
}

package com.careerfair.q.student.controller.implementation;

import com.careerfair.q.student.controller.StudentController;
import com.careerfair.q.student.service.StudentService;
import com.careerfair.q.student.workflow.request.AddStudentRequest;
import com.careerfair.q.student.workflow.request.UpdateStudentRequest;
import com.careerfair.q.student.workflow.response.AddStudentResponse;
import com.careerfair.q.student.workflow.response.DeleteStudentResponse;
import com.careerfair.q.student.workflow.response.GetStudentResponse;
import com.careerfair.q.student.workflow.response.UpdateStudentResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("student")
public class StudentControllerImpl implements StudentController {

    @Autowired private StudentService studentService;

    @GetMapping("/get/{id}")
    @Override
    public GetStudentResponse getStudent(@PathVariable("id") String id) {
        return studentService.getStudent(id);
    }

    @PutMapping("/update/{id}")
    @Override
    public UpdateStudentResponse updateStudent(@PathVariable("id") String id,
                                               @RequestBody UpdateStudentRequest updateStudentRequest) {
        return studentService.updateStudent(id, updateStudentRequest);
    }

    @DeleteMapping("/delete/{id}")
    @Override
    public DeleteStudentResponse deleteStudent(@PathVariable("id") String id) {
        return studentService.deleteStudent(id);
    }

    @PostMapping("/add")
    @Override
    public AddStudentResponse addStudent(@RequestBody AddStudentRequest addStudentRequest) {
        return studentService.addStudent(addStudentRequest);
    }

    @PutMapping("/upload-resume/{id}")
    @Override
    public UpdateStudentResponse uploadStudentResume(@PathVariable("id") String id,
                                                     @RequestBody UpdateStudentRequest uploadStudentResume) {
        return studentService.uploadStudentResume(id, uploadStudentResume);
    }

    @GetMapping("/")
    @Override
    public String ping() {
        studentService.testDatabaseConnection();
        return "Pong";
    }
}

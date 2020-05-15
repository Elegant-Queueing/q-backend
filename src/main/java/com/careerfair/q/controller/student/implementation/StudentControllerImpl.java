package com.careerfair.q.controller.student.implementation;

import com.careerfair.q.controller.student.StudentController;
import com.careerfair.q.service.student.StudentService;
import com.careerfair.q.service.student.request.AddStudentRequest;
import com.careerfair.q.service.student.request.UpdateStudentRequest;
import com.careerfair.q.service.student.response.AddStudentResponse;
import com.careerfair.q.service.student.response.DeleteStudentResponse;
import com.careerfair.q.service.student.response.GetStudentResponse;
import com.careerfair.q.service.student.response.UpdateStudentResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("student")
public class StudentControllerImpl implements StudentController {

    @Autowired private StudentService studentService;

    @GetMapping("/get/student-id/{student-id}")
    @Override
    public GetStudentResponse getStudentWithId(@PathVariable("student-id") String studentId) {
        return studentService.getStudentWithId(studentId);
    }

    @GetMapping("/get/email/{email}")
    @Override
    public GetStudentResponse getStudentWithEmail(@PathVariable("email") String email) {
        return studentService.getStudentWithEmail(email);
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

    @GetMapping()
    @Override
    public String ping() {
        studentService.testDatabaseConnection();
        return "Pong";
    }
}

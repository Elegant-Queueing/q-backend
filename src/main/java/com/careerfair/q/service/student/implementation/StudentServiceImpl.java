package com.careerfair.q.service.student.implementation;

import com.careerfair.q.service.database.StudentFirebase;
import com.careerfair.q.service.student.StudentService;
import com.careerfair.q.service.student.request.AddStudentRequest;
import com.careerfair.q.service.student.request.UpdateStudentRequest;
import com.careerfair.q.service.student.response.AddStudentResponse;
import com.careerfair.q.service.student.response.DeleteStudentResponse;
import com.careerfair.q.service.student.response.GetStudentResponse;
import com.careerfair.q.service.student.response.UpdateStudentResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class StudentServiceImpl implements StudentService {

    @Autowired private StudentFirebase studentFirebase;

    @Override
    public GetStudentResponse getStudent(String id) {
        // TODO
        return null;
    }

    @Override
    public UpdateStudentResponse updateStudent(String id, UpdateStudentRequest updateStudentRequest) {
        // TODO
        return null;
    }

    @Override
    public DeleteStudentResponse deleteStudent(String id) {
        // TODO
        return null;
    }

    @Override
    public AddStudentResponse addStudent(AddStudentRequest addStudentRequest) {
        // TODO
        return null;
    }

    @Override
    public UpdateStudentResponse uploadStudentResume(String id, UpdateStudentRequest uploadStudentResume) {
        // TODO
        return null;
    }

    @Override
    public void testDatabaseConnection() {
        studentFirebase.test();
    }
}

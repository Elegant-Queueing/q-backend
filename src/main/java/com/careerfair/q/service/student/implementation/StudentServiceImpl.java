package com.careerfair.q.service.student.implementation;

import com.careerfair.q.service.database.StudentFirebase;
import com.careerfair.q.service.student.StudentService;
import com.careerfair.q.service.student.request.AddStudentRequest;
import com.careerfair.q.service.student.request.UpdateStudentRequest;
import com.careerfair.q.service.student.response.*;
import com.careerfair.q.util.exception.FirebaseException;
import com.careerfair.q.util.exception.InvalidRequestException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.concurrent.ExecutionException;

@Service
public class StudentServiceImpl implements StudentService {

    @Autowired private StudentFirebase studentFirebase;

    @Override
    public GetStudentResponse getStudentWithId(String studentId) {
        try {
            return new GetStudentResponse(studentFirebase.getStudentWithId(studentId));
        } catch (ExecutionException | InterruptedException | FirebaseException ex) {
            throw new InvalidRequestException(ex.getMessage());
        }
    }

    @Override
    public GetStudentResponse getStudentWithEmail(String email) {
        try {
            return new GetStudentResponse(studentFirebase.getStudentWithEmail(email));
        } catch (ExecutionException | InterruptedException | FirebaseException ex) {
            throw new InvalidRequestException(ex.getMessage());
        }
    }

    @Override
    public UpdateStudentResponse updateStudent(String id, UpdateStudentRequest updateStudent) {
        try {
            return new UpdateStudentResponse(studentFirebase.updateStudent(id, updateStudent));
        } catch (ExecutionException | InterruptedException | FirebaseException ex) {
            throw new InvalidRequestException(ex.getMessage());
        }
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
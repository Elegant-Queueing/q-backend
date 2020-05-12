package com.careerfair.q.service.student.implementation;

import com.careerfair.q.model.db.Student;
import com.careerfair.q.service.database.StudentFirebase;
import com.careerfair.q.service.student.StudentService;
import com.careerfair.q.service.student.request.AddStudentRequest;
import com.careerfair.q.service.student.request.UpdateStudentRequest;
import com.careerfair.q.service.student.response.AddStudentResponse;
import com.careerfair.q.service.student.response.DeleteStudentResponse;
import com.careerfair.q.service.student.response.GetStudentResponse;
import com.careerfair.q.service.student.response.UpdateStudentResponse;
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
            Student student = studentFirebase.getStudentWithId(studentId);
            if (student == null) {
                throw new InvalidRequestException("No student with student id=" + studentId +
                        " exists");
            }
            return new GetStudentResponse(student);
        } catch (ExecutionException | InterruptedException ex) {
            throw new InvalidRequestException(ex.getMessage());
        }
    }

    @Override
    public GetStudentResponse getStudentWithEmail(String email) {
        try {
            Student student = studentFirebase.getStudentWithEmail(email);
            if (student == null) {
                throw new InvalidRequestException("No student with email=" + email + " exists");
            }
            return new GetStudentResponse(student);
        } catch (ExecutionException | InterruptedException ex) {
            throw new InvalidRequestException(ex.getMessage());
        }
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

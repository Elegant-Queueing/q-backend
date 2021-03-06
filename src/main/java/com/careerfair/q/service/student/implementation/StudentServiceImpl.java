package com.careerfair.q.service.student.implementation;

import com.careerfair.q.model.db.Student;
import com.careerfair.q.service.database.FirebaseService;
import com.careerfair.q.service.student.StudentService;
import com.careerfair.q.service.student.request.AddStudentRequest;
import com.careerfair.q.service.student.request.StudentRequest;
import com.careerfair.q.service.student.request.UpdateStudentRequest;
import com.careerfair.q.service.student.response.AddStudentResponse;
import com.careerfair.q.service.student.response.DeleteStudentResponse;
import com.careerfair.q.service.student.response.GetStudentResponse;
import com.careerfair.q.service.student.response.UpdateStudentResponse;
import com.careerfair.q.service.validation.ValidationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class StudentServiceImpl implements StudentService {

    @Autowired private FirebaseService firebaseService;
    @Autowired private ValidationService validationService;

    @Override
    public GetStudentResponse getStudentWithId(String studentId) {
        return new GetStudentResponse(firebaseService.getStudentWithId(studentId));
    }

    @Override
    public GetStudentResponse getStudentWithEmail(String email) {
        return new GetStudentResponse(firebaseService.getStudentWithEmail(email));
    }

    @Override
    public UpdateStudentResponse updateStudent(String studentId,
                                               UpdateStudentRequest updateStudentRequest) {
        validationService.checkValidStudentRequest(updateStudentRequest);
        Student updateStudent = firebaseService.updateStudent(studentId,
                createStudentFromRequest(updateStudentRequest));
        return new UpdateStudentResponse(updateStudent);
    }

    @Override
    public DeleteStudentResponse deleteStudent(String studentId) {
        return new DeleteStudentResponse(firebaseService.deleteStudent(studentId));
    }

    @Override
    public AddStudentResponse addStudent(AddStudentRequest addStudentRequest) {
        validationService.checkValidStudentRequest(addStudentRequest);
        Student studentFromRequest = createStudentFromRequest(addStudentRequest);
        Student newStudent = firebaseService.addStudent(studentFromRequest);
        return new AddStudentResponse(newStudent);
    }

    @Override
    public UpdateStudentResponse uploadStudentResume(String id,
                                                     UpdateStudentRequest uploadStudentResume) {
        // TODO
        return null;
    }

    @Override
    public void testDatabaseConnection() {
        firebaseService.test();
    }

    // Helper method to convert studentRequest object to a Student object
    <T extends StudentRequest> Student createStudentFromRequest(T studentRequest) {
        Student student = new Student();
        student.firstName = studentRequest.getFirstName();
        student.lastName = studentRequest.getLastName();
        student.universityId = studentRequest.getUniversityId();
        student.major = studentRequest.getMajor();
        student.role = studentRequest.getRole();
        student.bio = studentRequest.getBio();
        student.email = studentRequest.getEmail();
        student.gpa = studentRequest.getGpa();
        student.graduationDate = studentRequest.getGraduationDate();
        student.international = studentRequest.getInternational();
        return student;
    }
}
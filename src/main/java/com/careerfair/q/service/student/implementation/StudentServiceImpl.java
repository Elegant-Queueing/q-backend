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

    private final FirebaseService firebaseService;
    private final ValidationService validationService;

    public StudentServiceImpl(@Autowired FirebaseService firebaseService,
                              @Autowired ValidationService validationService) {
        this.firebaseService = firebaseService;
        this.validationService = validationService;
    }


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

    private <T extends StudentRequest> Student createStudentFromRequest(T studentRequest) {
        Student student = new Student();
        student.firstName = studentRequest.firstName;
        student.lastName = studentRequest.lastName;
        student.universityId = studentRequest.universityId;
        student.major = studentRequest.major;
        student.role = studentRequest.role;
        student.bio = studentRequest.bio;
        student.email = studentRequest.email;
        student.gpa = studentRequest.gpa;
        student.graduationDate = studentRequest.graduationDate;
        student.international = studentRequest.international;
        return student;
    }

    @Override
    public DeleteStudentResponse deleteStudent(String studentId) {
        return new DeleteStudentResponse(firebaseService.deleteStudent(studentId));
    }

    @Override
    public AddStudentResponse addStudent(AddStudentRequest addStudentRequest) {
        validationService.checkValidStudentRequest(addStudentRequest);
        Student studentFromRequest = createStudentFromRequest(addStudentRequest);
        Student addedStudent = firebaseService.addStudent(studentFromRequest);
        return new AddStudentResponse(addedStudent);
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
}
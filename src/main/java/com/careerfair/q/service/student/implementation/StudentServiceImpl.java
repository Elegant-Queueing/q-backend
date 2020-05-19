package com.careerfair.q.service.student.implementation;

import com.careerfair.q.model.db.Student;
import com.careerfair.q.service.database.FirebaseService;
import com.careerfair.q.service.student.StudentService;
import com.careerfair.q.service.student.request.AddStudentRequest;
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
    @Autowired private ValidationService validationService;

    public StudentServiceImpl(@Autowired FirebaseService firebaseService) {
        this.firebaseService = firebaseService;
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
        validationService.checkValidStudentUpdateRequest(updateStudentRequest);
        Student updateStudent = firebaseService.updateStudent(studentId,
                createStudentFromUpdateRequest(updateStudentRequest));
        return new UpdateStudentResponse(updateStudent);
    }

    private Student createStudentFromUpdateRequest(UpdateStudentRequest updateStudentRequest) {
        // Other way is to use BeanUtils but that uses reflection under the hood
        Student student = new Student();
        student.firstName = updateStudentRequest.firstName;
        student.lastName = updateStudentRequest.lastName;
        student.universityId = updateStudentRequest.universityId;
        student.major = updateStudentRequest.major;
        student.role = updateStudentRequest.role;
        student.bio = updateStudentRequest.bio;
        student.email = updateStudentRequest.email;
        student.gpa = updateStudentRequest.gpa;
        student.graduationDate = updateStudentRequest.graduationDate;
        student.international = updateStudentRequest.international;
        return student;
    }

    @Override
    public DeleteStudentResponse deleteStudent(String id) {
        // TODO
        return null;
    }

    @Override
    public AddStudentResponse addStudent(AddStudentRequest addStudentRequest) {
        validationService.checkValidStudentAddRequest(addStudentRequest);
        Student studentFromRequest = createStudentFromAddRequest(addStudentRequest);
        Student addedStudent = firebaseService.addStudent(studentFromRequest);
        return new AddStudentResponse(addedStudent);
    }


    private Student createStudentFromAddRequest(AddStudentRequest addStudentRequest) {
        // Other way is to use BeanUtils but that uses reflection under the hood
        Student student = new Student();
        student.firstName = addStudentRequest.firstName;
        student.lastName = addStudentRequest.lastName;
        student.universityId = addStudentRequest.universityId;
        student.major = addStudentRequest.major;
        student.role = addStudentRequest.role;
        student.bio = addStudentRequest.bio;
        student.email = addStudentRequest.email;
        student.gpa = addStudentRequest.gpa;
        student.graduationDate = addStudentRequest.graduationDate;
        student.international = addStudentRequest.international;
        return student;
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
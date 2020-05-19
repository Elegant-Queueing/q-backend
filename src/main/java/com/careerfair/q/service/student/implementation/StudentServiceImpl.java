package com.careerfair.q.service.student.implementation;

import com.careerfair.q.service.database.FirebaseService;
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

    private static final String ID = "efPdfJkESbSRX1KfVyD0tF:APA91bFdpTGvXwA3pN6yQksV1oeQFeOyEY-GuJ9HmTW2XoiZeeRUNd19TApPNny8xn3etYILvrxbbRlf0qZuOVOKQ7hwvJfY-P6G_nu2e6po0TTnARg-WzfH6F11ujIMDCajHaBCR_DJ";

    private final FirebaseService firebaseService;

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
    public String getRegistrationToken(String studentId) {
        return ID;
    }

    @Override
    public void testDatabaseConnection() {
        firebaseService.test();
    }
}

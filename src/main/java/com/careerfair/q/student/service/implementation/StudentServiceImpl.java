package com.careerfair.q.student.service.implementation;

import com.careerfair.q.student.service.StudentService;
import com.careerfair.q.student.workflow.StudentWorkflow;
import com.careerfair.q.student.workflow.request.AddStudentRequest;
import com.careerfair.q.student.workflow.request.UpdateStudentRequest;
import com.careerfair.q.student.workflow.response.AddStudentResponse;
import com.careerfair.q.student.workflow.response.DeleteStudentResponse;
import com.careerfair.q.student.workflow.response.GetStudentResponse;
import com.careerfair.q.student.workflow.response.UpdateStudentResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class StudentServiceImpl implements StudentService {

    @Autowired private StudentWorkflow studentWorkflow;

    @Override
    public GetStudentResponse getStudent(String id) {
        return studentWorkflow.getStudent(id);
    }

    @Override
    public UpdateStudentResponse updateStudent(String id, UpdateStudentRequest updateStudentRequest) {
        return studentWorkflow.updateStudent(id, updateStudentRequest);
    }

    @Override
    public DeleteStudentResponse deleteStudent(String id) {
        return studentWorkflow.deleteStudent(id);
    }

    @Override
    public AddStudentResponse addStudent(AddStudentRequest addStudentRequest) {
        return studentWorkflow.addStudent(addStudentRequest);
    }

    @Override
    public UpdateStudentResponse uploadStudentResume(String id, UpdateStudentRequest uploadStudentResume) {
        return studentWorkflow.uploadStudentResume(id, uploadStudentResume);
    }
}

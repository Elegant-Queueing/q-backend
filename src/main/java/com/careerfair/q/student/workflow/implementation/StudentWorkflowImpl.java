package com.careerfair.q.student.workflow.implementation;

import com.careerfair.q.database.service.student.StudentFirebase;
import com.careerfair.q.database.service.student.implementation.StudentFirebaseImpl;
import com.careerfair.q.student.workflow.StudentWorkflow;
import com.careerfair.q.student.workflow.request.AddStudentRequest;
import com.careerfair.q.student.workflow.request.UpdateStudentRequest;
import com.careerfair.q.student.workflow.response.AddStudentResponse;
import com.careerfair.q.student.workflow.response.DeleteStudentResponse;
import com.careerfair.q.student.workflow.response.GetStudentResponse;
import com.careerfair.q.student.workflow.response.UpdateStudentResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class StudentWorkflowImpl implements StudentWorkflow {

    @Autowired private StudentFirebaseImpl studentFirebase;

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
        studentFirebase.test();
        return null;
    }

    @Override
    public UpdateStudentResponse uploadStudentResume(String id, UpdateStudentRequest uploadStudentResume) {
        // TODO
        return null;
    }
}

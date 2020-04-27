package com.careerfair.q.student.controller;

import com.careerfair.q.student.workflow.request.AddStudentRequest;
import com.careerfair.q.student.workflow.request.UpdateStudentRequest;
import com.careerfair.q.student.workflow.response.AddStudentResponse;
import com.careerfair.q.student.workflow.response.DeleteStudentResponse;
import com.careerfair.q.student.workflow.response.GetStudentResponse;
import com.careerfair.q.student.workflow.response.UpdateStudentResponse;

public interface StudentController {

    /**
     *
     * @param id
     * @return
     */
    GetStudentResponse getStudent(String id);

    /**
     *
     * @param id
     * @param updateStudentRequest
     * @return
     */
    UpdateStudentResponse updateStudent(String id, UpdateStudentRequest updateStudentRequest);

    /**
     *
     * @param id
     * @return
     */
    DeleteStudentResponse deleteStudent(String id);

    /**
     *
     * @param addStudentRequest
     * @return
     */
    AddStudentResponse addStudent(AddStudentRequest addStudentRequest);

    /**
     *
     * @param id
     * @param uploadStudentResume
     * @return
     */
    UpdateStudentResponse uploadStudentResume(String id, UpdateStudentRequest uploadStudentResume);
}

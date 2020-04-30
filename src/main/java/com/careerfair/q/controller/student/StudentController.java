package com.careerfair.q.controller.student;

import com.careerfair.q.service.student.request.AddStudentRequest;
import com.careerfair.q.service.student.request.UpdateStudentRequest;
import com.careerfair.q.service.student.response.AddStudentResponse;
import com.careerfair.q.service.student.response.DeleteStudentResponse;
import com.careerfair.q.service.student.response.GetStudentResponse;
import com.careerfair.q.service.student.response.UpdateStudentResponse;

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

    /**
     *
     * @return
     */
    String ping();
}

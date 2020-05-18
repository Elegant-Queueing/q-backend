package com.careerfair.q.controller.student;

import com.careerfair.q.service.student.request.AddStudentRequest;
import com.careerfair.q.service.student.request.UpdateStudentRequest;
import com.careerfair.q.service.student.response.AddStudentResponse;
import com.careerfair.q.service.student.response.DeleteStudentResponse;
import com.careerfair.q.service.student.response.GetStudentResponse;
import com.careerfair.q.service.student.response.UpdateStudentResponse;

public interface StudentController {

    /**
     * Gets the student with the given id
     *
     * @param studentId id of the student to retrieve
     * @return GetStudentResponse
     */
    GetStudentResponse getStudentWithId(String studentId);

    /**
     * Gets the student with the given email
     *
     * @param email email of the student to retrieve
     * @return GetStudentResponse
     */
    GetStudentResponse getStudentWithEmail(String email);

    /**
     * Updates the student given the changes
     *
     * @param studentId id of the student to update
     * @param updateStudentRequest Request object holding all the changes to the student
     * @return UpdateStudentResponse
     */
    UpdateStudentResponse updateStudent(String studentId,
                                        UpdateStudentRequest updateStudentRequest);

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
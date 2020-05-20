package com.careerfair.q.service.student;

import com.careerfair.q.service.student.request.AddStudentRequest;
import com.careerfair.q.service.student.request.UpdateStudentRequest;
import com.careerfair.q.service.student.response.AddStudentResponse;
import com.careerfair.q.service.student.response.DeleteStudentResponse;
import com.careerfair.q.service.student.response.GetStudentResponse;
import com.careerfair.q.service.student.response.UpdateStudentResponse;

public interface StudentService {

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
     * Deletes the student with the given id
     *
     * @param studentId id of the student to be deleted
     * @return DeleteStudentResponse
     */
    DeleteStudentResponse deleteStudent(String studentId);

    /**
     * Adds a student with the given details in the request
     *
     * @param addStudentRequest details of the student to be added
     * @return AddStudentResponse
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
     */
    void testDatabaseConnection();
}
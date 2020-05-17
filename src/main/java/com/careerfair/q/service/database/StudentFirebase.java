package com.careerfair.q.service.database;

import com.careerfair.q.model.db.Student;
import com.careerfair.q.service.student.request.UpdateStudentRequest;

import java.util.concurrent.ExecutionException;

public interface StudentFirebase {
    /**
     *
     */
    void test();

    /**
     * Registers the given student's talk with the given employee
     *
     * @param studentId id of student to register
     * @param employeeId id of the employee that the student has finished talking to
     * @return true if the registration is successful
     */
    boolean registerStudent(String studentId, String employeeId) throws ExecutionException, InterruptedException;

    /**
     * Returns the data of the student associated with the given id
     *
     * @param studentId id of the student to be retrieved
     * @return Student
     */
    Student getStudentWithId(String studentId) throws ExecutionException, InterruptedException;

    /**
     * Returns the data of the student associated with the given email
     *
     * @param email email of the student
     * @return Student
     */
    Student getStudentWithEmail(String email) throws ExecutionException, InterruptedException;


    /**
     * Updates all the fields that the user changes
     *
     * @param studentId
     * @param updateStudent
     * @return Student
     * @throws ExecutionException
     * @throws InterruptedException
     */
    Student updateStudent(String studentId, UpdateStudentRequest updateStudent) throws ExecutionException, InterruptedException;
}
package com.careerfair.q.service.database;

import com.careerfair.q.model.db.Student;
import java.util.Map;
import java.util.concurrent.ExecutionException;

public interface StudentFirebase {
    /**
     *
     */
    void test();

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
     * @param updatedValues
     * @return Student
     * @throws ExecutionException
     * @throws InterruptedException
     */
    Student updateStudent(String studentId, Map<String, Object> updatedValues) throws ExecutionException, InterruptedException;
}
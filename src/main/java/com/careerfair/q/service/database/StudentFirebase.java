package com.careerfair.q.service.database;

import com.careerfair.q.model.db.Student;

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
}

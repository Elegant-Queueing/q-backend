package com.careerfair.q.workflow.database;

import com.careerfair.q.model.db.Student;
import com.careerfair.q.util.exception.FirebaseException;

public interface StudentFirebaseWorkflow {

    /**
     * Checks whether the student with the given id exists in the database
     *
     * @param studentId id of the student to validate
     * @throws FirebaseException if the student with id does not exists in the database
     */
    void checkValidStudentId(String studentId) throws FirebaseException;

    /**
     * Returns the data of the student associated with the given id
     *
     * @param studentId id of the student to be retrieved
     * @throws FirebaseException if the student with id is not present in the database
     * @return Student
     */
    Student getStudentWithId(String studentId) throws FirebaseException;

    /**
     * Returns the data of the student associated with the given email
     *
     * @param email email of the student
     * @throws FirebaseException if the student with id is not present in the database
     * @return Student
     */
    Student getStudentWithEmail(String email) throws FirebaseException;

    /**
     * Registers the given student's talk with the given employee
     *
     * @param studentId id of student to register
     * @param employeeId id of the employee that the student has finished talking to
     * @throws FirebaseException if the student or employee do not exist
     */
    void registerEmployeeToStudent(String studentId, String employeeId) throws FirebaseException;
}

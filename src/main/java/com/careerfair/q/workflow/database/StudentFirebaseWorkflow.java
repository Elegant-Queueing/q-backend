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
     * @throws FirebaseException if the student does not exist
     */
    void registerEmployeeToStudent(String studentId, String employeeId) throws FirebaseException;

    /**
     * Updates all the fields that the user changes
     *
     * @param studentId id of the student to make changes to
     * @param updatedStudent Student object with all the changes made
     * @return Student
     * @throws FirebaseException if the student does not exist
     */
    Student updateStudent(String studentId, Student updatedStudent) throws FirebaseException;

    /**
     * Adds a student with the given details
     *
     * @param newStudent id of the student to make changes to
     * @return Student
     */
    Student addStudent(Student newStudent);

    /**
     * Deletes the student with the given student id
     *
     * @param studentId id of the student to be deleted
     * @return Student that was deleted
     */
    Student deleteStudent(String studentId);
}

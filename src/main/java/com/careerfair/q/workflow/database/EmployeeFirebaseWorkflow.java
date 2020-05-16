package com.careerfair.q.workflow.database;

import com.careerfair.q.model.db.Employee;
import com.careerfair.q.util.exception.FirebaseException;

public interface EmployeeFirebaseWorkflow {

    /**
     * Checks whether the employee with the given id exists in the database
     *
     * @param employeeId id of the employee to validate
     * @throws FirebaseException if the employee with id does not exists in the database
     */
    void checkValidEmployeeId(String employeeId) throws FirebaseException;

    /**
     * Gets the employee profile from firebase
     *
     * @param employeeId id of the employee whose data is to retrieved
     * @return Employee
     */
    Employee getEmployeeWithId(String employeeId) throws FirebaseException;

    /**
     * Gets the employee profile from firebase
     *
     * @param email email of the employee whose data is to retrieved
     * @return Employee
     */
    Employee getEmployeeWithEmail(String email) throws FirebaseException;

    /**
     * Registers the given student's talk with the given employee
     *
     * @param employeeId id of the employee that the student has finished talking to
     * @param studentId id of student to register
     * @throws FirebaseException if the student or employee do not exist
     */
    void registerStudentToEmployee(String employeeId, String studentId) throws FirebaseException;
}

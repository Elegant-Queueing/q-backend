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

    /**
     * Updates the employee with the given details
     *
     * @param employeeId id of the employee to be updated
     * @param updatedEmployee employee object with the updated fields
     * @return Employee object with the updated fields
     * @throws FirebaseException if there are problems with writing the data to the DB
     */
    Employee updateEmployee(String employeeId, Employee updatedEmployee)
            throws FirebaseException;

    /**
     * Adds an employee with the given details
     *
     * @param newEmployee employee that is to be added
     * @return Employee object that was added to the DB
     * @throws FirebaseException if there are problems with writing the data to the DB
     */
    Employee addEmployee(Employee newEmployee) throws FirebaseException;

    /**
     * Deletes an employee with the given employeeId
     *
     * @param employeeId id of the employee that is to be deleted
     * @return Employee object that was deleted
     * @throws FirebaseException if there are problems with writing the data to the DB
     */
    Employee deleteEmployee(String employeeId) throws FirebaseException;
}

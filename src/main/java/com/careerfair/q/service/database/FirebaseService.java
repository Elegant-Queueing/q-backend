package com.careerfair.q.service.database;

import com.careerfair.q.model.db.Company;
import com.careerfair.q.model.db.Employee;
import com.careerfair.q.model.db.Fair;
import com.careerfair.q.model.db.Student;
import com.careerfair.q.model.exchange.StudentDTO;
import com.careerfair.q.util.exception.FirebaseException;

import java.util.List;

public interface FirebaseService {

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
     * @throws FirebaseException if the employee with id does not exists in the database
     * @return Employee
     */
    Employee getEmployeeWithId(String employeeId) throws FirebaseException;

    /**
     * Gets the employee profile from firebase
     *
     * @param email email of the employee whose data is to retrieved
     * @throws FirebaseException if the employee with email does not exists in the database
     * @return Employee
     */
    Employee getEmployeeWithEmail(String email) throws FirebaseException;

    /**
     * Checks whether the company with the given id exists in the database
     *
     * @param companyId id of the company to validate
     * @throws FirebaseException if the company with id does not exists in the database
     */
    void checkValidCompanyId(String companyId) throws FirebaseException;

    /**
     * Gets the data of the company with the given id
     *
     * @param fairId id of the fair the company is present in
     * @param companyId id of the company to retrieve
     * @throws FirebaseException if the company with id does not exists in the database
     * @return Company
     */
    Company getCompanyWithId(String fairId, String companyId) throws FirebaseException;

    /**
     * Gets the fair associated with the given id
     *
     * @param fairId id of the fair
     * @throws FirebaseException if the fair with id does not exists in the database
     * @return Fair
     */
    Fair getFairWithId(String fairId) throws FirebaseException;

    /**
     * Gets all the fairs from firebase
     *
     * @throws FirebaseException if something unexpected happens with Firebase
     * @return a list of all the fairs
     */
    List<Fair> getAllFairs() throws FirebaseException;

    /**
     * Registers the given student's talk with the given employee
     *
     * @param studentId id of student to register
     * @param employeeId id of the employee that the student has finished talking to
     * @throws FirebaseException if the student or employee do not exist
     */
    void registerStudent(String studentId, String employeeId) throws FirebaseException;

    /**
     * Updates the student given the changes
     *
     * @param studentId id of the student to update
     * @param student student object with the updated fields
     * @return Student
     */
    Student updateStudent(String studentId, Student student);

    /**
     * Adds a student with the given details
     *
     * @param addedStudent id of the student to make changes to
     * @return StudentDTO
     */
    StudentDTO addStudent(StudentDTO addedStudent);

    /**
     * Test firebase connection
     */
    void test();
}

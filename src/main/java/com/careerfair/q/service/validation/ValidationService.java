package com.careerfair.q.service.validation;

import com.careerfair.q.service.student.request.AddStudentRequest;
import com.careerfair.q.service.student.request.UpdateStudentRequest;
import com.careerfair.q.util.enums.Role;
import com.careerfair.q.util.exception.ValidationException;

public interface ValidationService {

    /**
     * Checks whether the student with the given id exists in the database
     *
     * @param studentId id of the student to validate
     * @throws ValidationException if the student with id does not exists in the database
     */
    void checkValidStudentId(String studentId) throws ValidationException;

    /**
     * Checks whether the employee with the given id exists in the database
     *
     * @param employeeId id of the employee to validate
     * @throws ValidationException if the employee with id does not exists in the database
     */
    void checkValidEmployeeId(String employeeId) throws ValidationException;

    /**
     * Checks whether the company with the given id exists in the database
     *
     * @param companyId id of the company to validate
     * @throws ValidationException if the company with id does not exists in the database
     */
    void checkValidCompanyId(String companyId) throws ValidationException;

    /**
     * Checks whether the employee with given id is associated with the given company and role
     *
     * @param companyId id of the company the employee is to be associated with
     * @param employeeId id of the employee to validate
     * @param role role that employee is associated with
     * @throws ValidationException if the employee with given id is not associated with the given
     *      company and role
     */
    void checkEmployeeAssociations(String companyId, String employeeId, Role role)
            throws ValidationException;

    public void checkValidStudentUpdateRequest(UpdateStudentRequest updateStudentRequest)
            throws ValidationException;

    public void checkValidStudentAddRequest(AddStudentRequest addStudentRequest)
            throws ValidationException;
}

package com.careerfair.q.controller.employee;

import com.careerfair.q.service.employee.request.AddEmployeeRequest;
import com.careerfair.q.service.employee.request.UpdateEmployeeRequest;
import com.careerfair.q.service.employee.response.AddEmployeeResponse;
import com.careerfair.q.service.employee.response.DeleteEmployeeResponse;
import com.careerfair.q.service.employee.response.GetEmployeeResponse;
import com.careerfair.q.service.employee.response.UpdateEmployeeResponse;

public interface EmployeeController {

    /**
     * Gets the employee's profile from the database
     *
     * @param employeeId id of the employee whose data is to retrieved
     * @return GetEmployeeResponse
     */
    GetEmployeeResponse getEmployeeWithId(String employeeId);


    /**
     * Gets the employee's profile from the database
     *
     * @param email email of the employee whose data is to retrieved
     * @return GetEmployeeResponse
     */
    GetEmployeeResponse getEmployeeWithEmail(String email);

    /**
     * Updates the given employees details based on the request details
     *
     * @param employeeId id of the employee to update
     * @param updateEmployeeRequest details of what attributes are to be updated
     * @return UpdateEmployeeResponse
     */
    UpdateEmployeeResponse updateEmployee(String employeeId,
                                          UpdateEmployeeRequest updateEmployeeRequest);

    /**
     * Deletes an employee with the given employeeId
     *
     * @param employeeId id of the employee to be deleted
     * @return DeleteEmployeeResponse
     */
    DeleteEmployeeResponse deleteEmployee(String employeeId);

    /**
     * Adds an employee with the given request details
     *
     * @param addEmployeeRequest details of the employee to be added
     * @return AddEmployeeResponse
     */
    AddEmployeeResponse addEmployee(AddEmployeeRequest addEmployeeRequest);
}

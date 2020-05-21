package com.careerfair.q.service.employee;

import com.careerfair.q.service.employee.request.AddEmployeeRequest;
import com.careerfair.q.service.employee.request.UpdateEmployeeRequest;
import com.careerfair.q.service.employee.response.AddEmployeeResponse;
import com.careerfair.q.service.employee.response.DeleteEmployeeResponse;
import com.careerfair.q.service.employee.response.GetEmployeeResponse;
import com.careerfair.q.service.employee.response.UpdateEmployeeResponse;

public interface EmployeeService {

    /**
     * Gets the employee profile
     *
     * @param employeeId id of the employee whose data is to be retrieved
     * @return GetEmployeeResponse
     */
    GetEmployeeResponse getEmployeeWithId(String employeeId);

    /**
     * Gets the employee profile by their email
     *
     * @param email email of employee whose data is to be retrieved
     * @return GetEmployeeResponse
     */
    GetEmployeeResponse getEmployeeWithEmail(String email);

    /**
     * Updates the employee, with given employeeId
     *
     * @param employeeId id of the employee whose data is to be updated
     * @param updateEmployeeRequest update details for the given employeeId
     * @return UpdateEmployeeResponse
     */
    UpdateEmployeeResponse updateEmployee(String employeeId,
                                          UpdateEmployeeRequest updateEmployeeRequest);

    /**
     * Deletes the employee associated with the given employeeId
     *
     * @param employeeId id of the employee whose data is to be deleted
     * @return DeleteEmployeeResponse
     */
    DeleteEmployeeResponse deleteEmployee(String employeeId);

    /**
     * Adds a new employee with the given details
     *
     * @param addEmployeeRequest details to be added for the new employee
     * @return AddEmployeeResponse
     */
    AddEmployeeResponse addEmployee(AddEmployeeRequest addEmployeeRequest);
}

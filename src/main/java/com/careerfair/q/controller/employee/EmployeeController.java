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
     *
     * @param id
     * @param updateEmployeeRequest
     * @return
     */
    UpdateEmployeeResponse updateEmployee(String id, UpdateEmployeeRequest updateEmployeeRequest);

    /**
     *
     * @param id
     * @return
     */
    DeleteEmployeeResponse deleteEmployee(String id);

    /**
     *
     * @param addEmployeeRequest
     * @return
     */
    AddEmployeeResponse addEmployee(AddEmployeeRequest addEmployeeRequest);
}

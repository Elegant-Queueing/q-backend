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

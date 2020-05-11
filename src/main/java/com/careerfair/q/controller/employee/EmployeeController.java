package com.careerfair.q.controller.employee;

import com.careerfair.q.service.employee.request.AddEmployeeRequest;
import com.careerfair.q.service.employee.request.UpdateEmployeeRequest;
import com.careerfair.q.service.employee.response.*;

import java.util.concurrent.ExecutionException;

public interface EmployeeController {

    /**
     * Gets the employee's profile from the database
     *
     * @param id employeeId
     * @return GetEmployeeResponse
     */
    GetEmployeeResponse getEmployee(String id) throws InterruptedException, ExecutionException,
            ClassNotFoundException;


    /**
     * Gets the employee's profile from the database
     *
     * @param email employee's email
     * @return GetEmployeeResponse
     */
    GetEmployeeResponse getEmployeeByEmail(String email) throws ExecutionException, InterruptedException;

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

    /**
     * Gets all the fairs from the database
     *
     * @return GetAllFairsResponse
     */
    GetAllFairsResponse getAllFairs() throws ExecutionException, InterruptedException;
}

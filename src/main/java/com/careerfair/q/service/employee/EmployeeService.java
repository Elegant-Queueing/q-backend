package com.careerfair.q.service.employee;

import com.careerfair.q.service.employee.request.AddEmployeeRequest;
import com.careerfair.q.service.employee.request.UpdateEmployeeRequest;
import com.careerfair.q.service.employee.response.AddEmployeeResponse;
import com.careerfair.q.service.employee.response.DeleteEmployeeResponse;
import com.careerfair.q.service.employee.response.GetEmployeeResponse;
import com.careerfair.q.service.employee.response.UpdateEmployeeResponse;

public interface EmployeeService {
    /**
     *
     * @param id
     * @return
     */
    GetEmployeeResponse getEmployee(String id);

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
     *
     * @param id
     * @param uploadEmployeeResume
     * @return
     */
    UpdateEmployeeResponse uploadEmployeeResume(String id, UpdateEmployeeRequest uploadEmployeeResume);
}

package com.careerfair.q.service.employee.implementation;

import com.careerfair.q.service.employee.EmployeeService;
import com.careerfair.q.service.employee.request.AddEmployeeRequest;
import com.careerfair.q.service.employee.request.UpdateEmployeeRequest;
import com.careerfair.q.service.employee.response.AddEmployeeResponse;
import com.careerfair.q.service.employee.response.DeleteEmployeeResponse;
import com.careerfair.q.service.employee.response.GetEmployeeResponse;
import com.careerfair.q.service.employee.response.UpdateEmployeeResponse;
import org.springframework.stereotype.Service;

@Service
public class EmployeeServiceImpl implements EmployeeService {

    @Override
    public GetEmployeeResponse getEmployee(String id) {
        // TODO
        return null;
    }

    @Override
    public UpdateEmployeeResponse updateEmployee(String id, UpdateEmployeeRequest updateEmployeeRequest) {
        // TODO
        return null;
    }

    @Override
    public DeleteEmployeeResponse deleteEmployee(String id) {
        // TODO
        return null;
    }

    @Override
    public AddEmployeeResponse addEmployee(AddEmployeeRequest addEmployeeRequest) {
        // TODO
        return null;
    }
}

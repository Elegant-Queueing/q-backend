package com.careerfair.q.service.employee.implementation;

import com.careerfair.q.service.database.EmployeeFirebase;
import com.careerfair.q.service.employee.EmployeeService;
import com.careerfair.q.service.employee.request.AddEmployeeRequest;
import com.careerfair.q.service.employee.request.UpdateEmployeeRequest;
import com.careerfair.q.service.employee.response.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.concurrent.ExecutionException;

@Service
public class EmployeeServiceImpl implements EmployeeService {

    @Autowired EmployeeFirebase employeeFirebase;

    @Override
    public GetEmployeeResponse getEmployee(String id) throws InterruptedException,
            ExecutionException, ClassNotFoundException {
        return new GetEmployeeResponse(employeeFirebase.getEmployee(id));
    }

    @Override
    public GetEmployeeResponse getEmployeeByEmail(String email) throws ExecutionException,
            InterruptedException {
        return new GetEmployeeResponse(employeeFirebase.getEmployeeByEmail(email));
    }

    @Override
    public UpdateEmployeeResponse updateEmployee(String id,
                                                 UpdateEmployeeRequest updateEmployeeRequest) {
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

    @Override
    public GetAllFairsResponse getAllFairs() throws ExecutionException, InterruptedException {
        return new GetAllFairsResponse(employeeFirebase.getAllFairs());
    }
}

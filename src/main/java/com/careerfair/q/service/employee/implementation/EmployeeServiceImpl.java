package com.careerfair.q.service.employee.implementation;

import com.careerfair.q.service.database.EmployeeFirebase;
import com.careerfair.q.service.employee.EmployeeService;
import com.careerfair.q.service.employee.request.AddEmployeeRequest;
import com.careerfair.q.service.employee.request.UpdateEmployeeRequest;
import com.careerfair.q.service.employee.response.AddEmployeeResponse;
import com.careerfair.q.service.employee.response.DeleteEmployeeResponse;
import com.careerfair.q.service.employee.response.GetEmployeeResponse;
import com.careerfair.q.service.employee.response.UpdateEmployeeResponse;
import com.careerfair.q.util.exception.InvalidRequestException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.concurrent.ExecutionException;

@Service
public class EmployeeServiceImpl implements EmployeeService {

    @Autowired EmployeeFirebase employeeFirebase;

    @Override
    public GetEmployeeResponse getEmployeeWithId(String employeeId) {
        try {
            return new GetEmployeeResponse(employeeFirebase.getEmployeeWithId(employeeId));
        } catch (ExecutionException | InterruptedException ex) {
            throw new InvalidRequestException(ex.getMessage());
        }
    }

    @Override
    public GetEmployeeResponse getEmployeeWithEmail(String email) {
        try {
            return new GetEmployeeResponse(employeeFirebase.getEmployeeWithEmail(email));
        } catch (ExecutionException | InterruptedException ex) {
            System.out.println("here");
            throw new InvalidRequestException(ex.getMessage());
        }
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
}

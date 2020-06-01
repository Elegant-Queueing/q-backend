package com.careerfair.q.service.employee.implementation;

import com.careerfair.q.model.db.Employee;
import com.careerfair.q.service.database.FirebaseService;
import com.careerfair.q.service.employee.EmployeeService;
import com.careerfair.q.service.employee.request.AddEmployeeRequest;
import com.careerfair.q.service.employee.request.EmployeeRequest;
import com.careerfair.q.service.employee.request.UpdateEmployeeRequest;
import com.careerfair.q.service.employee.response.AddEmployeeResponse;
import com.careerfair.q.service.employee.response.DeleteEmployeeResponse;
import com.careerfair.q.service.employee.response.GetEmployeeResponse;
import com.careerfair.q.service.employee.response.UpdateEmployeeResponse;
import com.careerfair.q.service.validation.ValidationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class EmployeeServiceImpl implements EmployeeService {

    @Autowired private FirebaseService firebaseService;
    @Autowired private ValidationService validationService;

    @Override
    public GetEmployeeResponse getEmployeeWithId(String employeeId) {
        return new GetEmployeeResponse(firebaseService.getEmployeeWithId(employeeId));
    }

    @Override
    public GetEmployeeResponse getEmployeeWithEmail(String email) {
        return new GetEmployeeResponse(firebaseService.getEmployeeWithEmail(email));
    }

    @Override
    public UpdateEmployeeResponse updateEmployee(String employeeId,
                                                 UpdateEmployeeRequest updateEmployeeRequest) {
        validationService.checkValidEmployeeRequest(updateEmployeeRequest);
        Employee updateEmployee = firebaseService.updateEmployee(employeeId,
                createEmployeeFromRequest(updateEmployeeRequest));
        return new UpdateEmployeeResponse(updateEmployee);
    }

    @Override
    public DeleteEmployeeResponse deleteEmployee(String employeeId) {
        return new DeleteEmployeeResponse(firebaseService.deleteEmployee(employeeId));
    }

    @Override
    public AddEmployeeResponse addEmployee(AddEmployeeRequest addEmployeeRequest) {
        validationService.checkValidEmployeeRequest(addEmployeeRequest);
        Employee employeeFromRequest = createEmployeeFromRequest(addEmployeeRequest);
        Employee newEmployee = firebaseService.addEmployee(employeeFromRequest);
        return new AddEmployeeResponse(newEmployee);
    }

    // Helper method to convert an EmployeeRequest object to an Employee
     <T extends EmployeeRequest> Employee createEmployeeFromRequest(T employeeRequest) {
        Employee employee = new Employee();
        employee.name = employeeRequest.getName();
        employee.companyId = firebaseService.getCompanyWithName(employeeRequest.getCompanyId())
                .getCompanyId();
        employee.bio = employeeRequest.getBio();
        employee.email = employeeRequest.getEmail();
        employee.role = employeeRequest.getRole();
        return employee;
    }
}

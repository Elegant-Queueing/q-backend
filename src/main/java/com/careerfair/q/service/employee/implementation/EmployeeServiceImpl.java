package com.careerfair.q.service.employee.implementation;

import com.careerfair.q.service.database.FirebaseService;
import com.careerfair.q.service.employee.EmployeeService;
import com.careerfair.q.service.employee.request.AddEmployeeRequest;
import com.careerfair.q.service.employee.request.UpdateEmployeeRequest;
import com.careerfair.q.service.employee.response.AddEmployeeResponse;
import com.careerfair.q.service.employee.response.DeleteEmployeeResponse;
import com.careerfair.q.service.employee.response.GetEmployeeResponse;
import com.careerfair.q.service.employee.response.UpdateEmployeeResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class EmployeeServiceImpl implements EmployeeService {

    private static final String ID = "cSvERmxDbFH9_ef8FkqTHG:APA91bEOJ4_FQ6WFTF5m4HYIABxYvKVQv8cJgiQU9XNxl4Mw_9guPbZ7qKbu2SveGclvvOcnruPPWif58MPAOOb2LVoJF3nXkmaHeG43ZugCbQGDvdJJsEJDL9QPb6amcyneAdMV46tR";

    private final FirebaseService firebaseService;

    public EmployeeServiceImpl(@Autowired FirebaseService firebaseService) {
        this.firebaseService = firebaseService;
    }

    @Override
    public GetEmployeeResponse getEmployeeWithId(String employeeId) {
        return new GetEmployeeResponse(firebaseService.getEmployeeWithId(employeeId));
    }

    @Override
    public GetEmployeeResponse getEmployeeWithEmail(String email) {
        return new GetEmployeeResponse(firebaseService.getEmployeeWithEmail(email));
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
    public String getRegistrationToken(String employeeId) {
        return ID;
    }
}

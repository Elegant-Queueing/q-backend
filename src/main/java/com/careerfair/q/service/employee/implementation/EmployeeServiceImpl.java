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

    private static final String ID = "efPdfJkESbSRX1KfVyD0tF:APA91bFdpTGvXwA3pN6yQksV1oeQFeOyEY-GuJ9HmTW2XoiZeeRUNd19TApPNny8xn3etYILvrxbbRlf0qZuOVOKQ7hwvJfY-P6G_nu2e6po0TTnARg-WzfH6F11ujIMDCajHaBCR_DJ";

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

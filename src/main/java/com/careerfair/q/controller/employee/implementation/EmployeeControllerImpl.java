package com.careerfair.q.controller.employee.implementation;

import com.careerfair.q.controller.employee.EmployeeController;
import com.careerfair.q.service.employee.EmployeeService;
import com.careerfair.q.service.employee.request.AddEmployeeRequest;
import com.careerfair.q.service.employee.request.UpdateEmployeeRequest;
import com.careerfair.q.service.employee.response.AddEmployeeResponse;
import com.careerfair.q.service.employee.response.DeleteEmployeeResponse;
import com.careerfair.q.service.employee.response.GetEmployeeResponse;
import com.careerfair.q.service.employee.response.UpdateEmployeeResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("employee")
public class EmployeeControllerImpl implements EmployeeController {

    @Autowired private EmployeeService employeeService;

    @GetMapping("/get/employee-id/{employee-id}")
    @Override
    public GetEmployeeResponse getEmployeeWithId(@PathVariable("employee-id") String employeeId) {
        return employeeService.getEmployeeWithId(employeeId);
    }

    @GetMapping("/get/email/{email}")
    @Override
    public GetEmployeeResponse getEmployeeWithEmail(@PathVariable("email") String email) {
        return employeeService.getEmployeeWithId(email);
    }

    @PutMapping("/update/{id}")
    @Override
    public UpdateEmployeeResponse updateEmployee(@PathVariable("id") String id,
                                                 @RequestBody UpdateEmployeeRequest updateEmployeeRequest) {
        return employeeService.updateEmployee(id, updateEmployeeRequest);
    }

    @DeleteMapping("/delete/{id}")
    @Override
    public DeleteEmployeeResponse deleteEmployee(@PathVariable("id") String id) {
        return employeeService.deleteEmployee(id);
    }

    @PostMapping("/add")
    @Override
    public AddEmployeeResponse addEmployee(@RequestBody AddEmployeeRequest addEmployeeRequest) {
        return employeeService.addEmployee(addEmployeeRequest);
    }
}

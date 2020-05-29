package com.careerfair.q.service.employee.implementation;

import com.careerfair.q.model.db.Employee;
import com.careerfair.q.service.database.FirebaseService;
import com.careerfair.q.service.employee.request.AddEmployeeRequest;
import com.careerfair.q.service.employee.request.EmployeeRequest;
import com.careerfair.q.service.employee.request.UpdateEmployeeRequest;
import com.careerfair.q.service.employee.response.*;
import com.careerfair.q.service.validation.ValidationService;
import com.careerfair.q.util.enums.Role;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Arrays;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doReturn;

public class EmployeeServiceTest {

    @Mock
    private FirebaseService firebaseService;

    @Mock
    private ValidationService validationService;

    @InjectMocks
    private final EmployeeServiceImpl employeeService = new EmployeeServiceImpl();

    private Employee employee;

    @BeforeEach
    public void setupMocks() {
        MockitoAnnotations.initMocks(this);
        employee = new Employee("e1", "n1", "c1",
                Role.SWE, "b1", "e1@c1.com", Arrays.asList("s1"));
    }

    @Test
    public void testGetEmployeeWithId() {
        doReturn(employee).when(firebaseService).getEmployeeWithId(anyString());
        GetEmployeeResponse getEmployeeResponse = employeeService
                .getEmployeeWithId("e1");

        checkValidResponse(getEmployeeResponse, "n1", "c1", "b1",
                Role.SWE, "e1@c1.com");
    }

    @Test
    public void testGetEmployeeWithEmail() {
        doReturn(employee).when(firebaseService).getEmployeeWithEmail(anyString());
        GetEmployeeResponse getEmployeeResponse = employeeService
                .getEmployeeWithEmail("e1@c1.com");

        checkValidResponse(getEmployeeResponse, "n1", "c1", "b1",
                Role.SWE, "e1@c1.com");
    }

    @Test
    public void testUpdateEmployeeName() {
        UpdateEmployeeRequest updateNameRequest = new UpdateEmployeeRequest();
        updateNameRequest.setName("n2");
        updateNameRequest.setCompanyId("c1");
        updateNameRequest.setRole(Role.SWE);
        updateNameRequest.setBio("b1");
        updateNameRequest.setEmail("e1@c1.com");

        // skipping validation test and assuming it is a valid request
        doNothing().when(validationService).checkValidEmployeeRequest(any());
        Employee updatedEmployee = employeeService
                .createEmployeeFromRequest(updateNameRequest);

        checkValidEmployee(updatedEmployee, updateNameRequest);

        doReturn(updatedEmployee).when(firebaseService).updateEmployee(anyString(), any());

        UpdateEmployeeResponse updateEmployeeNameResponse = employeeService
                .updateEmployee("e1", updateNameRequest);

        checkValidResponse(updateEmployeeNameResponse, "n2", "c1", "b1",
                Role.SWE, "e1@c1.com");
    }

    @Test
    public void testUpdateEmployeeMultipleAttributes() {
        UpdateEmployeeRequest updateRequest = new UpdateEmployeeRequest();
        updateRequest.setName("n2");
        updateRequest.setCompanyId("c2");
        updateRequest.setRole(Role.DS);
        updateRequest.setBio("b2");
        updateRequest.setEmail("e1@c2.com");

        // skipping validation test and assuming it is a valid request
        doNothing().when(validationService).checkValidStudentRequest(any());
        Employee updatedEmployee = employeeService
                .createEmployeeFromRequest(updateRequest);

        checkValidEmployee(updatedEmployee, updateRequest);
        doReturn(updatedEmployee).when(firebaseService).updateEmployee(anyString(), any());
        UpdateEmployeeResponse updateEmployeeNameResponse = employeeService
                .updateEmployee("e1", updateRequest);

        checkValidResponse(updateEmployeeNameResponse, "n2", "c2", "b2",
                Role.DS, "e1@c2.com");
    }

    @Test
    public void testDeleteEmployee() {
        doReturn(employee).when(firebaseService).deleteEmployee(anyString());
        DeleteEmployeeResponse deleteEmployeeResponse = employeeService
                .deleteEmployee("e1");

        checkValidResponse(deleteEmployeeResponse, "n1", "c1", "b1",
                Role.SWE, "e1@c1.com");
        assertEquals(deleteEmployeeResponse.getStudents(), Arrays.asList("s1"));
    }

    @Test
    public void testAddEmployee() {
        AddEmployeeRequest addEmployeeRequest = new AddEmployeeRequest();
        addEmployeeRequest.setName("n2");
        addEmployeeRequest.setCompanyId("c1");
        addEmployeeRequest.setBio("b1");
        addEmployeeRequest.setRole(Role.SWE);
        addEmployeeRequest.setEmail("e1@c1.com");

        // skipping validation test and assuming it is a valid request
        doNothing().when(validationService).checkValidEmployeeRequest(any());
        Employee newEmployee = employeeService.createEmployeeFromRequest(addEmployeeRequest);
        checkValidEmployee(newEmployee, addEmployeeRequest);
        doReturn(newEmployee).when(firebaseService).addEmployee(any());
        AddEmployeeResponse addEmployeeResponse = employeeService.addEmployee(addEmployeeRequest);
        checkValidResponse(addEmployeeResponse, "n2", "c1", "b1",
                Role.SWE, "e1@c1.com");
    }

    private void checkValidResponse(EmployeeResponse employeeResponse, String name,
                                    String companyId, String bio, Role role, String email) {
        assertNotNull(employeeResponse);
        assertEquals(employeeResponse.getName(), name);
        assertEquals(employeeResponse.getCompanyId(), companyId);
        assertEquals(employeeResponse.getBio(), bio);
        assertEquals(employeeResponse.getRole(), role);
        assertEquals(employeeResponse.getEmail(), email);
    }

    private void checkValidEmployee(Employee employee, EmployeeRequest request) {
        assertNotNull(employee);
        assertEquals(employee.name, request.getName());
        assertEquals(employee.companyId, request.getCompanyId());
        assertEquals(employee.bio, request.getBio());
        assertEquals(employee.email, request.getEmail());
        assertEquals(employee.role, request.getRole());
    }
}

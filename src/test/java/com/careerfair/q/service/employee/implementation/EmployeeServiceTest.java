package com.careerfair.q.service.employee.implementation;

import com.careerfair.q.model.db.Company;
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

import java.util.Collections;

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
    private Company company;

    @BeforeEach
    public void setupMocks() {
        MockitoAnnotations.initMocks(this);
        employee = createDummyEmployee();
        company = createDummyCompany();
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
        populateRequestObject(updateNameRequest, "n2", "c1", Role.SWE, "b1", "e1@c1.com");

        // skipping validation test and assuming it is a valid request
        doNothing().when(validationService).checkValidEmployeeRequest(any());
        doReturn(company).when(firebaseService).getCompanyWithName(anyString());

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
        populateRequestObject(updateRequest, "n2", "c1", Role.DS, "b2", "e1@c1.com");

        // skipping validation test and assuming it is a valid request
        doNothing().when(validationService).checkValidStudentRequest(any());
        doReturn(company).when(firebaseService).getCompanyWithName(anyString());

        Employee updatedEmployee = employeeService
                .createEmployeeFromRequest(updateRequest);

        checkValidEmployee(updatedEmployee, updateRequest);
        doReturn(updatedEmployee).when(firebaseService).updateEmployee(anyString(), any());
        UpdateEmployeeResponse updateEmployeeNameResponse = employeeService
                .updateEmployee("e1", updateRequest);

        checkValidResponse(updateEmployeeNameResponse, "n2", "c1", "b2",
                Role.DS, "e1@c1.com");
    }

    @Test
    public void testDeleteEmployee() {
        doReturn(employee).when(firebaseService).deleteEmployee(anyString());
        DeleteEmployeeResponse deleteEmployeeResponse = employeeService
                .deleteEmployee("e1");

        checkValidResponse(deleteEmployeeResponse, "n1", "c1", "b1",
                Role.SWE, "e1@c1.com");
        assertEquals(deleteEmployeeResponse.getStudents(), Collections.singletonList("s1"));
    }

    @Test
    public void testAddEmployee() {
        AddEmployeeRequest addEmployeeRequest = new AddEmployeeRequest();
        populateRequestObject(addEmployeeRequest, "n2", "c1", Role.SWE, "b1", "e1@c1.com");

        // skipping validation test and assuming it is a valid request
        doNothing().when(validationService).checkValidEmployeeRequest(any());
        doReturn(company).when(firebaseService).getCompanyWithName(anyString());

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

    private Employee createDummyEmployee() {
        Employee employee = new Employee();
        employee.employeeId = "e1";
        employee.companyId = "c1";
        employee.name = "n1";
        employee.bio = "b1";
        employee.role = Role.SWE;
        employee.email = "e1@c1.com";
        employee.students = Collections.singletonList("s1");
        return employee;
    }

    private <T extends EmployeeRequest> void populateRequestObject(EmployeeRequest request,
            String name, String companyId, Role role, String bio, String email) {
        request.setName(name);
        request.setCompanyId(companyId);
        request.setRole(role);
        request.setBio(bio);
        request.setEmail(email);
    }

    private Company createDummyCompany() {
        Company company = new Company();
        company.setName("c1");
        company.setCompanyId("c1");
        company.setRoles(Collections.singletonList(Role.SWE));
        company.setEmployees(Collections.singletonList("e1"));
        company.setBio("b1");
        company.setWebsite("www.c1.com");
        return company;
    }
}

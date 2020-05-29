package com.careerfair.q.service.employee.implementation;

import com.careerfair.q.model.db.Employee;
import com.careerfair.q.service.database.FirebaseService;
import com.careerfair.q.service.employee.request.AddEmployeeRequest;
import com.careerfair.q.service.employee.request.UpdateEmployeeRequest;
import com.careerfair.q.service.employee.response.AddEmployeeResponse;
import com.careerfair.q.service.employee.response.DeleteEmployeeResponse;
import com.careerfair.q.service.employee.response.GetEmployeeResponse;
import com.careerfair.q.service.employee.response.UpdateEmployeeResponse;
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

        assertNotNull(getEmployeeResponse);
        assertEquals(getEmployeeResponse.getName(), "n1");
        assertEquals(getEmployeeResponse.getCompanyId(), "c1");
        assertEquals(getEmployeeResponse.getBio(), "b1");
        assertEquals(getEmployeeResponse.getRole(), Role.SWE);
        assertEquals(getEmployeeResponse.getEmail(), "e1@c1.com");
        assertEquals(getEmployeeResponse.getStudents(), Arrays.asList("s1"));
    }

    @Test
    public void testGetEmployeeWithEmail() {
        doReturn(employee).when(firebaseService).getEmployeeWithEmail(anyString());
        GetEmployeeResponse getEmployeeResponse = employeeService
                .getEmployeeWithEmail("e1@c1.com");

        assertNotNull(getEmployeeResponse);
        assertEquals(getEmployeeResponse.getName(), "n1");
        assertEquals(getEmployeeResponse.getCompanyId(), "c1");
        assertEquals(getEmployeeResponse.getBio(), "b1");
        assertEquals(getEmployeeResponse.getRole(), Role.SWE);
        assertEquals(getEmployeeResponse.getEmail(), "e1@c1.com");
        assertEquals(getEmployeeResponse.getStudents(), Arrays.asList("s1"));
    }

    @Test
    public void testUpdateEmployeeName() {
        UpdateEmployeeRequest updatedNameRequest = new UpdateEmployeeRequest();
        updatedNameRequest.setName("n2");
        updatedNameRequest.setCompanyId("c1");
        updatedNameRequest.setRole(Role.SWE);
        updatedNameRequest.setBio("b1");
        updatedNameRequest.setEmail("e1@c1.com");

        // skipping validation test and assuming it is a valid request
        doNothing().when(validationService).checkValidStudentRequest(any());
        Employee updatedEmployee = employeeService
                .createEmployeeFromRequest(updatedNameRequest);
        
        assertNotNull(updatedEmployee);
        assertEquals(updatedEmployee.name, updatedNameRequest.getName());
        assertEquals(updatedEmployee.companyId, updatedNameRequest.getCompanyId());
        assertEquals(updatedEmployee.bio, updatedNameRequest.getBio());
        assertEquals(updatedEmployee.email, updatedNameRequest.getEmail());
        assertEquals(updatedEmployee.role, updatedNameRequest.getRole());

        doReturn(updatedEmployee).when(firebaseService).updateEmployee(anyString(), any());

        UpdateEmployeeResponse updateEmployeeNameResponse = employeeService
                .updateEmployee("e1", updatedNameRequest);

        // students is not an attribute in response or request
        assertNotNull(updateEmployeeNameResponse);
        assertEquals(updateEmployeeNameResponse.getName(), "n2");
        assertEquals(updateEmployeeNameResponse.getCompanyId(), "c1");
        assertEquals(updateEmployeeNameResponse.getBio(), "b1");
        assertEquals(updateEmployeeNameResponse.getRole(), Role.SWE);
        assertEquals(updateEmployeeNameResponse.getEmail(), "e1@c1.com");
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

        assertNotNull(updatedEmployee);
        assertEquals(updatedEmployee.name, updateRequest.getName());
        assertEquals(updatedEmployee.companyId, updateRequest.getCompanyId());
        assertEquals(updatedEmployee.bio, updateRequest.getBio());
        assertEquals(updatedEmployee.email, updateRequest.getEmail());
        assertEquals(updatedEmployee.role, updateRequest.getRole());

        doReturn(updatedEmployee).when(firebaseService).updateEmployee(anyString(), any());

        UpdateEmployeeResponse updateEmployeeNameResponse = employeeService
                .updateEmployee("e1", updateRequest);

        // students is not an attribute in response or request
        assertNotNull(updateEmployeeNameResponse);
        assertEquals(updateEmployeeNameResponse.getName(), "n2");
        assertEquals(updateEmployeeNameResponse.getCompanyId(), "c2");
        assertEquals(updateEmployeeNameResponse.getBio(), "b2");
        assertEquals(updateEmployeeNameResponse.getRole(), Role.DS);
        assertEquals(updateEmployeeNameResponse.getEmail(), "e1@c2.com");
    }

    @Test
    public void testDeleteEmployee() {
        doReturn(employee).when(firebaseService).deleteEmployee(anyString());
        DeleteEmployeeResponse deleteEmployeeResponse = employeeService
                .deleteEmployee("e1");

        assertNotNull(deleteEmployeeResponse);
        assertEquals(deleteEmployeeResponse.getEmployeeId(), "e1");
        assertEquals(deleteEmployeeResponse.getName(), "n1");
        assertEquals(deleteEmployeeResponse.getCompanyId(), "c1");
        assertEquals(deleteEmployeeResponse.getBio(), "b1");
        assertEquals(deleteEmployeeResponse.getRole(), Role.SWE);
        assertEquals(deleteEmployeeResponse.getEmail(), "e1@c1.com");
        assertEquals(deleteEmployeeResponse.getStudents(), Arrays.asList("s1"));
    }

    @Test
    public void testAddEmployee() {
        AddEmployeeRequest addEmployeeRequest = new AddEmployeeRequest();
        addEmployeeRequest.setName("n1");
        addEmployeeRequest.setCompanyId("c1");
        addEmployeeRequest.setBio("b1");
        addEmployeeRequest.setRole(Role.SWE);
        addEmployeeRequest.setEmail("e1@c1.com");

        // skipping validation test and assuming it is a valid request
        doNothing().when(validationService).checkValidStudentRequest(any());

        Employee newStudent = employeeService.createEmployeeFromRequest(addEmployeeRequest);

        assertNotNull(newStudent);
        assertEquals(newStudent.name, addEmployeeRequest.getName());
        assertEquals(newStudent.companyId, addEmployeeRequest.getCompanyId());
        assertEquals(newStudent.bio, addEmployeeRequest.getBio());
        assertEquals(newStudent.email, addEmployeeRequest.getEmail());
        assertEquals(newStudent.role, addEmployeeRequest.getRole());

        doReturn(employee).when(firebaseService).addEmployee(any());

        AddEmployeeResponse addEmployeeResponse = employeeService.addEmployee(addEmployeeRequest);

        // students is not an attribute in response or request
        assertNotNull(addEmployeeResponse);
        assertEquals(addEmployeeResponse.getName(), "n1");
        assertEquals(addEmployeeResponse.getCompanyId(), "c1");
        assertEquals(addEmployeeResponse.getBio(), "b1");
        assertEquals(addEmployeeResponse.getRole(), Role.SWE);
        assertEquals(addEmployeeResponse.getEmail(), "e1@c1.com");
    }
}

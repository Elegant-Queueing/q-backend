package com.careerfair.q.service.database.implementation;

import com.careerfair.q.model.db.Company;
import com.careerfair.q.model.db.Employee;
import com.careerfair.q.model.db.Fair;
import com.careerfair.q.model.db.Student;
import com.careerfair.q.util.constant.Firebase;
import com.careerfair.q.util.enums.Role;
import com.careerfair.q.util.exception.FirebaseException;
import com.careerfair.q.workflow.database.EmployeeFirebaseWorkflow;
import com.careerfair.q.workflow.database.FairFirebaseWorkflow;
import com.careerfair.q.workflow.database.StudentFirebaseWorkflow;
import com.google.cloud.Timestamp;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.Assert.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

public class FirebaseServiceTest {

    @Mock
    private StudentFirebaseWorkflow studentFirebaseWorkflow;

    @Mock
    private EmployeeFirebaseWorkflow employeeFirebaseWorkflow;

    @Mock
    private FairFirebaseWorkflow fairFirebaseWorkflow;

    @InjectMocks
    private final FirebaseServiceImpl firebaseService = new FirebaseServiceImpl();

    private Student student;
    private Employee employee;
    private Fair fair;
    private Company company;

    @BeforeEach
    public void setupMocks() {
        MockitoAnnotations.initMocks(this);
        student = new Student("s1", "f1", "l1", "u1",
                "m1", Role.SWE, "b1", "s1@u1.edu", 4.0,
                Timestamp.ofTimeSecondsAndNanos(1592506815, 0), true,
                Collections.singletonList("e1"));

        employee = new Employee("e1", "n1", "c1",
                Role.SWE, "b1", "e1@c1.com", Collections.singletonList("s1"));

        fair = new Fair("f1", "n1", "u1", "d1",
                Collections.singletonList("c1"),
                Timestamp.ofTimeSecondsAndNanos(1192506815, 0),
                Timestamp.ofTimeSecondsAndNanos(1192508815, 0));

        company = new Company("c1", Collections.singletonList(Role.SWE),
                Collections.singletonList("e1"), "b1", "www.c1.com");
    }

    @Test
    public void testCheckValidStudentId() {
        doNothing().when(studentFirebaseWorkflow).checkValidStudentId(anyString());
        firebaseService.checkValidStudentId("s1");
        verify(studentFirebaseWorkflow, times(1))
                .checkValidStudentId("s1");
    }

    @Test
    public void testInvalidStudentId() {
        doThrow(new FirebaseException("The student with student id=s10 does not exist"))
                .when(studentFirebaseWorkflow).checkValidStudentId(anyString());
        try {
            firebaseService.checkValidStudentId("s10");
            fail();
        } catch (FirebaseException ex) {
            assertEquals(ex.getMessage(), "The student with student id=s10 does not exist");
        }
    }

    @Test
    public void testGetStudentWithValidId() {
        doReturn(student).when(studentFirebaseWorkflow).getStudentWithId(anyString());
        Student getStudent = firebaseService.getStudentWithId("s1");

        checkValidStudent(getStudent, "s1","f1", "l1", "u1",
                "m1", Role.SWE, "b1","s1@u1.edu", 4.0,
                Timestamp.ofTimeSecondsAndNanos(1592506815, 0), true);
    }

    @Test
    public void testGetStudentWithInvalidId() {
        doThrow(new FirebaseException("The student with student id=s10 does not exist"))
                .when(studentFirebaseWorkflow).getStudentWithId(anyString());
        try {
            firebaseService.getStudentWithId("s10");
            fail();
        } catch (FirebaseException ex) {
            assertEquals(ex.getMessage(), "The student with student id=s10 does not exist");
        }
    }

    @Test
    public void testGetStudentWithValidEmail() {
        doReturn(student).when(studentFirebaseWorkflow).getStudentWithEmail(anyString());
        Student getStudent = firebaseService.getStudentWithEmail("s1@u1.edu");

        checkValidStudent(getStudent, "s1", "f1", "l1", "u1",
                "m1", Role.SWE, "b1","s1@u1.edu", 4.0,
                Timestamp.ofTimeSecondsAndNanos(1592506815, 0), true);
    }

    @Test
    public void testGetStudentWithInvalidEmail() {
        doThrow(new FirebaseException("No student with email=s2@u2.edu exists"))
                .when(studentFirebaseWorkflow).getStudentWithEmail(anyString());
        try {
            firebaseService.getStudentWithEmail("s2@u2.edu");
            fail();
        } catch (FirebaseException ex) {
            assertEquals(ex.getMessage(), "No student with email=s2@u2.edu exists");
        }
    }

    @Test
    public void testCheckValidEmployeeId() {
        doNothing().when(employeeFirebaseWorkflow).checkValidEmployeeId(anyString());
        firebaseService.checkValidEmployeeId("e1");
        verify(employeeFirebaseWorkflow, times(1))
                .checkValidEmployeeId("e1");
    }

    @Test
    public void testGetEmployeeWithValidId() {
        doReturn(employee).when(employeeFirebaseWorkflow).getEmployeeWithId(anyString());
        Employee getEmployee = firebaseService.getEmployeeWithId("e1");

        checkValidEmployee(getEmployee, "e1", "n1", "c1", "b1",
                Role.SWE, "e1@c1.com");
    }

    @Test
    public void testGetEmployeeWithInvalidId() {
        doThrow(new FirebaseException("No employee with employee id=e10"))
                .when(employeeFirebaseWorkflow).getEmployeeWithId(anyString());
        try {
            firebaseService.getEmployeeWithId("e10");
            fail();
        } catch (FirebaseException ex) {
            assertEquals(ex.getMessage(),"No employee with employee id=e10");
        }
    }

    @Test
    public void testGetEmployeeWithValidEmail() {
        doReturn(employee).when(employeeFirebaseWorkflow).getEmployeeWithEmail(anyString());
        Employee getEmployee = firebaseService.getEmployeeWithEmail("e1@c1.com");

        checkValidEmployee(getEmployee, "e1","n1", "c1", "b1",
                Role.SWE, "e1@c1.com");
    }

    @Test
    public void testGetEmployeeWithInvalidEmail() {
        doThrow(new FirebaseException("No employee with email e2@c2.com"))
                .when(employeeFirebaseWorkflow).getEmployeeWithEmail(anyString());
        try {
            firebaseService.getEmployeeWithEmail("e2@c2.com");
            fail();
        } catch (FirebaseException ex) {
            assertEquals(ex.getMessage(), "No employee with email e2@c2.com");
        }
    }

    @Test
    public void testGetFairWithValidId() {
        doReturn(fair).when(fairFirebaseWorkflow).getFairWithId(anyString());
        Fair getFair = firebaseService.getFairWithId("f1");

        checkValidFair(getFair, "f1", "n1", "u1", "d1",
                Collections.singletonList("c1"),
                Timestamp.ofTimeSecondsAndNanos(1192506815, 0),
                Timestamp.ofTimeSecondsAndNanos(1192508815, 0));
    }

    @Test
    public void testGetFairWithInvalidId() {
        doThrow(new FirebaseException("No fair exists with id=f2"))
                .when(fairFirebaseWorkflow).getFairWithId(anyString());
        try {
            firebaseService.getFairWithId("f2");
            fail();
        } catch (FirebaseException ex) {
            assertEquals(ex.getMessage(), "No fair exists with id=f2");
        }
    }

    @Test
    public void testCheckValidCompanyId() {
        doNothing().when(fairFirebaseWorkflow).checkValidCompanyId(anyString());
        firebaseService.checkValidCompanyId("c1");
        verify(fairFirebaseWorkflow, times(1))
                .checkValidCompanyId("c1");
    }

    @Test
    public void testGetCompanyWithValidFairAndCompanyId() {
        doReturn(company).when(fairFirebaseWorkflow).getCompanyWithId(anyString(), anyString());
        Company getCompany = firebaseService.getCompanyWithId("f1", "c1");

        checkValidCompany(getCompany, "c1", Collections.singletonList(Role.SWE),
                Collections.singletonList("e1"), "b1", "www.c1.com");
    }

    @Test
    public void testGetCompanyWithInvalidFairId() {
        doThrow(new FirebaseException("No fair exists with id=f10"))
                .when(fairFirebaseWorkflow).getCompanyWithId(anyString(), anyString());
        try {
            firebaseService.getCompanyWithId("f10", "c1");
            fail();
        } catch (FirebaseException ex) {
            assertEquals(ex.getMessage(),"No fair exists with id=f10");
        }
    }

    @Test
    public void testGetCompanyWithInvalidCompanyId() {
        doThrow(new FirebaseException("The company with company id=c10 is not present " +
                "in the fair with fair id=f1"))
                .when(fairFirebaseWorkflow).getCompanyWithId(anyString(), anyString());
        try {
            firebaseService.getCompanyWithId("f1", "c10");
            fail();
        } catch (FirebaseException ex) {
            assertEquals(ex.getMessage(),"The company with company id=c10 is not present " +
                    "in the fair with fair id=f1");
        }
    }

    private void checkValidCompany(Company company, String name, List<Role> roles,
                                   List<String> employees, String bio, String website) {
        assertNotNull(company);
        assertEquals(company.name, name);
        assertEquals(company.roles, roles);
        assertEquals(company.employees, employees);
        assertEquals(company.bio, bio);
        assertEquals(company.website, website);
    }

    private void checkValidFair(Fair fair, String fairId, String name, String universityId,
            String description, List<String> companies, Timestamp startTime, Timestamp endTime) {

        assertNotNull(fair);
        assertEquals(fair.fairId, fairId);
        assertEquals(fair.name, name);
        assertEquals(fair.universityId, universityId);
        assertEquals(fair.description, description);
        assertEquals(fair.companies, companies);
        assertEquals(fair.startTime, startTime);
        assertEquals(fair.endTime, endTime);

    }
    private void checkValidStudent(Student student, String studentId, String firstName,
            String lastName, String universityId, String major, Role role, String bio, String email,
            Double gpa, Timestamp gradDate, Boolean international) {

        assertNotNull(student);
        assertEquals(student.studentId, studentId);
        assertEquals(student.firstName, firstName);
        assertEquals(student.lastName, lastName);
        assertEquals(student.universityId, universityId);
        assertEquals(student.major, major);
        assertEquals(student.role, role);
        assertEquals(student.bio, bio);
        assertEquals(student.email, email);
        assertEquals(student.gpa, gpa);
        assertEquals(student.graduationDate, gradDate);
        assertEquals(student.international, international);
    }

    private void checkValidEmployee(Employee employee, String employeeId, String name,
                                    String companyId, String bio, Role role, String email) {

        assertNotNull(employee);
        assertEquals(employee.employeeId, employeeId);
        assertEquals(employee.name, name);
        assertEquals(employee.companyId, companyId);
        assertEquals(employee.bio, bio);
        assertEquals(employee.role, role);
        assertEquals(employee.email, email);
    }
}
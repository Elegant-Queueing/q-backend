package com.careerfair.q.service.student.implementation;

import com.careerfair.q.model.db.Student;
import com.careerfair.q.service.database.FirebaseService;
import com.careerfair.q.service.student.request.AddStudentRequest;
import com.careerfair.q.service.student.request.StudentRequest;
import com.careerfair.q.service.student.request.UpdateStudentRequest;
import com.careerfair.q.service.student.response.*;
import com.careerfair.q.service.validation.ValidationService;
import com.careerfair.q.util.enums.Role;
import com.google.cloud.Timestamp;
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

public class StudentServiceTest {

    @Mock
    private FirebaseService firebaseService;

    @Mock
    private ValidationService validationService;

    @InjectMocks
    private final StudentServiceImpl studentService = new StudentServiceImpl();

    private Student student;

    @BeforeEach
    public void setupMocks() {
        MockitoAnnotations.initMocks(this);
        student = new Student("s1", "f1", "l1", "u1",
                "m1", Role.SWE, "b1", "s1@u1.edu", 4.0,
                Timestamp.ofTimeSecondsAndNanos(1592506815, 0), true,
                Arrays.asList("e1"));
    }

    @Test
    public void testGetStudentWithId() {
        doReturn(student).when(firebaseService).getStudentWithId(anyString());
        GetStudentResponse getStudentResponse = studentService.getStudentWithId("s1");
        checkValidResponse(getStudentResponse, "f1", "l1", "u1",
                "m1", Role.SWE, "b1","s1@u1.edu", 4.0,
                Timestamp.ofTimeSecondsAndNanos(1592506815, 0), true);
    }

    @Test
    public void testGetStudentWithEmail() {
        doReturn(student).when(firebaseService).getStudentWithEmail(anyString());
        GetStudentResponse getStudentResponse = studentService.getStudentWithEmail("s1@u1.edu");
        checkValidResponse(getStudentResponse, "f1", "l1", "u1",
                "m1", Role.SWE, "b1","s1@u1.edu", 4.0,
                Timestamp.ofTimeSecondsAndNanos(1592506815, 0), true);
    }

    @Test
    public void testUpdateStudentFirstName() {
        UpdateStudentRequest updateNameRequest = new UpdateStudentRequest();
        updateNameRequest.setFirstName("f2");
        updateNameRequest.setLastName("l1");
        updateNameRequest.setUniversityId("u1");
        updateNameRequest.setMajor("m1");
        updateNameRequest.setRole(Role.SWE);
        updateNameRequest.setBio("b1");
        updateNameRequest.setEmail("s1@u1.edu");
        updateNameRequest.setGpa(4.0);
        updateNameRequest.setGraduationDate(Timestamp.ofTimeSecondsAndNanos(1592506815,
                0));
        updateNameRequest.setInternational(true);

        doNothing().when(validationService).checkValidStudentRequest(any());
        Student updatedStudent = studentService.createStudentFromRequest(updateNameRequest);
        checkValidStudent(updatedStudent, updateNameRequest);

        doReturn(updatedStudent).when(firebaseService).updateStudent(anyString(), any());

        UpdateStudentResponse updateStudentResponse = studentService.updateStudent("s1",
                updateNameRequest);
        checkValidResponse(updateStudentResponse,"f2", "l1", "u1",
                "m1", Role.SWE, "b1","s1@u1.edu", 4.0,
                Timestamp.ofTimeSecondsAndNanos(1592506815, 0), true);
    }

    @Test
    public void testUpdateStudentMultipleAttributes() {
        UpdateStudentRequest updateNameRequest = new UpdateStudentRequest();
        updateNameRequest.setFirstName("f2");
        updateNameRequest.setLastName("l2");
        updateNameRequest.setUniversityId("u2");
        updateNameRequest.setMajor("m2");
        updateNameRequest.setRole(Role.DS);
        updateNameRequest.setBio("b2");
        updateNameRequest.setEmail("s2@u2.edu");
        updateNameRequest.setGpa(3.0);
        updateNameRequest.setGraduationDate(Timestamp.ofTimeSecondsAndNanos(1592506825,
                0));
        updateNameRequest.setInternational(false);

        doNothing().when(validationService).checkValidStudentRequest(any());
        Student updatedStudent = studentService.createStudentFromRequest(updateNameRequest);
        checkValidStudent(updatedStudent, updateNameRequest);

        doReturn(updatedStudent).when(firebaseService).updateStudent(anyString(), any());

        UpdateStudentResponse updateStudentResponse = studentService.updateStudent("s1",
                updateNameRequest);
        checkValidResponse(updateStudentResponse,"f2", "l2", "u2",
                "m2", Role.DS, "b2","s2@u2.edu", 3.0,
                Timestamp.ofTimeSecondsAndNanos(1592506825, 0), false);
    }

    @Test
    public void testDeleteStudent() {
        doReturn(student).when(firebaseService).deleteStudent(anyString());
        DeleteStudentResponse deleteStudentResponse = studentService.deleteStudent("s1");

        checkValidResponse(deleteStudentResponse,"f1", "l1", "u1",
                "m1", Role.SWE, "b1","s1@u1.edu", 4.0,
                Timestamp.ofTimeSecondsAndNanos(1592506815, 0), true);
        assertEquals(deleteStudentResponse.getEmployees(), Arrays.asList("e1"));
    }

    @Test
    public void testAddStudent() {
        AddStudentRequest addStudentRequest = new AddStudentRequest();
        addStudentRequest.setFirstName("f2");
        addStudentRequest.setLastName("l2");
        addStudentRequest.setUniversityId("u2");
        addStudentRequest.setMajor("m2");
        addStudentRequest.setRole(Role.DS);
        addStudentRequest.setBio("b2");
        addStudentRequest.setEmail("s2@u2.edu");
        addStudentRequest.setGpa(3.0);
        addStudentRequest.setGraduationDate(Timestamp.ofTimeSecondsAndNanos(1592506825,
                0));
        addStudentRequest.setInternational(false);

        doNothing().when(validationService).checkValidStudentRequest(any());
        Student newStudent = studentService.createStudentFromRequest(addStudentRequest);
        checkValidStudent(newStudent, addStudentRequest);
        doReturn(newStudent).when(firebaseService).addStudent(any());
        AddStudentResponse addStudentResponse = studentService.addStudent(addStudentRequest);
        checkValidResponse(addStudentResponse, "f2", "l2", "u2",
                "m2", Role.DS, "b2","s2@u2.edu", 3.0,
                Timestamp.ofTimeSecondsAndNanos(1592506825, 0), false);
    }

    private void checkValidResponse(StudentResponse studentResponse, String firstName,
            String lastName, String universityId, String major, Role role, String bio, String email,
            Double gpa, Timestamp gradDate, Boolean international) {
        assertNotNull(studentResponse);
        assertEquals(studentResponse.getFirstName(), firstName);
        assertEquals(studentResponse.getLastName(), lastName);
        assertEquals(studentResponse.getUniversityId(), universityId);
        assertEquals(studentResponse.getMajor(), major);
        assertEquals(studentResponse.getRole(), role);
        assertEquals(studentResponse.getBio(), bio);
        assertEquals(studentResponse.getEmail(), email);
        assertEquals(studentResponse.getGpa(), gpa);
        assertEquals(studentResponse.getGraduationDate(), gradDate);
        assertEquals(studentResponse.getInternational(), international);
    }

    private void checkValidStudent(Student student, StudentRequest request) {
        assertNotNull(student);
        assertEquals(student.firstName, request.getFirstName());
        assertEquals(student.lastName, request.getLastName());
        assertEquals(student.universityId, request.getUniversityId());
        assertEquals(student.major, request.getMajor());
        assertEquals(student.role, request.getRole());
        assertEquals(student.bio, request.getBio());
        assertEquals(student.email, request.getEmail());
        assertEquals(student.gpa, request.getGpa());
        assertEquals(student.graduationDate, request.getGraduationDate());
        assertEquals(student.international, request.getInternational());
    }
}

package com.careerfair.q.service.database.implementation;

import com.careerfair.q.model.db.Company;
import com.careerfair.q.model.db.Employee;
import com.careerfair.q.model.db.Fair;
import com.careerfair.q.model.db.Student;
import com.careerfair.q.service.database.FirebaseService;
import com.careerfair.q.util.exception.FirebaseException;
import com.careerfair.q.workflow.database.EmployeeFirebaseWorkflow;
import com.careerfair.q.workflow.database.FairFirebaseWorkflow;
import com.careerfair.q.workflow.database.StudentFirebaseWorkflow;
import com.google.cloud.firestore.Firestore;
import com.google.common.collect.Maps;
import com.google.firebase.cloud.FirestoreClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
public class FirebaseServiceImpl implements FirebaseService {

    private final StudentFirebaseWorkflow studentFirebaseWorkflow;
    private final EmployeeFirebaseWorkflow employeeFirebaseWorkflow;
    private final FairFirebaseWorkflow fairFirebaseWorkflow;

    public FirebaseServiceImpl(@Autowired StudentFirebaseWorkflow studentFirebaseWorkflow,
                               @Autowired EmployeeFirebaseWorkflow employeeFirebaseWorkflow,
                               @Autowired FairFirebaseWorkflow fairFirebaseWorkflow) {
        this.studentFirebaseWorkflow = studentFirebaseWorkflow;
        this.employeeFirebaseWorkflow = employeeFirebaseWorkflow;
        this.fairFirebaseWorkflow = fairFirebaseWorkflow;
    }

    @Override
    public void checkValidStudentId(String studentId) throws FirebaseException {
        studentFirebaseWorkflow.checkValidStudentId(studentId);
    }

    @Override
    public Student getStudentWithId(String studentId) throws FirebaseException {
        return studentFirebaseWorkflow.getStudentWithId(studentId);
    }

    @Override
    public Student getStudentWithEmail(String email) throws FirebaseException {
        return studentFirebaseWorkflow.getStudentWithEmail(email);
    }

    @Override
    public void checkValidEmployeeId(String employeeId) throws FirebaseException {
        employeeFirebaseWorkflow.checkValidEmployeeId(employeeId);
    }

    @Override
    public Employee getEmployeeWithId(String employeeId) throws FirebaseException {
        return employeeFirebaseWorkflow.getEmployeeWithId(employeeId);
    }

    @Override
    public Employee getEmployeeWithEmail(String email) throws FirebaseException {
        return employeeFirebaseWorkflow.getEmployeeWithEmail(email);
    }

    @Override
    public Fair getFairWithId(String fairId) throws FirebaseException {
        return fairFirebaseWorkflow.getFairWithId(fairId);
    }

    @Override
    public void checkValidCompanyId(String companyId) throws FirebaseException {
        fairFirebaseWorkflow.checkValidCompanyId(companyId);
    }

    @Override
    public Company getCompanyWithName(String companyName) throws FirebaseException {
        return fairFirebaseWorkflow.getCompanyWithName(companyName);
    }
    @Override
    public Company getCompanyWithId(String fairId, String companyId) throws FirebaseException {
        return fairFirebaseWorkflow.getCompanyWithId(fairId, companyId);
    }

    @Override
    public List<Fair> getAllFairs() throws FirebaseException {
        return fairFirebaseWorkflow.getAllFairs();
    }

    @Override
    public void registerStudent(String studentId, String employeeId) throws FirebaseException {
        studentFirebaseWorkflow.registerEmployeeToStudent(studentId, employeeId);
        employeeFirebaseWorkflow.registerStudentToEmployee(employeeId, studentId);
    }

    @Override
    public Student updateStudent(String studentId, Student student) throws FirebaseException {
        return studentFirebaseWorkflow.updateStudent(studentId, student);
    }

    @Override
    public Student addStudent(Student newStudent) throws FirebaseException {
        return studentFirebaseWorkflow.addStudent(newStudent);
    }

    @Override
    public Student deleteStudent(String studentId) throws FirebaseException {
        return studentFirebaseWorkflow.deleteStudent(studentId);
    }

    @Override
    public Employee updateEmployee(String employeeId, Employee employee)
            throws FirebaseException {
        return employeeFirebaseWorkflow.updateEmployee(employeeId, employee);
    }

    @Override
    public Employee addEmployee(Employee newEmployee) throws FirebaseException {
        return employeeFirebaseWorkflow.addEmployee(newEmployee);
    }

    @Override
    public Employee deleteEmployee(String employeeId) throws FirebaseException {
        return employeeFirebaseWorkflow.deleteEmployee(employeeId);
    }

    // Use this method to test your firebase connection
    @Override
    public void test() {
        Firestore dbFirestore = FirestoreClient.getFirestore();
        Map<String, String> map = Maps.newHashMap();
        String name = "TEST NAME";  // change this to your name when testing
        map.put("name", name);
        dbFirestore.collection("test").add(map);
    }
}

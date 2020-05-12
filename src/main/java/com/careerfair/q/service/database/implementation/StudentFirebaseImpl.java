package com.careerfair.q.service.database.implementation;

import com.careerfair.q.model.db.Employee;
import com.careerfair.q.model.db.Student;
import com.careerfair.q.service.database.StudentFirebase;
import com.google.api.client.util.Lists;
import com.google.api.core.ApiFuture;
import com.google.cloud.firestore.DocumentReference;
import com.google.cloud.firestore.Firestore;
import com.google.firebase.cloud.FirestoreClient;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.concurrent.ExecutionException;

import static com.careerfair.q.util.constant.Firebase.EMPLOYEE_COLLECTION;
import static com.careerfair.q.util.constant.Firebase.STUDENT_COLLECTION;

@Service
public class StudentFirebaseImpl implements StudentFirebase {

    // Use this method to test your firebase connection
    @Override
    public void test() {
        Firestore dbFirestore = FirestoreClient.getFirestore();
        HashMap<String, String> map = new HashMap<>();
        String name = "TEST NAME";  // change this to your name when testing
        map.put("name", name);
        ApiFuture<DocumentReference> collectionsApiFuture = dbFirestore.collection("test").add(map);
    }

    @Override
    public boolean registerStudent(String studentId, String employeeId) throws ExecutionException,
            InterruptedException {
        Firestore firestore = FirestoreClient.getFirestore();
        Student student = firestore.collection(STUDENT_COLLECTION)
                .document(studentId)
                .get().get()
                .toObject(Student.class);

        if (student == null) {
            return false;
        }

        Employee employee = firestore.collection(EMPLOYEE_COLLECTION)
                .document(employeeId)
                .get().get()
                .toObject(Employee.class);

        if (employee == null) {
            return false;
        }

        if (student.getEmployees() == null) {
            student.setEmployees(Lists.newArrayList());
        }
        if (employee.getStudents() == null) {
            employee.setStudents(Lists.newArrayList());
        }

        student.getEmployees().add(employeeId);
        employee.getStudents().add(studentId);

        firestore.collection(STUDENT_COLLECTION).document(studentId).update("employees",
                student.getEmployees());
        firestore.collection(EMPLOYEE_COLLECTION).document(employeeId).update("students",
                employee.getStudents());

        return true;
    }
}

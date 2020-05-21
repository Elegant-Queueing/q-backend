package com.careerfair.q.workflow.database.implementation;

import com.careerfair.q.model.db.Employee;
import com.careerfair.q.util.exception.FirebaseException;
import com.careerfair.q.workflow.database.EmployeeFirebaseWorkflow;
import com.google.api.client.util.Lists;
import com.google.cloud.firestore.CollectionReference;
import com.google.cloud.firestore.DocumentReference;
import com.google.cloud.firestore.DocumentSnapshot;
import com.google.cloud.firestore.Firestore;
import com.google.firebase.cloud.FirestoreClient;
import org.springframework.stereotype.Component;

import java.util.concurrent.ExecutionException;

import static com.careerfair.q.util.constant.Firebase.EMPLOYEE_COLLECTION;

@Component
public class EmployeeFirebaseWorkflowImpl implements EmployeeFirebaseWorkflow {

    @Override
    public void checkValidEmployeeId(String employeeId) throws FirebaseException {
        Firestore firestore = FirestoreClient.getFirestore();

        try {
            if (!firestore.collection(EMPLOYEE_COLLECTION).document(employeeId).get().get()
                    .exists()) {
                throw new FirebaseException("The employee with employee id=" + employeeId +
                        " does not exist");
            }
        } catch (ExecutionException | InterruptedException ex) {
            throw new FirebaseException(ex.getMessage());
        }
    }

    @Override
    public Employee getEmployeeWithId(String employeeId) throws FirebaseException {
        Firestore dbFirestore = FirestoreClient.getFirestore();

        try {
            Employee employee = dbFirestore.collection(EMPLOYEE_COLLECTION)
                    .document(employeeId)
                    .get().get()
                    .toObject(Employee.class);

            if (employee == null) {
                throw new FirebaseException("No employee with employee id=" + employeeId);
            }

            employee.employeeId = employeeId;
            return employee;
        } catch (ExecutionException | InterruptedException ex) {
            throw new FirebaseException(ex.getMessage());
        }
    }

    @Override
    public Employee getEmployeeWithEmail(String email) throws FirebaseException {
        Firestore dbFirestore = FirestoreClient.getFirestore();
        CollectionReference collectionReference = dbFirestore.collection(EMPLOYEE_COLLECTION);

        try {
            for (DocumentReference documentReference : collectionReference.listDocuments()) {
                DocumentSnapshot documentSnapshot = documentReference.get().get();
                String documentEmail = (String) documentSnapshot.get("email");

                if (email.equals(documentEmail)) {
                    Employee employee = documentSnapshot.toObject(Employee.class);
                    assert employee != null;

                    employee.employeeId = documentSnapshot.getId();
                    return employee;
                }
            }

            throw new FirebaseException("No employee with email=" + email);
        } catch (ExecutionException | InterruptedException ex) {
            throw new FirebaseException(ex.getMessage());
        }
    }

    @Override
    public void registerStudentToEmployee(String employeeId, String studentId)
            throws FirebaseException {
        Firestore firestore = FirestoreClient.getFirestore();
        Employee employee = getEmployeeWithId(employeeId);

        if (employee.students == null) {
            employee.students = Lists.newArrayList();
        }
        employee.students.add(studentId);

        firestore.collection(EMPLOYEE_COLLECTION).document(employeeId).update("students",
                employee.students);
    }

    @Override
    public Employee updateEmployee(String employeeId, Employee updatedEmployee)
            throws FirebaseException {
        Firestore firestore = FirestoreClient.getFirestore();
        try {
            firestore.collection(EMPLOYEE_COLLECTION).document(employeeId)
                    .set(updatedEmployee).get();
            return getEmployeeWithId(employeeId);
        } catch (ExecutionException | InterruptedException ex) {
            throw new FirebaseException(ex.getMessage());
        }
    }

    @Override
    public Employee addEmployee(Employee newEmployee) throws FirebaseException {
        Firestore firestore = FirestoreClient.getFirestore();
        DocumentReference documentReference = firestore.collection(EMPLOYEE_COLLECTION).document();
        String employeeId = documentReference.getId();
        try {
            documentReference.set(newEmployee).get();
            return getEmployeeWithId(employeeId);
        } catch (ExecutionException | InterruptedException ex) {
            throw new FirebaseException(ex.getMessage());
        }
    }

    @Override
    public Employee deleteEmployee(String employeeId) throws FirebaseException {
        Firestore firestore = FirestoreClient.getFirestore();
        Employee deletedEmployee = getEmployeeWithId(employeeId);
        firestore.collection(EMPLOYEE_COLLECTION).document(employeeId).delete();
        return deletedEmployee;
    }
}
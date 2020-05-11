package com.careerfair.q.service.database.implementation;

import com.careerfair.q.model.db.Employee;
import com.careerfair.q.service.database.EmployeeFirebase;
import com.careerfair.q.util.exception.InvalidRequestException;
import com.google.cloud.firestore.CollectionReference;
import com.google.cloud.firestore.DocumentReference;
import com.google.cloud.firestore.DocumentSnapshot;
import com.google.cloud.firestore.Firestore;
import com.google.firebase.cloud.FirestoreClient;
import org.springframework.stereotype.Service;

import java.util.concurrent.ExecutionException;

import static com.careerfair.q.util.contant.Fair.EMPLOYEE_COLLECTION;

@Service
public class EmployeeFirebaseImpl implements EmployeeFirebase {

    @Override
    public Employee getEmployeeWithId(String employeeId) throws ExecutionException,
            InterruptedException {
        Firestore dbFirestore = FirestoreClient.getFirestore();
        Employee employee = dbFirestore.collection(EMPLOYEE_COLLECTION)
                .document(employeeId)
                .get().get()
                .toObject(Employee.class);

        if (employee == null) {
            throw new InvalidRequestException("No employee with id=" + employeeId);
        }

        employee.setEmployee_id(employeeId);
        return employee;
    }

    @Override
    public Employee getEmployeeWithEmail(String email) throws ExecutionException,
            InterruptedException {
        Firestore dbFirestore = FirestoreClient.getFirestore();
        CollectionReference collectionReference = dbFirestore.collection(EMPLOYEE_COLLECTION);

        for (DocumentReference documentReference : collectionReference.listDocuments()) {
            DocumentSnapshot documentSnapshot = documentReference.get().get();
            String documentEmail = (String) documentSnapshot.get("email");

            if (email.equals(documentEmail)) {
                Employee employee = documentSnapshot.toObject(Employee.class);
                assert employee != null;
                employee.setEmployee_id(documentSnapshot.getId());
                return employee;
            }
        }

        throw new InvalidRequestException("No employee with the given email=" + email);
    }
}

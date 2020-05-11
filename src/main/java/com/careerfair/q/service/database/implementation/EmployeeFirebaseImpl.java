package com.careerfair.q.service.database.implementation;

import com.careerfair.q.model.db.Employee;
import com.careerfair.q.model.db.Fair;
import com.careerfair.q.service.database.EmployeeFirebase;
import com.careerfair.q.util.exception.InvalidRequestException;
import com.google.cloud.firestore.DocumentReference;
import com.google.cloud.firestore.DocumentSnapshot;
import com.google.cloud.firestore.Firestore;
import com.google.firebase.cloud.FirestoreClient;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.ExecutionException;

@Service
public class EmployeeFirebaseImpl implements EmployeeFirebase {

    private static final String EMPLOYEE_COLLECTION = "employees";
    private static final String FAIR_COLLECTION = "fairs";

    @Override
    public Employee getEmployee(String id) throws ExecutionException, InterruptedException, ClassNotFoundException {
        Firestore dbFirestore = FirestoreClient.getFirestore();
        Employee employee = dbFirestore
                .collection(EMPLOYEE_COLLECTION)
                .document(id)
                .get()
                .get()
                .toObject(Employee.class);

        if (employee == null) {
            throw new InvalidRequestException("No employee with id=" + id);
        }

        employee.setEmployee_id(id);
        return employee;
    }

    @Override
    public Employee getEmployeeByEmail(String email) throws ExecutionException, InterruptedException {
        Firestore dbFirestore = FirestoreClient.getFirestore();
        Iterator<DocumentReference> iterator = dbFirestore
                .collection(EMPLOYEE_COLLECTION)
                .listDocuments()
                .iterator();

        while (iterator.hasNext()) {
            DocumentSnapshot documentSnapshot = iterator
                    .next()
                    .get()
                    .get();
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

    @Override
    public List<Fair> getAllFairs() throws ExecutionException, InterruptedException {
        Firestore dbFirestore = FirestoreClient.getFirestore();
        List<Fair> result = new ArrayList<>();
        Iterator<DocumentReference> iterator = dbFirestore
                .collection(FAIR_COLLECTION)
                .listDocuments()
                .iterator();

        while (iterator.hasNext()) {
            Fair fair = iterator
                    .next()
                    .get()
                    .get()
                    .toObject(Fair.class);
            result.add(fair);
        }
        return result;
    }
}

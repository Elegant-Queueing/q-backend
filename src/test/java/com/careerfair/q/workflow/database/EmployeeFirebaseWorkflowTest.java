package com.careerfair.q.workflow.database;

import com.careerfair.q.database.FirebaseConnection;
import com.careerfair.q.model.db.Employee;
import com.careerfair.q.util.enums.Role;
import com.careerfair.q.util.exception.FirebaseException;
import com.careerfair.q.workflow.database.implementation.EmployeeFirebaseWorkflowImpl;
import com.google.api.core.ApiFuture;
import com.google.cloud.firestore.CollectionReference;
import com.google.cloud.firestore.DocumentReference;
import com.google.cloud.firestore.DocumentSnapshot;
import com.google.cloud.firestore.Firestore;
import com.google.firebase.cloud.FirestoreClient;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Arrays;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

public class EmployeeFirebaseWorkflowTest {

    @Mock
    private Employee employee;

    @Mock
    private FirebaseConnection connection;

    @Mock
    private FirestoreClient mockedClient;

    @Mock
    private Firestore mockedFirestore;

    @Mock
    private CollectionReference mockedCollectionReference;

    @Mock
    private DocumentReference mockedDocumentReference;

    @Mock
    private ApiFuture<DocumentReference> apiFuture;

    @Mock
    private DocumentSnapshot documentSnapshot;

    @InjectMocks
    private final EmployeeFirebaseWorkflowImpl employeeFirebaseWorkflow =
            new EmployeeFirebaseWorkflowImpl();

    @BeforeEach
    public void setupMock() {
        MockitoAnnotations.initMocks(this); // enable mockito annotations
        employee = new Employee("e1", "n1", "c1",
                Role.SWE, "b1", "e1@e1.com", Arrays.asList("s1"));
    }
    @Test
    public void testGetInvalidEmployeeId() {
        try {
            employeeFirebaseWorkflow.getEmployeeWithId("e1");
            fail();
        } catch(FirebaseException ex) {
            assertEquals(ex.getMessage(), "No employee with employee id=e1");
        }
    }

//    @Test
//    public void testInvalidEmployeeId() {
//
//    }
}

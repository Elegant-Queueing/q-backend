package com.careerfair.q.workflow.database;

import com.careerfair.q.database.FirebaseConnection;
import com.careerfair.q.model.db.Employee;
import com.careerfair.q.workflow.database.implementation.EmployeeFirebaseWorkflowImpl;
import com.google.api.core.ApiFuture;
import com.google.cloud.firestore.CollectionReference;
import com.google.cloud.firestore.DocumentReference;
import com.google.cloud.firestore.DocumentSnapshot;
import com.google.cloud.firestore.Firestore;
import com.google.firebase.cloud.FirestoreClient;
import org.junit.jupiter.api.BeforeEach;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

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
    }
}

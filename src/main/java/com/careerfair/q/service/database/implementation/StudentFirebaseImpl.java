package com.careerfair.q.service.database.implementation;

import com.careerfair.q.model.db.Student;
import com.careerfair.q.service.database.StudentFirebase;
import com.google.api.core.ApiFuture;
import com.google.cloud.firestore.CollectionReference;
import com.google.cloud.firestore.DocumentReference;
import com.google.cloud.firestore.DocumentSnapshot;
import com.google.cloud.firestore.Firestore;
import com.google.firebase.cloud.FirestoreClient;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.concurrent.ExecutionException;

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
    public boolean registerStudent(String studentId, String employeeId) {
        return true;
    }

    @Override
    public Student getStudentWithId(String studentId) throws ExecutionException,
            InterruptedException {
        Firestore firestore = FirestoreClient.getFirestore();

        DocumentSnapshot documentSnapshot = firestore.collection(STUDENT_COLLECTION)
                .document(studentId).get().get();
        Student student = documentSnapshot.toObject(Student.class);

        if (student == null) {
            return null;
        }

        student.setStudentId(documentSnapshot.getId());
        return student;
    }

    @Override
    public Student getStudentWithEmail(String email) throws ExecutionException,
            InterruptedException {
        Firestore firestore = FirestoreClient.getFirestore();
        CollectionReference collectionReference = firestore.collection(STUDENT_COLLECTION);

        for (DocumentReference documentReference : collectionReference.listDocuments()) {
            DocumentSnapshot documentSnapshot = documentReference.get().get();
            String documentEmail = (String) documentSnapshot.get("email");

            if (email.equals(documentEmail)) {
                Student student = documentSnapshot.toObject(Student.class);
                assert student != null;

                student.setStudentId(documentSnapshot.getId());
                return student;
            }
        }

        return null;
    }
}

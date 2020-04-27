package com.careerfair.q.database.service.student.implementation;

import com.careerfair.q.database.service.student.StudentFirebase;
import com.google.api.core.ApiFuture;
import com.google.cloud.firestore.DocumentReference;
import com.google.cloud.firestore.Firestore;
import com.google.firebase.cloud.FirestoreClient;
import org.springframework.stereotype.Service;

import java.util.HashMap;

@Service
public class StudentFirebaseImpl implements StudentFirebase {

    public void test() {
        Firestore dbFirestore = FirestoreClient.getFirestore();
        HashMap<String, String> map = new HashMap<>();
        map.put("name", "TestName");
        map.put("temp", "TestTemp");
        ApiFuture<DocumentReference> collectionsApiFuture = dbFirestore.collection("test").add(map);
    }
}

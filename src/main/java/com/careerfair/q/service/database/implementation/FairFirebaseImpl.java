package com.careerfair.q.service.database.implementation;

import com.careerfair.q.model.db.Fair;
import com.careerfair.q.service.database.FairFirebase;
import com.google.cloud.firestore.CollectionReference;
import com.google.cloud.firestore.DocumentReference;
import com.google.cloud.firestore.Firestore;
import com.google.firebase.cloud.FirestoreClient;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

import static com.careerfair.q.util.contant.Fair.FAIR_COLLECTION;

@Component
public class FairFirebaseImpl implements FairFirebase {

    @Override
    public List<Fair> getAllFairs() throws ExecutionException, InterruptedException {
        Firestore dbFirestore = FirestoreClient.getFirestore();
        List<Fair> result = new ArrayList<>();

        CollectionReference collectionReference = dbFirestore.collection(FAIR_COLLECTION);
        for (DocumentReference documentReference : collectionReference.listDocuments()) {
            Fair fair = documentReference.get().get().toObject(Fair.class);
            result.add(fair);
        }
        return result;
    }
}

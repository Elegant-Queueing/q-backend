package com.careerfair.q.service.database.implementation;

import com.careerfair.q.model.db.Company;
import com.careerfair.q.model.db.Fair;
import com.careerfair.q.service.database.FairFirebase;
import com.careerfair.q.util.exception.InvalidRequestException;
import com.google.cloud.firestore.CollectionReference;
import com.google.cloud.firestore.DocumentReference;
import com.google.cloud.firestore.DocumentSnapshot;
import com.google.cloud.firestore.Firestore;
import com.google.firebase.cloud.FirestoreClient;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

import static com.careerfair.q.util.contant.Firebase.COMPANY_COLLECTION;
import static com.careerfair.q.util.contant.Firebase.FAIR_COLLECTION;

@Component
public class FairFirebaseImpl implements FairFirebase {

    @Override
    public List<Fair> getAllFairs() throws ExecutionException, InterruptedException {
        Firestore dbFirestore = FirestoreClient.getFirestore();
        List<Fair> result = new ArrayList<>();

        CollectionReference collectionReference = dbFirestore.collection(FAIR_COLLECTION);

        for (DocumentReference documentReference : collectionReference.listDocuments()) {
            DocumentSnapshot documentSnapshot = documentReference.get().get();
            Fair fair = documentSnapshot.toObject(Fair.class);
            assert fair != null;

            fair.setFairId(documentSnapshot.getId());
            result.add(fair);
        }

        return result;
    }

    @Override
    public Fair getFair(String fairId) throws ExecutionException, InterruptedException {
        Firestore firestore = FirestoreClient.getFirestore();
        Fair fair = firestore.collection(FAIR_COLLECTION)
                .document(fairId)
                .get().get()
                .toObject(Fair.class);

        if (fair == null) {
            throw new InvalidRequestException("No fair exists with id=" + fairId);
        }

        fair.setFairId(fairId);
        return fair;
    }

    @Override
    public Company getCompanyWithId(String fairId, String companyId) throws ExecutionException,
            InterruptedException {
        Fair fair = getFair(fairId);

        if (!fair.getCompanies().contains(companyId)) {
            throw new InvalidRequestException("The company with company id=" + companyId +
                    " is not present in the fair with fair id=" + fairId);
        }

        Firestore firestore = FirestoreClient.getFirestore();
        Company company = firestore.collection(COMPANY_COLLECTION)
                .document(companyId)
                .get().get()
                .toObject(Company.class);

        if (company == null) {
            throw new InvalidRequestException("No company exists with id=" + companyId);
        }

        return company;
    }
}

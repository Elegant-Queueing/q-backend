package com.careerfair.q.workflow.database.implementation;

import com.careerfair.q.model.db.Company;
import com.careerfair.q.model.db.Fair;
import com.careerfair.q.util.exception.FirebaseException;
import com.careerfair.q.workflow.database.FairFirebaseWorkflow;
import com.google.cloud.firestore.CollectionReference;
import com.google.cloud.firestore.DocumentReference;
import com.google.cloud.firestore.DocumentSnapshot;
import com.google.cloud.firestore.Firestore;
import com.google.firebase.cloud.FirestoreClient;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

import static com.careerfair.q.util.constant.Firebase.COMPANY_COLLECTION;
import static com.careerfair.q.util.constant.Firebase.FAIR_COLLECTION;

@Component
public class FairFirebaseWorkflowImpl implements FairFirebaseWorkflow {

    @Override
    public void checkValidCompanyId(String companyId) throws FirebaseException {
        Firestore firestore = FirestoreClient.getFirestore();

        try {
            if (!firestore.collection(COMPANY_COLLECTION).document(companyId).get().get()
                    .exists()) {
                throw new FirebaseException("The company with company id=" + companyId +
                        " does not exist");
            }
        } catch (ExecutionException | InterruptedException ex) {
            throw new FirebaseException(ex.getMessage());
        }
    }

    @Override
    public Company getCompanyWithId(String fairId, String companyId) throws FirebaseException {
        Fair fair = getFairWithId(fairId);

        if (!fair.getCompanies().contains(companyId)) {
            throw new FirebaseException("The company with company id=" + companyId +
                    " is not present in the fair with fair id=" + fairId);
        }

        Firestore firestore = FirestoreClient.getFirestore();

        try {
            Company company = firestore.collection(COMPANY_COLLECTION)
                    .document(companyId)
                    .get().get()
                    .toObject(Company.class);

            if (company == null) {
                throw new FirebaseException("No company exists with id=" + companyId);
            }

            return company;

        } catch (ExecutionException | InterruptedException ex) {
            throw new FirebaseException(ex.getMessage());
        }
    }

    @Override
    public Company getCompanyWithName(String companyName) {
        Firestore dbFirestore = FirestoreClient.getFirestore();
        CollectionReference collectionReference = dbFirestore.collection(COMPANY_COLLECTION);

        try {
            for (DocumentReference documentReference : collectionReference.listDocuments()) {
                DocumentSnapshot documentSnapshot = documentReference.get().get();
                String documentName = (String) documentSnapshot.get("name");
                assert documentName != null;

                if (companyName.toLowerCase().equals(documentName.toLowerCase())) {
                    Company company = documentSnapshot.toObject(Company.class);
                    assert company != null;

                    company.companyId = documentSnapshot.getId();
                    return company;
                }
            }

            throw new FirebaseException("No company exists with name=" + companyName);
        } catch (ExecutionException | InterruptedException ex) {
            throw new FirebaseException(ex.getMessage());
        }
    }

    @Override
    public Fair getFairWithId(String fairId) throws FirebaseException {
        Firestore firestore = FirestoreClient.getFirestore();

        try {
            Fair fair = firestore.collection(FAIR_COLLECTION)
                    .document(fairId)
                    .get().get()
                    .toObject(Fair.class);

            if (fair == null) {
                throw new FirebaseException("No fair exists with id=" + fairId);
            }

            fair.setFairId(fairId);
            return fair;
        } catch (ExecutionException | InterruptedException ex) {
            throw new FirebaseException(ex.getMessage());
        }
    }

    @Override
    public List<Fair> getAllFairs() throws FirebaseException {
        Firestore dbFirestore = FirestoreClient.getFirestore();
        List<Fair> result = new ArrayList<>();

        CollectionReference collectionReference = dbFirestore.collection(FAIR_COLLECTION);

        try {
            for (DocumentReference documentReference : collectionReference.listDocuments()) {
                DocumentSnapshot documentSnapshot = documentReference.get().get();
                Fair fair = documentSnapshot.toObject(Fair.class);
                assert fair != null;

                fair.setFairId(documentSnapshot.getId());
                result.add(fair);
            }

            return result;
        } catch (ExecutionException | InterruptedException ex) {
            throw new FirebaseException(ex.getMessage());
        }
    }
}

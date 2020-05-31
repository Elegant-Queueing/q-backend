package com.careerfair.q.workflow.database;

import com.careerfair.q.model.db.Company;
import com.careerfair.q.model.db.Fair;
import com.careerfair.q.util.exception.FirebaseException;

import java.util.List;

public interface FairFirebaseWorkflow {

    /**
     * Checks whether the company with the given id exists in the database
     *
     * @param companyId id of the company to validate
     * @throws FirebaseException if the company with id does not exists in the database
     */
    void checkValidCompanyId(String companyId) throws FirebaseException;

    /**
     * Gets the data of the company with the given id
     *
     * @param fairId id of the fair the company is present in
     * @param companyId id of the company to retrieve
     * @throws FirebaseException if the company with id does not exists in the database
     * @return Company
     */
    Company getCompanyWithId(String fairId, String companyId) throws FirebaseException;

    /**
     * Gets the data of the company with the given name
     *
     * @param companyName name of the company
     * @return Company
     * @throws FirebaseException if the company with name does not exist in the database
     */
    Company getCompanyWithName(String companyName) throws FirebaseException;

    /**
     * Gets the fair associated with the given id
     *
     * @param fairId id of the fair
     * @throws FirebaseException if the fair with id does not exists in the database
     * @return Fair
     */
    Fair getFairWithId(String fairId) throws FirebaseException;

    /**
     * Gets all the fairs from firebase
     *
     * @throws FirebaseException if something unexpected happens with Firebase
     * @return a list of all the fairs
     */
    List<Fair> getAllFairs() throws FirebaseException;
}

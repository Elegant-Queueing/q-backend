package com.careerfair.q.service.database;

import com.careerfair.q.model.db.Company;
import com.careerfair.q.model.db.Fair;

import java.util.List;
import java.util.concurrent.ExecutionException;

public interface FairFirebase {

    /**
     * Gets all the fairs from firebase
     *
     * @return a list of all the fairs
     */
    List<Fair> getAllFairs() throws ExecutionException, InterruptedException;

    /**
     * Gets the data of the company with the given id
     *
     * @param companyId id of the company to retrieve
     * @return Company
     */
    Company getCompanyWithId(String companyId) throws ExecutionException, InterruptedException;
}

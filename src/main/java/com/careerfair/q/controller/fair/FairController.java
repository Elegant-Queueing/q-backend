package com.careerfair.q.controller.fair;

import com.careerfair.q.service.fair.response.GetAllFairsResponse;
import com.careerfair.q.service.fair.response.GetCompanyResponse;

public interface FairController {

    /**
     * Gets all the fairs from the database
     *
     * @return GetAllFairsResponse
     */
    GetAllFairsResponse getAllFairs();

    /**
     * Gets the company with the given id
     *
     * @param companyId id of the company to retrieve
     * @return GetCompanyResponse
     */
    GetCompanyResponse getCompanyWithId(String companyId);
}

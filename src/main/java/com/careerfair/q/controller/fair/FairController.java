package com.careerfair.q.controller.fair;

import com.careerfair.q.service.fair.response.GetAllFairsResponse;
import com.careerfair.q.service.fair.response.GetCompanyResponse;
import com.careerfair.q.service.fair.response.GetFairResponse;

public interface FairController {

    /**
     * Gets all the fairs from the database
     *
     * @return GetAllFairsResponse
     */
    GetAllFairsResponse getAllFairs();

    /**
     * Gets the fair with the given id
     *
     * @param fairId id of the fair to retrieve
     * @return GetFairResponse
     */
    GetFairResponse getFairWithId(String fairId);

    /**
     * Gets the company with the given id present in the given fair
     *
     * @param fairId id of the fair the company is present in
     * @param companyId id of the company to retrieve
     * @return GetCompanyResponse
     */
    GetCompanyResponse getCompanyWithId(String fairId, String companyId);
}

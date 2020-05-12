package com.careerfair.q.service.fair;

import com.careerfair.q.service.fair.response.GetAllFairsResponse;
import com.careerfair.q.service.fair.response.GetCompanyResponse;
import com.careerfair.q.service.fair.response.GetFairResponse;

public interface FairService {

    /**
     * Gets all the fairs
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
     * Gets the company with the given id
     *
     * @param companyId id of the company to retrieve
     * @return GetCompanyResponse
     */
    GetCompanyResponse getCompanyWithId(String companyId);
}

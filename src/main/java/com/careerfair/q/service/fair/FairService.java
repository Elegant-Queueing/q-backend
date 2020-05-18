package com.careerfair.q.service.fair;

import com.careerfair.q.service.fair.response.GetAllFairsResponse;
import com.careerfair.q.service.fair.response.GetCompanyResponse;
import com.careerfair.q.service.fair.response.GetFairResponse;
import com.careerfair.q.service.fair.response.GetWaitTimeResponse;
import com.careerfair.q.util.enums.Role;

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
     * @param fairId id of the fair the company is present in
     * @param companyId id of the company to retrieve
     * @return GetCompanyResponse
     */
    GetCompanyResponse getCompanyWithId(String fairId, String companyId);

    /**
     * Gets the wait time for the given company and role
     *
     * @param companyId id of the company whose wait time is to retrieved
     * @param role role the student is recruiting for
     * @return GetWaitTimeResponse
     */
    GetWaitTimeResponse getCompanyWaitTime(String companyId, Role role);

    /**
     * Gets the wait time for the all the companies with a queue open for the given role
     *
     * @param role role the student is recruiting for
     * @return GetWaitTimeResponse
     */
    GetWaitTimeResponse getAllCompaniesWaitTime(Role role);
}

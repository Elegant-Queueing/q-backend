package com.careerfair.q.service.fair;

import com.careerfair.q.service.fair.response.GetAllFairsResponse;

public interface FairService {

    /**
     * Gets all the fairs
     *
     * @return GetAllFairsResponse
     */
    GetAllFairsResponse getAllFairs();
}

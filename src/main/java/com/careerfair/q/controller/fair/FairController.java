package com.careerfair.q.controller.fair;

import com.careerfair.q.service.fair.response.GetAllFairsResponse;

public interface FairController {

    /**
     * Gets all the fairs from the database
     *
     * @return GetAllFairsResponse
     */
    GetAllFairsResponse getAllFairs();
}

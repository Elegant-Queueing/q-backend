package com.careerfair.q.service.fair.implementation;

import com.careerfair.q.service.database.FirebaseService;
import com.careerfair.q.service.fair.FairService;
import com.careerfair.q.service.fair.response.GetAllFairsResponse;
import com.careerfair.q.service.fair.response.GetCompanyResponse;
import com.careerfair.q.service.fair.response.GetFairResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class FairServiceImpl implements FairService {

    private final FirebaseService firebaseService;

    public FairServiceImpl(@Autowired FirebaseService firebaseService) {
        this.firebaseService = firebaseService;
    }

    @Override
    public GetAllFairsResponse getAllFairs() {
        return new GetAllFairsResponse(firebaseService.getAllFairs());
    }

    @Override
    public GetFairResponse getFairWithId(String fairId) {
        return new GetFairResponse(firebaseService.getFairWithId(fairId));
    }

    @Override
    public GetCompanyResponse getCompanyWithId(String fairId, String companyId) {
        return new GetCompanyResponse(firebaseService.getCompanyWithId(fairId, companyId));
    }
}

package com.careerfair.q.service.fair.implementation;

import com.careerfair.q.service.database.FairFirebase;
import com.careerfair.q.service.fair.FairService;
import com.careerfair.q.service.fair.response.GetAllFairsResponse;
import com.careerfair.q.service.fair.response.GetCompanyResponse;
import com.careerfair.q.util.exception.InvalidRequestException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.concurrent.ExecutionException;

@Service
public class FairServiceImpl implements FairService {

    @Autowired private FairFirebase fairFirebase;

    @Override
    public GetAllFairsResponse getAllFairs() {
        try {
            return new GetAllFairsResponse(fairFirebase.getAllFairs());
        } catch (ExecutionException | InterruptedException ex) {
            throw new InvalidRequestException(ex.getMessage());
        }
    }

    @Override
    public GetCompanyResponse getCompanyWithId(String companyId) {
        try {
            return new GetCompanyResponse(fairFirebase.getCompanyWithId(companyId));
        } catch (ExecutionException | InterruptedException ex) {
            throw new InvalidRequestException(ex.getMessage());
        }
    }
}

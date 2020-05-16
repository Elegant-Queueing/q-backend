package com.careerfair.q.service.fair.implementation;

import com.careerfair.q.service.database.FirebaseService;
import com.careerfair.q.service.fair.FairService;
import com.careerfair.q.service.fair.response.GetAllFairsResponse;
import com.careerfair.q.service.fair.response.GetCompanyResponse;
import com.careerfair.q.service.fair.response.GetFairResponse;
import com.careerfair.q.service.fair.response.GetWaitTimeResponse;
import com.careerfair.q.service.queue.QueueService;
import com.careerfair.q.util.enums.Role;
import com.google.common.collect.Maps;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public class FairServiceImpl implements FairService {

    private final FirebaseService firebaseService;
    private final QueueService queueService;

    public FairServiceImpl(@Autowired FirebaseService firebaseService,
                           @Autowired QueueService queueService) {
        this.firebaseService = firebaseService;
        this.queueService = queueService;
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

    @Override
    public GetWaitTimeResponse getCompanyWaitTime(String companyId, Role role) {
        Map<String, Integer> companyWaitTime = Maps.newHashMap();
        companyWaitTime.put(companyId, queueService.getOverallWaitTime(companyId, role));
        return new GetWaitTimeResponse(companyWaitTime);
    }

    @Override
    public GetWaitTimeResponse getAllCompaniesWaitTime(Role role) {
        Map<String, Integer> companyWaitTimes = Maps.newHashMap();

        queueService.getAllEmployees().forEach(employee -> {
            if (employee.getRole() == role && employee.getVirtualQueueId() != null &&
                    !companyWaitTimes.containsKey(employee.getCompanyId())) {
                GetWaitTimeResponse waitTimeResponse = getCompanyWaitTime(employee.getCompanyId(),
                        role);
                companyWaitTimes.putAll(waitTimeResponse.getCompanyWaitTimes());
            }
        });

        return new GetWaitTimeResponse(companyWaitTimes);
    }
}

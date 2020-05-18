package com.careerfair.q.controller.fair.implementation;

import com.careerfair.q.controller.fair.FairController;
import com.careerfair.q.service.fair.FairService;
import com.careerfair.q.service.fair.response.GetAllFairsResponse;
import com.careerfair.q.service.fair.response.GetCompanyResponse;
import com.careerfair.q.service.fair.response.GetFairResponse;
import com.careerfair.q.service.fair.response.GetWaitTimeResponse;
import com.careerfair.q.util.enums.Role;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("fair")
public class FairControllerImpl implements FairController {

    private final FairService fairService;

    public FairControllerImpl(@Autowired FairService fairService) {
        this.fairService = fairService;
    }

    @GetMapping("/get-all")
    @Override
    public GetAllFairsResponse getAllFairs() {
        return fairService.getAllFairs();
    }

    @GetMapping("/get/fair-id/{fair-id}")
    @Override
    public GetFairResponse getFairWithId(@PathVariable("fair-id") String fairId) {
        return fairService.getFairWithId(fairId);
    }

    @GetMapping("/get/fair-id/{fair-id}/company-id/{company-id}")
    @Override
    public GetCompanyResponse getCompanyWithId(@PathVariable("fair-id") String fairId,
                                               @PathVariable("company-id") String companyId) {
        return fairService.getCompanyWithId(fairId, companyId);
    }

    @GetMapping("/wait-time/company-id/{company-id}/role/{role}")
    @Override
    public GetWaitTimeResponse getCompanyWaitTime(@PathVariable("company-id") String companyId,
                                                  @PathVariable("role") Role role) {
        return fairService.getCompanyWaitTime(companyId, role);
    }

    @GetMapping("/wait-time/role/{role}")
    @Override
    public GetWaitTimeResponse getAllCompaniesWaitTime(@PathVariable("role") Role role) {
        return fairService.getAllCompaniesWaitTime(role);
    }
}

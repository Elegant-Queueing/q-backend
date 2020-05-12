package com.careerfair.q.controller.fair.implementation;

import com.careerfair.q.controller.fair.FairController;
import com.careerfair.q.service.fair.FairService;
import com.careerfair.q.service.fair.response.GetAllFairsResponse;
import com.careerfair.q.service.fair.response.GetCompanyResponse;
import com.careerfair.q.service.fair.response.GetFairResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("fair")
public class FairControllerImpl implements FairController {

    @Autowired private FairService fairService;

    @GetMapping("/get-all")
    @Override
    public GetAllFairsResponse getAllFairs() {
        return fairService.getAllFairs();
    }

    @GetMapping("get/fair-id/{fair-id}")
    @Override
    public GetFairResponse getFairWithId(@PathVariable("fair-id") String fairId) {
        return fairService.getFairWithId(fairId);
    }

    @GetMapping("/get/company-id/{company-id}")
    @Override
    public GetCompanyResponse getCompanyWithId(@PathVariable("company-id") String companyId) {
        return fairService.getCompanyWithId(companyId);
    }
}

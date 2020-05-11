package com.careerfair.q.controller.fair.implementation;

import com.careerfair.q.controller.fair.FairController;
import com.careerfair.q.service.fair.FairService;
import com.careerfair.q.service.fair.response.GetAllFairsResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
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
}

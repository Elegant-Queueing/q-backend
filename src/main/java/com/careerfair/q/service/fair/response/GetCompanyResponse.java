package com.careerfair.q.service.fair.response;

import com.careerfair.q.model.db.Company;
import lombok.Data;

@Data
public class GetCompanyResponse {

    private final Company company;
}

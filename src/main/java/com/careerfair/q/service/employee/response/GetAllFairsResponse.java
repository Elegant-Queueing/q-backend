package com.careerfair.q.service.employee.response;

import com.careerfair.q.model.db.Fair;
import lombok.Data;

import java.util.List;

@Data
public class GetAllFairsResponse {
    private final List<Fair> fairs;
}

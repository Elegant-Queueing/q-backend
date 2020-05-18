package com.careerfair.q.service.fair.response;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.Map;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class GetWaitTimeResponse {

    @JsonProperty("company-wait-times")
    private final Map<String, Integer> companyWaitTimes;
}

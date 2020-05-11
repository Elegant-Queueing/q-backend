package com.careerfair.q.service.queue.response;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.List;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class GetWaitTimeResponse {

    @JsonProperty("companies")
    private final List<String> companies;

    @JsonProperty("wait-times")
    private final List<Integer> waitTimes;  // in seconds
}

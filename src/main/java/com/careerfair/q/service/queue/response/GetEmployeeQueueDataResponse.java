package com.careerfair.q.service.queue.response;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class GetEmployeeQueueDataResponse {

    @JsonProperty("data")
    private final EmployeeQueueData employeeQueueData;
}

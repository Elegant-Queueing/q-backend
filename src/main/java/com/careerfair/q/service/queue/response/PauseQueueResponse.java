package com.careerfair.q.service.queue.response;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class PauseQueueResponse {

    @JsonProperty("data")
    private final EmployeeQueueData employeeQueueData;
}

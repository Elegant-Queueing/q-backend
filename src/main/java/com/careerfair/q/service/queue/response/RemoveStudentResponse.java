package com.careerfair.q.service.queue.response;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class RemoveStudentResponse {

    @JsonProperty("data")
    private final EmployeeQueueData employeeQueueData;
}

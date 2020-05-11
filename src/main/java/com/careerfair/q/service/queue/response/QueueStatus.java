package com.careerfair.q.service.queue.response;

import com.careerfair.q.model.redis.Employee;
import com.careerfair.q.util.enums.QueueType;
import com.careerfair.q.util.enums.Role;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.NonNull;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class QueueStatus {

    @JsonProperty("company-id")
    private final String companyId;

    @JsonProperty("queue-id")
    private final String queueId;

    @JsonProperty("queue-type")
    private final QueueType queueType;

    @JsonProperty("role")
    private final Role role;

    @JsonProperty("position")
    private int position;

    @JsonProperty("wait-time")
    private int waitTime;  // in seconds

    @JsonProperty("employee")
    private Employee employee;
}

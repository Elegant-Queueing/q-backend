package com.careerfair.q.service.queue.response;

import com.careerfair.q.model.redis.Employee;
import com.careerfair.q.util.enums.QueueType;
import com.careerfair.q.util.enums.Role;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.cloud.Timestamp;
import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class QueueStatus {

    @JsonProperty("queue-id")
    private final String queueId;

    @JsonProperty("queue-type")
    private final QueueType queueType;

    @JsonProperty("role")
    private final Role role;

    @JsonProperty("position")
    private final int position;

    @JsonProperty("wait-time")
    private final int waitTime;

    @JsonProperty("joined-at")
    private final Timestamp joinedAt;

    @JsonProperty("employee")
    private Employee employee;
}

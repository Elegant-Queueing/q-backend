package com.careerfair.q.service.queue.response;

import com.careerfair.q.model.redis.Employee;
import com.careerfair.q.util.enums.QueueType;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.cloud.Timestamp;
import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class QueueStatus {

    @JsonProperty("queue-type")
    private final QueueType queueType;

    @JsonProperty("position")
    private final int position;

    @JsonProperty("wait-time")
    private final int waitTime;

    @JsonProperty("joined-at")
    private final Timestamp joinedAt;

    @JsonProperty("employee")
    private final Employee employee;
}

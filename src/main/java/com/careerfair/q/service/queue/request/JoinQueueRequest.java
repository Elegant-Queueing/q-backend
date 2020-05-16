package com.careerfair.q.service.queue.request;

import com.careerfair.q.model.redis.Student;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;

@Getter
@JsonIgnoreProperties(ignoreUnknown = true)
public class JoinQueueRequest {

    @JsonProperty("student")
    private Student student;
}

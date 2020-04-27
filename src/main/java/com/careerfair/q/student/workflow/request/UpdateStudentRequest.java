package com.careerfair.q.student.workflow.request;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class UpdateStudentRequest {

    @JsonProperty(value = "name", required = true)
    private final String name;
}

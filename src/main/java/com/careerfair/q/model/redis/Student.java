package com.careerfair.q.model.redis;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.NonNull;

import java.io.Serializable;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class Student implements Serializable {
    @JsonProperty("id")
    @NonNull private final String id;

    @JsonProperty("name")
    @NonNull private final String name;
}

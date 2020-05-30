package com.careerfair.q.service.student.request;

import com.careerfair.q.service.database.deserializer.TimestampDeserializer;
import com.careerfair.q.util.enums.Role;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.google.cloud.Timestamp;
import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class StudentRequest {
    @JsonProperty("first_name")
    private String firstName;

    @JsonProperty("last_name")
    private String lastName;

    @JsonProperty("university_id")
    private String universityId;

    @JsonProperty("major")
    private String major;

    @JsonProperty("role")
    private Role role;

    @JsonProperty("bio")
    private String bio;

    @JsonProperty("email")
    private String email;

    @JsonProperty("gpa")
    private Double gpa;

    @JsonProperty("grad_date")
    @JsonDeserialize(using = TimestampDeserializer.class)
    private Timestamp graduationDate;

    @JsonProperty("international")
    private Boolean international;
}
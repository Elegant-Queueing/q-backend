package com.careerfair.q.service.student.request;
import com.careerfair.q.service.database.deserializer.TimestampDeserializer;
import com.careerfair.q.util.enums.Role;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.google.cloud.Timestamp;
import lombok.Getter;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

@Getter
@JsonIgnoreProperties(ignoreUnknown = true)
public class UpdateStudentRequest {
    @JsonProperty("first_name")
    @NotEmpty
    public String firstName;

    @JsonProperty("last_name")
    @NotEmpty
    public String lastName;

    @JsonProperty("university_id")
    @NotEmpty
    public String universityId;

    @JsonProperty("major")
    @NotEmpty
    public String major;

    @JsonProperty("role")
    @NotNull(message = "You must provide Role!")
    public Role role;

    @JsonProperty("bio")
    @NotNull
    public String bio;

    @JsonProperty("email")
    @NotEmpty
    public String email;

    @JsonProperty("gpa")
    @NotNull
    public Double gpa;

    @JsonProperty("grad_date")
    @NotNull
    @JsonDeserialize(using = TimestampDeserializer.class)
    public Timestamp graduationDate;

    @JsonProperty("international")
    @NotNull
    public Boolean international;
}
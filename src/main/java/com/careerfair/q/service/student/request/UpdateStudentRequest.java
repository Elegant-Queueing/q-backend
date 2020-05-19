package com.careerfair.q.service.student.request;
import com.careerfair.q.service.database.deserializer.TimestampDeserializer;
import com.careerfair.q.util.enums.Role;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.google.cloud.Timestamp;
import lombok.Getter;

@Getter
@JsonIgnoreProperties(ignoreUnknown = true)
public class UpdateStudentRequest {
    @JsonProperty("first_name")
    public String firstName;

    @JsonProperty("last_name")
    public String lastName;

    @JsonProperty("university_id")
    public String universityId;

    @JsonProperty("major")
    public String major;

    @JsonProperty("role")
    public Role role;

    @JsonProperty("bio")
    public String bio;

    @JsonProperty("email")
    public String email;

    @JsonProperty("gpa")
    public Double gpa;

    @JsonProperty("grad_date")
    @JsonDeserialize(using = TimestampDeserializer.class)
    public Timestamp graduationDate;

    @JsonProperty("international")
    public Boolean international;
}
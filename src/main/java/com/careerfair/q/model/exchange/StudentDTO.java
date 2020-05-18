package com.careerfair.q.model.exchange;

import com.careerfair.q.service.database.deserializer.TimestampDeserializer;
import com.careerfair.q.util.enums.Role;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.google.cloud.Timestamp;
import com.google.cloud.firestore.annotation.PropertyName;
import javax.validation.constraints.NotNull;

public class StudentDTO {
    @PropertyName("first_name")
    @JsonProperty("first_name")
    @NotNull
    public String firstName;

    @PropertyName("last_name")
    @JsonProperty("last_name")
    @NotNull
    public String lastName;

    @PropertyName("university_id")
    @JsonProperty("university_id")
    @NotNull
    public String universityId;

    @PropertyName("major")
    @JsonProperty("major")
    @NotNull
    public String major;

    @PropertyName("role")
    @JsonProperty("role")
    @NotNull
    public Role role;

    @PropertyName("bio")
    @JsonProperty("bio")
    @NotNull
    public String bio;

    @PropertyName("email")
    @JsonProperty("email")
    @NotNull
    public String email;

    @PropertyName("gpa")
    @JsonProperty("gpa")
    @NotNull
    public Double gpa;

    @PropertyName("grad_date")
    @JsonProperty("grad_date")
    @NotNull
    @JsonDeserialize(using = TimestampDeserializer.class)
    public Timestamp graduationDate;

    @PropertyName("international")
    @JsonProperty("international")
    @NotNull
    public Boolean international;
}

package com.careerfair.q.model.db;

import com.careerfair.q.service.database.deserializer.TimestampDeserializer;
import com.careerfair.q.util.enums.Role;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.google.cloud.Timestamp;
import com.google.cloud.firestore.annotation.PropertyName;
import java.util.List;

public class Student {
    @PropertyName("student_id")
    @JsonProperty("student_id")
    public String studentId;

    @PropertyName("first_name")
    @JsonProperty("first_name")
    public String firstName;

    @PropertyName("last_name")
    @JsonProperty("last_name")
    public String lastName;

    @PropertyName("university_id")
    @JsonProperty("university_id")
    public String universityId;

    @PropertyName("major")
    @JsonProperty("major")
    public String major;

    @PropertyName("role")
    @JsonProperty("role")
    public Role role;

    @PropertyName("bio")
    @JsonProperty("bio")
    public String bio;

    @PropertyName("email")
    @JsonProperty("email")
    public String email;

    @PropertyName("gpa")
    @JsonProperty("gpa")
    public Double gpa;

    @PropertyName("grad_date")
    @JsonProperty("grad_date")
    @JsonDeserialize(using = TimestampDeserializer.class)
    public Timestamp graduationDate;

    @PropertyName("international")
    @JsonProperty("international")
    public Boolean international;

    @PropertyName("employees")
    @JsonProperty("employees")
    public List<String> employees;

}

package com.careerfair.q.model.db;

import com.careerfair.q.service.database.deserializer.TimestampDeserializer;
import com.careerfair.q.util.enums.Role;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.google.cloud.Timestamp;
import com.google.cloud.firestore.annotation.Exclude;
import com.google.cloud.firestore.annotation.PropertyName;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.List;

public class Student {
    @PropertyName("student_id")
    @Exclude
    public String studentId;

    @PropertyName("first_name")
    @NotEmpty
    public String firstName;

    @PropertyName("last_name")
    @NotEmpty
    public String lastName;

    @PropertyName("university_id")
    @NotEmpty
    public String universityId;

    @PropertyName("major")
    @NotEmpty
    public String major;

    @PropertyName("role")
    @NotNull(message = "You must provide Role!")
    public Role role;

    @PropertyName("bio")
    @NotNull
    public String bio;

    @PropertyName("email")
    @NotEmpty
    public String email;

    @PropertyName("gpa")
    @NotNull
    public Double gpa;

    @PropertyName("grad_date")
    @NotNull
    @JsonDeserialize(using = TimestampDeserializer.class)
    public Timestamp graduationDate;

    @PropertyName("international")
    @NotNull
    public Boolean international;

    @PropertyName("employees")
    @Exclude
    public List<String> employees;
}

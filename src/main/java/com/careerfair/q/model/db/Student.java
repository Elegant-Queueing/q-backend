package com.careerfair.q.model.db;

import com.careerfair.q.service.database.deserializer.TimestampDeserializer;
import com.careerfair.q.util.enums.Role;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.google.cloud.Timestamp;
import com.google.cloud.firestore.annotation.Exclude;
import com.google.cloud.firestore.annotation.PropertyName;
import java.util.List;

public class Student {
    @PropertyName("student_id")
    @Exclude
    public String studentId;

    @PropertyName("first_name")
    public String firstName;

    @PropertyName("last_name")
    public String lastName;

    @PropertyName("university_id")
    public String universityId;

    @PropertyName("major")
    public String major;

    @PropertyName("role")
    public Role role;

    @PropertyName("bio")
    public String bio;

    @PropertyName("email")
    public String email;

    @PropertyName("gpa")
    public Double gpa;

    @PropertyName("grad_date")
    @JsonDeserialize(using = TimestampDeserializer.class)
    public Timestamp graduationDate;

    @PropertyName("international")
    public Boolean international;

    @PropertyName("employees")
    @Exclude
    public List<String> employees;
}

package com.careerfair.q.service.student.response;

import com.careerfair.q.model.db.Student;
import com.careerfair.q.util.enums.Role;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.cloud.Timestamp;
import lombok.Data;

@Data
public abstract class StudentResponse {

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
    private Timestamp graduationDate;

    @JsonProperty("international")
    private Boolean international;

    public StudentResponse(Student student) {
        this.firstName = student.firstName;
        this.lastName = student.lastName;
        this.universityId = student.universityId;
        this.major = student.major;
        this.role = student.role;
        this.bio = student.bio;
        this.email = student.email;
        this.gpa = student.gpa;
        this.graduationDate = student.graduationDate;
        this.international = student.international;
    }
}

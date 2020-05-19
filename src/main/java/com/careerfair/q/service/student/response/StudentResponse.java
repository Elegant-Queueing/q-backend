package com.careerfair.q.service.student.response;

import com.careerfair.q.model.db.Student;
import com.careerfair.q.util.enums.Role;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.cloud.Timestamp;
import lombok.Data;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

@Data
public abstract class StudentResponse {
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
    @NotEmpty
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
    public Timestamp graduationDate;

    @JsonProperty("international")
    @NotNull
    public Boolean international;

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

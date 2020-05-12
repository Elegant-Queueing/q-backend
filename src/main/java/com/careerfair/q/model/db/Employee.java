package com.careerfair.q.model.db;

import com.careerfair.q.util.enums.Role;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.cloud.firestore.annotation.PropertyName;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class Employee {

    @PropertyName("employee_id")
    @JsonProperty("employee_id")
    public String employeeId;

    @PropertyName("name")
    @JsonProperty("name")
    public String name;

    @PropertyName("company_id")
    @JsonProperty("company_id")
    public String companyId;

    @PropertyName("role")
    @JsonProperty("role")
    public Role role;

    @PropertyName("bio")
    @JsonProperty("bio")
    public String bio;

    @PropertyName("email")
    @JsonProperty("email")
    public String email;

    @PropertyName("students")
    @JsonProperty("students")
    public List<String> students;
}

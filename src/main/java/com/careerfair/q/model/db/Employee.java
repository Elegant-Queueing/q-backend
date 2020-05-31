package com.careerfair.q.model.db;

import com.careerfair.q.util.enums.Role;
import com.google.cloud.firestore.annotation.Exclude;
import com.google.cloud.firestore.annotation.PropertyName;

import java.util.List;

public class Employee {

    @PropertyName("employee_id")
    @Exclude
    public String employeeId;

    @PropertyName("name")
    public String name;

    @PropertyName("company_id")
    public String companyId;

    @PropertyName("role")
    public Role role;

    @PropertyName("bio")
    public String bio;

    @PropertyName("email")
    public String email;

    @PropertyName("students")
    @Exclude
    public List<String> students;
}

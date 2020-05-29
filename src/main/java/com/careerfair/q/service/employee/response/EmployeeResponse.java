package com.careerfair.q.service.employee.response;

import com.careerfair.q.model.db.Employee;
import com.careerfair.q.util.enums.Role;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public abstract class EmployeeResponse {
    @JsonProperty("name")
    private String name;

    @JsonProperty("company_id")
    private String companyId;

    @JsonProperty("role")
    private Role role;

    @JsonProperty("bio")
    private String bio;

    @JsonProperty("email")
    private String email;

    public EmployeeResponse(Employee employee) {
        this.name = employee.name;
        this.companyId = employee.companyId;
        this.role = employee.role;
        this.bio = employee.bio;
        this.email = employee.email;
    }
}

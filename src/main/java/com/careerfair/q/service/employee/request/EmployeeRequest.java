package com.careerfair.q.service.employee.request;

import com.careerfair.q.util.enums.Role;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class EmployeeRequest {

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
}

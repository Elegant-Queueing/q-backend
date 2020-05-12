package com.careerfair.q.service.employee.response;

import com.careerfair.q.model.db.Employee;
import com.careerfair.q.util.enums.Role;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class GetEmployeeResponse extends EmployeeResponse {
    @JsonProperty("employee")
    private final Employee employee;

}

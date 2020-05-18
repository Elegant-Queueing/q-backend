package com.careerfair.q.service.employee.response;

import com.careerfair.q.model.db.Employee;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class GetEmployeeResponse extends EmployeeResponse {

    @JsonProperty("employee")
    private final Employee employee;
}

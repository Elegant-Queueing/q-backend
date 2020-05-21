package com.careerfair.q.service.employee.response;

import com.careerfair.q.model.db.Employee;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@EqualsAndHashCode(callSuper = true)
public class GetEmployeeResponse extends EmployeeResponse {
    @JsonProperty("students")
    private List<String> students;

    public GetEmployeeResponse(Employee employee) {
        super(employee);
        this.students = employee.students;
    }
}

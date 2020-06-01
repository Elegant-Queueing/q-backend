package com.careerfair.q.service.employee.response;

import com.careerfair.q.model.db.Employee;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.List;

@Data
public class DeleteEmployeeResponse extends EmployeeResponse {

    @JsonProperty("students")
    private List<String> students;

    public DeleteEmployeeResponse(Employee employee) {
        super(employee);
        this.students = employee.students;
    }
}

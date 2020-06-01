package com.careerfair.q.service.employee.response;

import com.careerfair.q.model.db.Employee;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;

@Data
@EqualsAndHashCode(callSuper = true)
public class DeleteEmployeeResponse extends EmployeeResponse {

    @JsonProperty("students")
    private List<String> students;

    public DeleteEmployeeResponse(Employee employee) {
        super(employee);
        this.students = employee.students;
    }
}

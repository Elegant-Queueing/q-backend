package com.careerfair.q.service.employee.response;

import com.careerfair.q.model.db.Employee;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
public class AddEmployeeResponse extends EmployeeResponse {
    public AddEmployeeResponse(Employee employee) {
        super(employee);
    }
}

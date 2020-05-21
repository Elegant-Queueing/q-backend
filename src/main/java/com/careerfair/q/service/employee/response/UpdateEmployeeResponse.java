package com.careerfair.q.service.employee.response;

import com.careerfair.q.model.db.Employee;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
public class UpdateEmployeeResponse extends EmployeeResponse {
    public UpdateEmployeeResponse(Employee employee) {
        super(employee);
    }
}

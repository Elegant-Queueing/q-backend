package com.careerfair.q.service.employee.response;

import com.careerfair.q.model.db.Employee;

public class GetEmployeeResponse extends EmployeeResponse {
    public GetEmployeeResponse(Employee employee) {
        super(employee);
    }
}

package com.careerfair.q.service.employee.request;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class AddEmployeeRequest extends EmployeeRequest {
}

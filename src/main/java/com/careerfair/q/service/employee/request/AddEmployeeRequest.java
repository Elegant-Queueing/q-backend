package com.careerfair.q.service.employee.request;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Getter;

@Getter
@JsonIgnoreProperties(ignoreUnknown = true)
public class AddEmployeeRequest extends EmployeeRequest {

}

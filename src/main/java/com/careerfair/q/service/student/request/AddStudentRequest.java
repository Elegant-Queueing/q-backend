package com.careerfair.q.service.student.request;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Getter;

@Getter
@JsonIgnoreProperties(ignoreUnknown = true)
public class AddStudentRequest extends StudentRequest {
}

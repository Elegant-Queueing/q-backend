package com.careerfair.q.service.student.request;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class UpdateStudentRequest extends StudentRequest {
}
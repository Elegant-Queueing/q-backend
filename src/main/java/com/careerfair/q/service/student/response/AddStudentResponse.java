package com.careerfair.q.service.student.response;

import com.careerfair.q.model.exchange.StudentDTO;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class AddStudentResponse extends StudentResponse {
    @JsonProperty("student")
    private final StudentDTO studentDTO;
}

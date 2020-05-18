package com.careerfair.q.service.student.request;

import com.careerfair.q.model.exchange.StudentDTO;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import javax.validation.Valid;

@Getter
@JsonIgnoreProperties(ignoreUnknown = true)
public class AddStudentRequest {
    @JsonProperty("student")
    private @Valid StudentDTO studentDTO;
}

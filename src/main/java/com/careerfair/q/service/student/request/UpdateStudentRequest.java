package com.careerfair.q.service.student.request;
import com.careerfair.q.model.db.Student;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;

@Getter
@JsonIgnoreProperties(ignoreUnknown = true)
public class UpdateStudentRequest {
    @JsonProperty("student")
    private Student student;
}
package com.careerfair.q.service.student.response;

import com.careerfair.q.model.db.Student;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;

@Data
@EqualsAndHashCode(callSuper = true)
public class GetStudentResponse extends StudentResponse {
    @JsonProperty("employees")
    public List<String> employees;

    public GetStudentResponse(Student student) {
        super(student);
        this.employees = student.employees;
    }
}

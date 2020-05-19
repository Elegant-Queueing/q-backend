package com.careerfair.q.service.student.response;

import com.careerfair.q.model.db.Student;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Data
@Setter
@Getter
@EqualsAndHashCode(callSuper = true)
public class GetStudentResponse extends StudentResponse {
    @JsonProperty("employees")
    private List<String> employees;

    public GetStudentResponse(Student student) {
        super(student);
        this.employees = student.employees;
    }
}

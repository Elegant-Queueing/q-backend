package com.careerfair.q.service.student.response;

import com.careerfair.q.model.db.Student;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Setter
@Getter
@Data
@EqualsAndHashCode(callSuper = true)
public class DeleteStudentResponse extends StudentResponse {
    @JsonProperty("student_id")
    private String studentId;

    @JsonProperty("employees")
    private List<String> employees;

    public DeleteStudentResponse(Student student) {
        super(student);
        this.studentId = student.studentId;
        this.employees = student.employees;
    }
}

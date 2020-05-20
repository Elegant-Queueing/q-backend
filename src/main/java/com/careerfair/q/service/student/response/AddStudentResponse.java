package com.careerfair.q.service.student.response;

import com.careerfair.q.model.db.Student;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
public class AddStudentResponse extends StudentResponse {
    public AddStudentResponse(Student student) {
        super(student);
    }
}

package com.careerfair.q.service.student.response;

import com.careerfair.q.model.db.Student;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
public class UpdateStudentResponse extends StudentResponse {
    public UpdateStudentResponse(Student student) {
        super(student);
    }
}
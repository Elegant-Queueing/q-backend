package com.careerfair.q.service.student.response;

import com.careerfair.q.model.db.Student;
import lombok.Data;

@Data
public class UpdateStudentResponse extends StudentResponse {
    private final Student student;
}

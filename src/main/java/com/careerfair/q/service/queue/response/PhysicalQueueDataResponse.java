package com.careerfair.q.service.queue.response;

import com.careerfair.q.model.Student;
import lombok.Data;

import java.util.List;

@Data
public class PhysicalQueueDataResponse {
    private final List<Student> students;
    private final int numStudentsTalked;
    private final int avgTime;
}

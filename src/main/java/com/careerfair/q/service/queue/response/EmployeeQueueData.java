package com.careerfair.q.service.queue.response;

import com.careerfair.q.model.Student;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.List;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class EmployeeQueueData {

    @JsonProperty("students")
    private final List<Student> students;

    @JsonProperty("num-students")
    private final int numStudentsTalked;

    @JsonProperty("average-time")
    private final int averageTime;
}

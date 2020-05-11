package com.careerfair.q.service.queue.response;

import com.careerfair.q.model.redis.Student;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.List;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class EmployeeQueueData {

    @JsonProperty("students")
    private final List<Student> students;

    @JsonProperty("num-registered-students")
    private final int numRegisteredStudents;

    @JsonProperty("average-time-per-student")
    private final double averageTimePerStudent;  // in seconds
}

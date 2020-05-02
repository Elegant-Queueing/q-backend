package com.careerfair.q.model.redis;

import lombok.Data;

import java.io.Serializable;
import java.util.UUID;

@Data
public class Employee implements Serializable {
    private final String id;
    private final String windowQueueId;
    private final String physicalQueueId;
    private final int totalTimeSpent;
    private final int numRegisteredStudents;
}

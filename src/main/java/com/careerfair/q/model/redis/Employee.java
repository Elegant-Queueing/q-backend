package com.careerfair.q.model.redis;

import com.careerfair.q.util.enums.Role;
import lombok.Data;

import java.io.Serializable;

@Data
public class Employee implements Serializable {
    private final String id;
    private final String companyId;
    private final Role role;
    private String virtualQueueId;
    private String windowQueueId;
    private String physicalQueueId;
    private long totalTimeSpent;
    private int numRegisteredStudents;
}

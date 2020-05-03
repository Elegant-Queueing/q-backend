package com.careerfair.q.model.redis;

import com.careerfair.q.util.enums.Role;
import lombok.Data;

import java.io.Serializable;

@Data
public class Employee implements Serializable {
    private final String id;
    private final String companyId;
    private final Role role;
    private final String windowQueueId;
    private final String physicalQueueId;
    private int totalTimeSpent;
    private int numRegisteredStudents;
}

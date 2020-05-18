package com.careerfair.q.model.redis;

import com.careerfair.q.util.enums.Role;
import lombok.Data;
import lombok.NonNull;

import java.io.Serializable;

@Data
public class Employee implements Serializable {
    @NonNull private final String id;
    @NonNull private final String companyId;
    @NonNull private final Role role;
    private String virtualQueueId;
    private String windowQueueId;
    private String physicalQueueId;
    private long totalTimeSpent;
    private int numRegisteredStudents;
}

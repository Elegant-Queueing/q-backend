package com.careerfair.q.model.redis;

import com.careerfair.q.util.enums.Role;
import lombok.*;

import java.io.Serializable;

@Data
public class Employee implements Serializable {
    @NonNull private String id;
    @NonNull private String companyId;
    @NonNull private Role role;
    private String virtualQueueId;
    private String windowQueueId;
    private String physicalQueueId;
    private long totalTimeSpent;
    private int numRegisteredStudents;
}

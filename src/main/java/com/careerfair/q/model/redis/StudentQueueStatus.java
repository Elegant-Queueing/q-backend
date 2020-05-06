package com.careerfair.q.model.redis;

import com.careerfair.q.util.enums.QueueType;
import com.careerfair.q.util.enums.Role;
import com.google.cloud.Timestamp;
import lombok.*;

import java.io.Serializable;

@Data
public class StudentQueueStatus implements Serializable {
    // TODO: REMOVE vvvvvvvvvvv
    @NonNull private final String name;
    // TODO: REMOVE ^^^^^^^^^^^

    @NonNull private final String companyId;
    @NonNull private final String studentId;
    @NonNull private final Role role;
    private String employeeId;
    private String queueId;
    private QueueType queueType;
    private long positionWhenJoinedPhysicalQueue;
    private Timestamp joinedWindowQueueAt;
    private Timestamp joinedPhysicalQueueAt;
}

package com.careerfair.q.model.redis;

import com.careerfair.q.util.enums.QueueType;
import com.careerfair.q.util.enums.Role;
import com.google.cloud.Timestamp;
import lombok.*;

import java.io.Serializable;

@Data
public class StudentQueueStatus implements Serializable {
    @NonNull private String companyId;
    @NonNull private String studentId;
    @NonNull private Role role;
    @NonNull private Timestamp joinedVirtualQueueAt;
    private String employeeId;
    private String queueId;
    private QueueType queueType;
    private Timestamp joinedWindowQueueAt;
    private Timestamp joinedPhysicalQueueAt;
}

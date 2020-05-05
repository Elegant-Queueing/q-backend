package com.careerfair.q.model.redis;

import com.careerfair.q.util.enums.QueueType;
import com.careerfair.q.util.enums.Role;
import com.google.cloud.Timestamp;
import lombok.Data;

import java.io.Serializable;

@Data
public class StudentQueueStatus implements Serializable {
    private final String companyId;
    private final String studentId;
    private final Role role;
    private final Timestamp joinedVirtualQueueAt;
    private String employeeId;
    private String queueId;
    private QueueType queueType;
    private Timestamp joinedWindowQueueAt;
    private Timestamp joinedPhysicalQueueAt;
}

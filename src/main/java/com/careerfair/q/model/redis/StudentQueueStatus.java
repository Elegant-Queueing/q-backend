package com.careerfair.q.model.redis;

import com.careerfair.q.util.enums.QueueType;
import com.careerfair.q.util.enums.Role;
import com.google.cloud.Timestamp;
import lombok.Data;

import java.io.Serializable;

@Data
public class StudentQueueStatus implements Serializable {
    private final String studentId;
    private String companyId;
    private Role role;
    private String employeeId;
    private String queueId;
    private QueueType queueType;
    private Timestamp joinedVirtualQueueAt;
    private Timestamp joinedWindowQueueAt;
    private Timestamp joinedPhysicalQueueAt;
}

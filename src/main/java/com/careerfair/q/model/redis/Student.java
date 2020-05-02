package com.careerfair.q.model.redis;

import com.google.cloud.Timestamp;
import lombok.Data;

@Data
public class Student {
    private final String id;
    private final String name;
    private final Timestamp joinedVirtualQueueAt;
    private final Timestamp joinedWindowQueueAt;
}

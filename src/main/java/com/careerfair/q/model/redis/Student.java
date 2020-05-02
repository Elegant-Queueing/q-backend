package com.careerfair.q.model.redis;

import com.google.cloud.Timestamp;
import lombok.Data;

import java.io.Serializable;

@Data
public class Student implements Serializable {
    private final String id;
    private final String name;
    private final Timestamp joinedVirtualQueueAt;
    private final Timestamp joinedWindowQueueAt;
}

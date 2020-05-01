package com.careerfair.q.model.redis;

import lombok.Data;

import java.io.Serializable;
import java.util.UUID;

@Data
public class Employee implements Serializable {
    private final String id;
    private final UUID waitingQueueId;
    private final UUID physicalQueueId;
    private final int totalTalkingDuration;
    private final int numStudentsTalked;
}

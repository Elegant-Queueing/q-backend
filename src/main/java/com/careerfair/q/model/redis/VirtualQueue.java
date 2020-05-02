package com.careerfair.q.model.redis;

import lombok.Data;

import java.io.Serializable;
import java.util.Set;
import java.util.UUID;

@Data
public class VirtualQueue implements Serializable {
    private final String virtualQueueId;
    private final Set<String> employeeIds;
}

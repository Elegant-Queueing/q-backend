package com.careerfair.q.model.redis;

import lombok.Data;
import lombok.NonNull;

import java.io.Serializable;
import java.util.Set;

@Data
public class VirtualQueueData implements Serializable {
    @NonNull private final String virtualQueueId;
    @NonNull private final Set<String> employeeIds;
}

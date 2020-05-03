package com.careerfair.q.model.redis;

import lombok.Data;

import java.io.Serializable;
import java.util.Set;

@Data
public class Company implements Serializable {
    private final String virtualQueueId;
    private final Set<String> employeeIds;
}

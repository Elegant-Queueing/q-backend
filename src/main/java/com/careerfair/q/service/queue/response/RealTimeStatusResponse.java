package com.careerfair.q.service.queue.response;

import com.careerfair.q.enums.QueueType;
import com.careerfair.q.enums.Role;
import com.careerfair.q.model.Employee;
import com.google.cloud.Timestamp;
import lombok.Data;

@Data
public class RealTimeStatusResponse {
    private final int position;
    private final int waitTime;
    private final String queueId;
    private final QueueType queueType;
    private final Role role;
    private final Employee employee;
    private final Timestamp joinedAt;
}

package com.careerfair.q.service.queue.virtual.response;

import lombok.Data;

@Data
public class RealTimeStatusResponse {
    private final int position;
    private final int waitTime;
}

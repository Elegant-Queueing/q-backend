package com.careerfair.q.service.queue.virtual.implementation;

import com.careerfair.q.enums.Role;
import com.careerfair.q.service.queue.virtual.VirtualQueueService;
import org.springframework.stereotype.Service;

@Service
public class VirtualQueueServiceImpl implements VirtualQueueService {

    @Override
    public int getWaitTime(String fairId, Role role) {
        // TODO
        return 0;
    }
}

package com.careerfair.q.service.queue.implementation;

import com.careerfair.q.enums.Role;
import com.careerfair.q.service.queue.QueueService;
import com.careerfair.q.service.queue.response.PhysicalQueueDataResponse;
import com.careerfair.q.service.queue.response.RealTimeStatusResponse;

import java.util.Map;

public class QueueServiceImpl implements QueueService {

    @Override
    public int getWaitTime(String companyId, Role role) {
        return 0;
    }

    @Override
    public Map<String, Integer> getWaitTime(Role role) {
        return null;
    }

    @Override
    public RealTimeStatusResponse getRealTimeStatus(String studentId) {
        return null;
    }

    @Override
    public RealTimeStatusResponse joinVirtualQueue(String companyId, String studentId, Role role) {
        return null;
    }

    @Override
    public RealTimeStatusResponse joinPhysicalQueue(String employeeId, String studentId) {
        return null;
    }

    @Override
    public PhysicalQueueDataResponse addQueue(String companyId, String employeeId, Role role) {
        return null;
    }

    @Override
    public PhysicalQueueDataResponse getPhysicalQueueData(String employeeId, Role role) {
        return null;
    }

    @Override
    public PhysicalQueueDataResponse pauseQueue(String companyId, String employeeId) {
        return null;
    }

    @Override
    public PhysicalQueueDataResponse removeFromQueue(String employeeId, String studentId) {
        return null;
    }
}

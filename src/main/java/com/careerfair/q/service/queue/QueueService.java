package com.careerfair.q.service.queue;

import com.careerfair.q.enums.Role;
import com.careerfair.q.service.queue.response.PhysicalQueueDataResponse;
import com.careerfair.q.service.queue.response.RealTimeStatusResponse;

import java.util.Map;

public interface QueueService {

    int getWaitTime(String companyId, Role role);

    Map<String, Integer> getWaitTime(Role role);

    RealTimeStatusResponse getRealTimeStatus(String studentId);

    RealTimeStatusResponse joinVirtualQueue(String companyId, String studentId, Role role);

    RealTimeStatusResponse joinPhysicalQueue(String employeeId, String studentId);

    PhysicalQueueDataResponse addQueue(String companyId, String employeeId, Role role);

    PhysicalQueueDataResponse getPhysicalQueueData(String employeeId, Role role);

    PhysicalQueueDataResponse pauseQueue(String companyId, String employeeId);

    PhysicalQueueDataResponse removeFromQueue(String employeeId, String studentId);

}

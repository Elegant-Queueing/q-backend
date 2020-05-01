package com.careerfair.q.service.queue.virtual;

import com.careerfair.q.enums.Role;
import com.careerfair.q.service.queue.virtual.response.RealTimeStatusResponse;

public interface VirtualQueueService {

    /**
     *
     * @param fairId
     * @param role
     * @return int wait time for the given role in the given fair (in minutes)
     */
    int getWaitTime(String fairId, Role role);

//    RealTimeStatusResponse getRealTimeStatus(String studentId, String fairId, String companyId, Role role);



}

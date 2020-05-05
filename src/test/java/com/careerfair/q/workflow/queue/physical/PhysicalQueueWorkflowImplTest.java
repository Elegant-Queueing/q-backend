package com.careerfair.q.workflow.queue.physical;

import com.careerfair.q.model.redis.Student;
import com.careerfair.q.service.database.StudentFirebase;
import com.careerfair.q.util.enums.Role;
import com.careerfair.q.workflow.queue.physical.implementation.PhysicalQueueWorkflowImpl;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.data.redis.core.RedisTemplate;

public class PhysicalQueueWorkflowImplTest {

    @InjectMocks
    private RedisTemplate<String, Role> companyRedisTemplate;
    @Mock private RedisTemplate<String, String> employeeRedisTemplate;
    @Mock private RedisTemplate<String, Student> queueRedisTemplate;
    @Mock private RedisTemplate<String, String> studentRedisTemplate;
    @Mock private StudentFirebase studentFirebase;

    private PhysicalQueueWorkflowImpl physicalQueueWorkflow;

//    @Before
//    public void setupMock() {
//        physicalQueueWorkflow
//    }

    @Test
    public void testJoinQueue() {

    }
}

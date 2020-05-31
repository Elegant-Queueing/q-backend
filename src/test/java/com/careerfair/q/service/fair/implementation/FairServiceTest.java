package com.careerfair.q.service.fair.implementation;

import com.careerfair.q.model.db.Company;
import com.careerfair.q.model.db.Fair;
import com.careerfair.q.service.database.FirebaseService;
import com.careerfair.q.service.fair.response.GetAllFairsResponse;
import com.careerfair.q.service.fair.response.GetCompanyResponse;
import com.careerfair.q.service.fair.response.GetFairResponse;
import com.careerfair.q.service.fair.response.GetWaitTimeResponse;
import com.careerfair.q.service.queue.QueueService;
import com.careerfair.q.util.enums.Role;
import com.google.cloud.Timestamp;
import com.google.common.collect.Maps;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.*;

import static org.junit.Assert.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doReturn;

class FairServiceTest {

    @Mock
    private FirebaseService firebaseService;

    @Mock
    private QueueService queueService;

    @InjectMocks
    private final FairServiceImpl fairService = new FairServiceImpl();

    private Fair fair;
    private Fair fairTwo;
    private Company company;
    private int overallWaitTime;

    @BeforeEach
    public void setupMocks() {
        MockitoAnnotations.initMocks(this);
        fair = createDummyFair("f1", "n1", "u1", "d1", Collections.singletonList("c1"),
                Timestamp.ofTimeSecondsAndNanos(1192506815, 0),
                Timestamp.ofTimeSecondsAndNanos(1192508815, 0));
        fairTwo = createDummyFair("f2", "n2", "u1", "d2", Collections.singletonList("c2"),
                Timestamp.ofTimeSecondsAndNanos(1193506815, 0),
                Timestamp.ofTimeSecondsAndNanos(1193508815, 0));
        company = createDummyCompany();
        overallWaitTime = 1000;
    }

    @Test
    void testGetAllFairs() {
        doReturn(Arrays.asList(fair, fairTwo)).when(firebaseService).getAllFairs();
        GetAllFairsResponse getAllFairsResponse = fairService.getAllFairs();
        List<Fair> fairs = getAllFairsResponse.getFairs();
        assertEquals(fairs.size(), 2);
        checkFairResponse(fairs.get(0), "f1", "n1", "u1", "d1", Collections.singletonList("c1"),
                Timestamp.ofTimeSecondsAndNanos(1192506815, 0),
                Timestamp.ofTimeSecondsAndNanos(1192508815, 0) );
        checkFairResponse(fairs.get(1), "f2", "n2", "u1", "d2", Collections.singletonList("c2"),
                Timestamp.ofTimeSecondsAndNanos(1193506815, 0),
                Timestamp.ofTimeSecondsAndNanos(1193508815, 0));
    }

    @Test
    void testGetFairWithId() {
        doReturn(fair).when(firebaseService).getFairWithId(anyString());
        GetFairResponse getFairResponse = fairService.getFairWithId("f1");
        checkFairResponse(getFairResponse.getFair(), "f1", "n1", "u1", "d1",
                Collections.singletonList("c1"), Timestamp.ofTimeSecondsAndNanos(1192506815, 0),
                Timestamp.ofTimeSecondsAndNanos(1192508815, 0));
    }

    @Test
    void testGetCompanyWithId() {
        doReturn(company).when(firebaseService).getCompanyWithId(anyString(), anyString());
        GetCompanyResponse getCompanyResponse = fairService.getCompanyWithId("f1", "c1");
        checkValidCompany(getCompanyResponse.getCompany(), "c1",
                Collections.singletonList(Role.SWE), Collections.singletonList("e1"), "b1",
                "www.c1.com");
    }

    @Test
    void testGetCompanyWaitTime() {
        doReturn(overallWaitTime).when(queueService).getOverallWaitTime(anyString(), any());
        GetWaitTimeResponse getWaitTimeResponse = fairService.getCompanyWaitTime("c1", Role.SWE);
        assertNotNull(getWaitTimeResponse);
        Map<String, Integer> expected = Maps.newHashMap();
        expected.put("c1", overallWaitTime);
        assertTrue(Maps.difference(getWaitTimeResponse.getCompanyWaitTimes(), expected).areEqual());
    }

    @Test
    void testGetAllCompaniesWaitTime() {
    }

    private Fair createDummyFair(String fairId, String name, String universityId,
            String description, List<String> companies, Timestamp startTime, Timestamp endTime) {
        Fair fair = new Fair();
        fair.setFairId(fairId);
        fair.setName(name);
        fair.setUniversityId(universityId);
        fair.setDescription(description);
        fair.setCompanies(companies);
        fair.setStartTime(startTime);
        fair.setEndTime(endTime);
        return fair;
    }

    private void checkFairResponse(Fair fair, String fairId, String name,
            String universityId, String description, List<String> companies, Timestamp startTime,
            Timestamp endTime) {
        assertNotNull(fair);
        assertEquals(fair.getFairId(), fairId);
        assertEquals(fair.getName(), name);
        assertEquals(fair.getUniversityId(), universityId);
        assertEquals(fair.getDescription(), description);
        assertEquals(fair.getCompanies(), companies);
        assertEquals(fair.getStartTime(), startTime);
        assertEquals(fair.getEndTime(), endTime);
    }

    private Company createDummyCompany() {
        Company company = new Company();
        company.setName("c1");
        company.setRoles(Collections.singletonList(Role.SWE));
        company.setEmployees(Collections.singletonList("e1"));
        company.setBio("b1");
        company.setWebsite("www.c1.com");
        return company;
    }

    private void checkValidCompany(Company company, String name, List<Role> roles,
                                   List<String> employees, String bio, String website) {
        assertNotNull(company);
        assertEquals(company.name, name);
        assertEquals(company.roles, roles);
        assertEquals(company.employees, employees);
        assertEquals(company.bio, bio);
        assertEquals(company.website, website);
    }
}
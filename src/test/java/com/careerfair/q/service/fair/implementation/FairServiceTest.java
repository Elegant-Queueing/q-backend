package com.careerfair.q.service.fair.implementation;

import com.careerfair.q.service.database.FirebaseService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.junit.jupiter.api.Assertions.*;

class FairServiceTest {

    @Mock
    private FirebaseService firebaseService;

    @InjectMocks
    private final FairServiceImpl fairService = new FairServiceImpl();


    @BeforeEach
    public void setupMocks() {
        MockitoAnnotations.initMocks(this);
    }
    @Test
    void getAllFairs() {
    }

    @Test
    void getFairWithId() {
    }

    @Test
    void getCompanyWithId() {
    }

    @Test
    void getCompanyWaitTime() {
    }

    @Test
    void getAllCompaniesWaitTime() {
    }
}
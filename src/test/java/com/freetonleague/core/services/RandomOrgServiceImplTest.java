package com.freetonleague.core.services;

import com.freetonleague.core.common.IntegrationTest;
import com.freetonleague.core.service.implementations.RandomOrgServiceImpl;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

public class RandomOrgServiceImplTest extends IntegrationTest {

    @Autowired
    private RandomOrgServiceImpl randomOrgService;

    @Test
    public void get() {
        randomOrgService.getRandomLong(10L);
    }

}

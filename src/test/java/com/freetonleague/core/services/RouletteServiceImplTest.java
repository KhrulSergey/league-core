package com.freetonleague.core.services;

import com.freetonleague.core.common.IntegrationTest;
import com.freetonleague.core.domain.entity.RouletteMatchEntity;
import com.freetonleague.core.service.implementations.RouletteServiceImpl;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

public class RouletteServiceImplTest extends IntegrationTest {

    @Autowired
    private RouletteServiceImpl rouletteService;

    @Test
    public void rouletteShouldNotReadyToStartWithoutPlayers() {

        RouletteMatchEntity match = RouletteMatchEntity.builder()
                .bets(List.of())
                .build();

        Assertions.assertFalse(rouletteService.isReadyToStart(match));
    }

    @Test
    public void rouletteStatsShouldReturned() {
        Assertions.assertNotNull(rouletteService.getStats());
    }

}

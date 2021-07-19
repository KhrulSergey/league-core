package com.freetonleague.core.service.implementations;

import com.freetonleague.core.domain.dto.RandomLongDto;
import com.freetonleague.core.service.RandomService;
import org.apache.commons.lang3.RandomUtils;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class RandomServiceImpl implements RandomService {

    @Override
    public RandomLongDto getRandomLong(Long max) {
        return RandomLongDto.builder()
                .randomizeId(UUID.randomUUID().toString())
                .value(RandomUtils.nextLong(0, max + 1))
                .build();
    }

}

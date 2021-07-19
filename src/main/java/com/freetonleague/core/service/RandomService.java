package com.freetonleague.core.service;

import com.freetonleague.core.domain.dto.RandomLongDto;

public interface RandomService {

    RandomLongDto getRandomLong(Long max);

}

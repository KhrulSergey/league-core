package com.freetonleague.core.service.implementations;

import com.freetonleague.core.domain.dto.GameIndicatorTypeDto;
import com.freetonleague.core.domain.enums.GameIndicatorType;
import com.freetonleague.core.mapper.NsiMapper;
import com.freetonleague.core.service.RestNsiFacade;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Service-facade for getting nsi list
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class RestNsiFacadeImpl implements RestNsiFacade {

    private final NsiMapper mapper;

    /**
     * Returns all entries of Game Indicator Type
     */
    @Override
    public List<GameIndicatorTypeDto> getGameIndicatorList() {
        log.debug("^ trying to get GameIndicatorType entries of size: {}", GameIndicatorType.values().length);
        return mapper.toDto(GameIndicatorType.values());
    }

}

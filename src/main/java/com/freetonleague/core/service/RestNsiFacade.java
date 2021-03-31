package com.freetonleague.core.service;

import com.freetonleague.core.domain.dto.GameIndicatorTypeDto;

import java.util.List;

/**
 * Service-facade for getting nsi list
 */
public interface RestNsiFacade {


    /**
     * Returns all entries of Game Indicator Type
     *
     * @return team entity
     */
    List<GameIndicatorTypeDto> getGameIndicatorList();

}

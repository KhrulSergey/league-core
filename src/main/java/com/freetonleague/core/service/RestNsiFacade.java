package com.freetonleague.core.service;

import com.freetonleague.core.domain.dto.GameIndicatorTypeDto;
import com.freetonleague.core.domain.dto.MatchPropertyTypeDto;

import java.util.List;

/**
 * Service-facade for getting nsi list
 */
public interface RestNsiFacade {


    /**
     * Returns all entries of Game Indicator Type
     *
     * @return game indicator entity list
     */
    List<GameIndicatorTypeDto> getGameIndicatorList();

    /**
     * Returns all entries of Match Property Type
     *
     * @return match property list
     */
    List<MatchPropertyTypeDto> getMatchPropertyList();

}

package com.freetonleague.core.service;

import com.freetonleague.core.domain.dto.tournament.TournamentMatchPropertyTypeDto;
import com.freetonleague.core.domain.dto.tournament.GameIndicatorTypeDto;
import com.freetonleague.core.domain.dto.product.ProductPropertyTypeDto;

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
    List<TournamentMatchPropertyTypeDto> getMatchPropertyList();

    /**
     * Returns all entries of Product Property Type
     *
     * @return product property list
     */
    List<ProductPropertyTypeDto> getProductPropertyList();

}

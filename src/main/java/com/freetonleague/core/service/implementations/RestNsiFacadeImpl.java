package com.freetonleague.core.service.implementations;

import com.freetonleague.core.domain.dto.product.ProductPropertyTypeDto;
import com.freetonleague.core.domain.dto.tournament.GameIndicatorTypeDto;
import com.freetonleague.core.domain.dto.tournament.TournamentMatchPropertyTypeDto;
import com.freetonleague.core.domain.enums.MatchPropertyType;
import com.freetonleague.core.domain.enums.product.ProductPropertyType;
import com.freetonleague.core.domain.enums.tournament.GameIndicatorType;
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
        log.debug("^ trying to get GameIndicatorType entries of size: '{}'", GameIndicatorType.values().length);
        return mapper.toDto(GameIndicatorType.values());
    }

    /**
     * Returns all entries of Match Property Type
     */
    @Override
    public List<TournamentMatchPropertyTypeDto> getMatchPropertyList() {
        log.debug("^ trying to get MatchPropertyType entries of size: '{}'", MatchPropertyType.values().length);
        return mapper.toDto(MatchPropertyType.values());
    }

    /**
     * Returns all entries of Product Property Type
     */
    @Override
    public List<ProductPropertyTypeDto> getProductPropertyList() {
        log.debug("^ trying to get ProductPropertyType entries of size: '{}'", ProductPropertyType.values().length);
        return mapper.toDto(ProductPropertyType.values());
    }

}

package com.freetonleague.core.domain.dto.tournament;

import com.freetonleague.core.domain.enums.IndicatorValueClassType;
import lombok.Data;

/**
 * Dto for match property (enum) entry
 */
@Data
public class TournamentMatchPropertyTypeDto {

    private String name;

    private String description;

    private IndicatorValueClassType valueClassType;
}

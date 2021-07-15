package com.freetonleague.core.domain.dto.tournament;

import com.freetonleague.core.domain.enums.IndicatorValueClassType;
import lombok.Data;

/**
 * Dto for game indicators (enum) entry
 */
@Data
public class GameIndicatorTypeDto {

    private String name;

    private String description;

    private IndicatorValueClassType valueClassType;

    private Long gameDisciplineId;
}

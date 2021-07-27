package com.freetonleague.core.domain.dto.tournament;

import com.freetonleague.core.domain.enums.tournament.GameIndicatorType;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@NoArgsConstructor
public class GameDisciplineIndicatorDto implements Serializable {

    private static final long serialVersionUID = 270113932224274376L;

    private GameIndicatorType gameIndicatorType;

    private Object gameIndicatorValue;

    private Double multipliedValue;

}

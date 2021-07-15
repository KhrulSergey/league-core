package com.freetonleague.core.domain.dto;

import com.freetonleague.core.domain.enums.TournamentWinnerPlaceType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.validation.constraints.NotNull;
import java.io.Serializable;

/**
 * Model for score distribution for rivals in series/matches
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class TournamentScoreWinnersDistributionDto implements Serializable {
    private static final long serialVersionUID = 9140253901535729041L;

    @NotNull
    @Enumerated(EnumType.ORDINAL)
    private TournamentWinnerPlaceType place;

    private Double scoreValue;
}

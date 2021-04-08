package com.freetonleague.core.domain.dto;

import com.freetonleague.core.domain.enums.TournamentWinnerPlaceType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import java.io.Serializable;

/**
 * Model for distribution prize fund between winner
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class TournamentPrizePoolDistributionDto implements Serializable {
    private static final long serialVersionUID = -7062409096028979416L;

    @NotNull
    @Enumerated(EnumType.ORDINAL)
    private TournamentWinnerPlaceType place;

    @Min(0)
    @Max(100)
    private Double proportion;
}

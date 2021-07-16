package com.freetonleague.core.domain.dto.tournament;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import java.io.Serializable;

/**
 * Money penalty entry for quit tournament after approved participation
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class TournamentQuitPenaltyDistributionDto implements Serializable {
    private static final long serialVersionUID = -4409574506197851333L;

    @NotNull
    @Min(0)
    private Double hoursBeforeTournamentStart;

    @NotNull
    @Max(100)
    @Min(0)
    private Double penaltyValue;
}

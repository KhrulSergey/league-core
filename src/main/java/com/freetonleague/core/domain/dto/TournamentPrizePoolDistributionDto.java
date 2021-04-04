package com.freetonleague.core.domain.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

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
    private static final long serialVersionUID = -6443155301809569583L;

    @NotNull
    @Min(1)
    private Integer place;

    @Min(0)
    @Max(100)
    private Double proportion;
}

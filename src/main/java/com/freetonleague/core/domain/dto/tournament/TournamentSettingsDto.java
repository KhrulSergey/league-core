package com.freetonleague.core.domain.dto.tournament;

import com.freetonleague.core.domain.enums.tournament.FundGatheringType;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.List;
import java.util.Map;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class TournamentSettingsDto {

    private Long id;

    private Long tournamentId;

    @NotNull
    @Min(0)
    @Max(100)
    private Double organizerCommission;

    @NotNull
    @Min(0)
    private Double participationFee;

    @NotNull
    @Min(0)
    private Integer minTeamCount;

    @Min(0)
    private Integer maxTeamCount;

    @NotNull
    @Min(0)
    private Integer maxTeamMainParticipantCount;

    @Min(0)
    private Integer maxTeamReserveParticipantCount;

    @NotNull
    @Min(1)
    private Integer matchCountPerSeries;

    private Boolean selfHosted;

    @ApiModelProperty(notes = "Enable generating series without waiting rounds. Only for SingleElimination (default - false)")
    @Builder.Default
    private Boolean isSequentialSeriesEnabled = false;

    private Map<Integer, TournamentRoundSettingDto> tournamentRoundSettingsList;

    @Min(0)
    private Double prizeFund;

    @ApiModelProperty(notes = "Score distribution for rivals in series/matches")
    private List<TournamentScoreWinnersDistributionDto> scoreDistributionWithinRivals;

    @NotNull
    @NotEmpty
    private List<TournamentPrizePoolDistributionDto> prizePoolDistribution;

    @NotNull
    @NotEmpty
    private List<TournamentQuitPenaltyDistributionDto> quitPenaltyDistribution;

    @NotNull
    private FundGatheringType fundGatheringType;
}

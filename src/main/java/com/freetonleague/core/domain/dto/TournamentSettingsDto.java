package com.freetonleague.core.domain.dto;

import com.freetonleague.core.domain.enums.FundGatheringType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.List;
import java.util.Map;

@Builder
@Data
@AllArgsConstructor
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

    private Map<Integer, TournamentRoundSettingDto> tournamentRoundSettingsList;

    @Min(0)
    private Double prizeFund;

    @NotNull
    @NotEmpty
    private List<TournamentPrizePoolDistributionDto> prizePoolDistribution;

    @NotNull
    @NotEmpty
    private List<TournamentQuitPenaltyDistributionDto> quitPenaltyDistribution;

    @NotNull
    private FundGatheringType fundGatheringType;
}

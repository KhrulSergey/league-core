package com.freetonleague.core.domain.model.tournament;

import com.freetonleague.core.domain.dto.tournament.TournamentPrizePoolDistributionDto;
import com.freetonleague.core.domain.dto.tournament.TournamentQuitPenaltyDistributionDto;
import com.freetonleague.core.domain.dto.tournament.TournamentRoundSettingDto;
import com.freetonleague.core.domain.dto.tournament.TournamentScoreWinnersDistributionDto;
import com.freetonleague.core.domain.enums.tournament.FundGatheringType;
import com.freetonleague.core.domain.model.ExtendedBaseEntity;
import com.vladmihalcea.hibernate.type.json.JsonBinaryType;
import lombok.*;
import lombok.experimental.SuperBuilder;
import org.hibernate.annotations.Type;
import org.hibernate.annotations.TypeDef;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.util.List;
import java.util.Map;


@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@SuperBuilder
@ToString(callSuper = true)
@Getter
@Setter
@TypeDef(name = "jsonb", typeClass = JsonBinaryType.class)
@Entity
@Table(schema = "public", name = "tournament_settings")
@SequenceGenerator(name = "base_entity_seq", sequenceName = "tournament_settings_id_seq", schema = "public", allocationSize = 1)
public class TournamentSettings extends ExtendedBaseEntity {

    @ToString.Exclude
    @OneToOne(fetch = FetchType.EAGER, cascade = CascadeType.REFRESH)
    @JoinColumn(name = "tournament_id")
    private Tournament tournament;

    @NotNull
    @Column(name = "organizer_commission")
    private Double organizerCommission;

    @NotNull
    @Column(name = "participation_fee")
    private Double participationFee;

    @NotNull
    @Builder.Default
    @Column(name = "min_team_count")
    private Integer minTeamCount = 2;

    @Column(name = "max_team_count")
    private Integer maxTeamCount;

    @NotNull
    @Column(name = "max_main_participant_count")
    private Integer maxTeamMainParticipantCount;

    @Column(name = "max_reserve_participant_count")
    private Integer maxTeamReserveParticipantCount;

    @Builder.Default
    @NotNull
    @Column(name = "match_count_per_series")
    private Integer matchCountPerSeries = 3;

    @Type(type = "jsonb")
    @Column(name = "tournament_round_settings_list", columnDefinition = "jsonb")
    private Map<Integer, TournamentRoundSettingDto> tournamentRoundSettingsList;

    /**
     * Goal prize fund value. (info purpose only)
     */
    @Column(name = "prize_fund")
    private Double prizeFund;

    /**
     * Settings that allow tournament participants (rivals) to manage winners of the series and matches
     */
    @Builder.Default
    @Column(name = "self_hosted")
    private Boolean selfHosted = false;

    @Builder.Default
    @Column(name = "is_auto_finish_round_enabled")
    private Boolean isAutoFinishRoundEnabled = true;

    @Builder.Default
    @Column(name = "is_generation_round_enabled")
    private Boolean isGenerationRoundEnabled = true;

    @Builder.Default
    @Column(name = "is_sequential_series_enabled")
    private Boolean isSequentialSeriesEnabled = false;

    @Builder.Default
    @Column(name = "is_auto_finish_series_enabled")
    private Boolean isAutoFinishSeriesEnabled = true;

    @Builder.Default
    @Column(name = "is_generation_series_enabled")
    private Boolean isGenerationSeriesEnabled = true;

    @Type(type = "jsonb")
    @Column(name = "score_distribution_within_rivals", columnDefinition = "jsonb")
    private List<TournamentScoreWinnersDistributionDto> scoreDistributionWithinRivals;

    /**
     * Schema of distribution prize fund between winners
     */
    @Type(type = "jsonb")
    @Column(name = "prize_distribution", columnDefinition = "jsonb")
    private List<TournamentPrizePoolDistributionDto> prizePoolDistribution;

    /**
     * Schema of money penalties for quit tournament after approved participation
     */
    @Type(type = "jsonb")
    @Column(name = "quit_penalty_distribution", columnDefinition = "jsonb")
    private List<TournamentQuitPenaltyDistributionDto> quitPenaltyDistribution;

    @NotNull
    @Column(name = "fund_gathering_type")
    @Enumerated(EnumType.STRING)
    private FundGatheringType fundGatheringType;
}

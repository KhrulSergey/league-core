package com.freetonleague.core.domain.model;

import com.freetonleague.core.domain.dto.TournamentPrizePoolDistributionDto;
import com.freetonleague.core.domain.dto.TournamentQuitPenaltyDistributionDto;
import com.freetonleague.core.domain.enums.FundGatheringType;
import com.vladmihalcea.hibernate.type.json.JsonBinaryType;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;
import org.hibernate.annotations.Type;
import org.hibernate.annotations.TypeDef;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.util.List;


@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@SuperBuilder
@Getter
@Setter
@TypeDef(name = "jsonb", typeClass = JsonBinaryType.class)
@Entity
@Table(schema = "public", name = "tournament_settings")
@SequenceGenerator(name = "base_entity_seq", sequenceName = "tournament_settings_id_seq", schema = "public", allocationSize = 1)
public class TournamentSettings extends ExtendedBaseEntity {

    @OneToOne(fetch = FetchType.EAGER, cascade = CascadeType.REFRESH)
    @JoinColumn(name = "tournament_id")
    private Tournament tournament;

    @NotNull
    @Column(name = "organizer_commission")
    private Double organizerCommission;

    @NotNull
    @Column(name = "min_team_count")
    private Integer minTeamCount;

    @Column(name = "max_team_count")
    private Integer maxTeamCount;

    @NotNull
    @Column(name = "max_main_participant_count")
    private Integer maxTeamMainParticipantCount;

    @Column(name = "max_reserve_participant_count")
    private Integer maxTeamReserveParticipantCount;

    /**
     * Goal prize fund value. (info purpose only)
     */
    @Column(name = "prize_fund")
    private Double prizeFund;

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

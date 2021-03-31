package com.freetonleague.core.domain.model;

import com.freetonleague.core.domain.dto.FundGatheringType;
import com.freetonleague.core.domain.dto.TournamentPrizePoolDistributionDto;
import com.sun.istack.NotNull;
import com.vladmihalcea.hibernate.type.basic.PostgreSQLHStoreType;
import com.vladmihalcea.hibernate.type.json.JsonBinaryType;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;
import org.hibernate.annotations.Type;
import org.hibernate.annotations.TypeDef;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@SuperBuilder
@Getter
@Setter
@TypeDef(name = "hstore", typeClass = PostgreSQLHStoreType.class)
@TypeDef(name = "jsonb", typeClass = JsonBinaryType.class)
@Entity
@Table(schema = "public", name = "tournament_settings")
@SequenceGenerator(name = "base_entity_seq", sequenceName = "tournament_settings_id_seq", schema = "public", allocationSize = 1)
public class TournamentSettings extends BaseEntity {

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

    @Column(name = "prize_fund")
    private Double prizeFund;

    @Type(type = "hstore")
    @Column(name = "prizeDistribution", columnDefinition = "hstore")
    private Map<Integer, Double> prizePoolDistribution = new HashMap<>();

    @Type(type = "jsonb")
    @Column(name = "prizeDistribution2", columnDefinition = "jsonb")
    private List<TournamentPrizePoolDistributionDto> prizePoolDistribution2 = new ArrayList<>();

    @NotNull
    @Column(name = "fund_gathering_type")
    @Enumerated(EnumType.STRING)
    private FundGatheringType fundGatheringType;
}

package com.freetonleague.core.domain.model;

import com.freetonleague.core.domain.dto.GameDisciplineIndicatorDto;
import com.freetonleague.core.domain.enums.TournamentMatchRivalParticipantStatusType;
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

/**
 * Model of team on series of matches
 * entity to have ref to teamProposal & tournamentSeries
 * (all other fields doesn't make critical sense on 18/04/2021)
 */
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@SuperBuilder
@Getter
@Setter
@Entity
@TypeDef(name = "jsonb", typeClass = JsonBinaryType.class)
@Table(schema = "public", name = "tournament_series_rivals")
@SequenceGenerator(name = "base_entity_seq", sequenceName = "tournament_series_rivals_id_seq", schema = "public", allocationSize = 1)
public class TournamentSeriesRival extends ExtendedBaseEntity {


    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "series_id")
    private TournamentSeries tournamentSeries;
    /**
     * Reference to team on tournament
     */
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "team_proposal_id")
    private TournamentTeamProposal teamProposal;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "parent_series_id")
    private TournamentSeries parentTournamentSeries;

    @NotNull
    @Column(name = "status")
    @Enumerated(EnumType.STRING)
    private TournamentMatchRivalParticipantStatusType status;

    @Transient
    private TournamentMatchRivalParticipantStatusType prevStatus;

    /**
     * Indicators (score) of team on current series
     */
    @Type(type = "jsonb")
    @Column(name = "indicators", columnDefinition = "jsonb")
    private List<GameDisciplineIndicatorDto> seriesIndicatorList;

    /**
     * Won place in the match
     */
    @Column(name = "won_place_in_series")
    private Integer wonPlaceInSeries;

    public void setStatus(TournamentMatchRivalParticipantStatusType status) {
        prevStatus = this.status;
        this.status = status;
    }

    public boolean isStatusChanged() {
        return !this.status.equals(this.prevStatus);
    }
}


package com.freetonleague.core.domain.model;

import com.freetonleague.core.domain.dto.GameDisciplineIndicatorDto;
import com.freetonleague.core.domain.enums.TournamentStatusType;
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
import java.util.Set;

/**
 * Model of team on current match
 */
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@SuperBuilder
@Getter
@Setter
@Entity
@TypeDef(name = "jsonb", typeClass = JsonBinaryType.class)
@Table(schema = "public", name = "tournament_match_rivals")
@SequenceGenerator(name = "base_entity_seq", sequenceName = "tournament_match_rivals_id_seq", schema = "public", allocationSize = 1)
public class TournamentMatchRival extends ExtendedBaseEntity {

    /**
     * List of team participants on current match
     */
    @OneToMany(mappedBy = "tournamentMatchRival", cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    Set<TournamentMatchRivalParticipant> rivalParticipants;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "match_id")
    private TournamentMatch tournamentMatch;
    /**
     * Reference to team on tournament
     */
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "team_proposal_id")
    private TournamentTeamProposal teamProposal;

    @NotNull
    @Column(name = "status")
    @Enumerated(EnumType.STRING)
    private TournamentStatusType status;

    @Transient
    private TournamentStatusType prevStatus;

    /**
     * Indicators (score) of team on current match
     */
    @Type(type = "jsonb")
    @Column(name = "indicators", columnDefinition = "jsonb")
    private List<GameDisciplineIndicatorDto> matchIndicator;

    /**
     * Won place in the match
     */
    @Column(name = "place_in_match")
    private Integer placeInMatch;

    public void setStatus(TournamentStatusType status) {
        prevStatus = this.status;
        this.status = status;
    }

    public boolean isStatusChanged() {
        return !this.status.equals(this.prevStatus);
    }
}


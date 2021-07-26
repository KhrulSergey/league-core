package com.freetonleague.core.domain.model.tournament;

import com.freetonleague.core.domain.dto.tournament.GameDisciplineIndicatorDto;
import com.freetonleague.core.domain.enums.tournament.TournamentMatchRivalParticipantStatusType;
import com.freetonleague.core.domain.enums.tournament.TournamentWinnerPlaceType;
import com.freetonleague.core.domain.model.ExtendedBaseEntity;
import com.vladmihalcea.hibernate.type.json.JsonBinaryType;
import lombok.*;
import lombok.experimental.SuperBuilder;
import org.hibernate.annotations.Type;
import org.hibernate.annotations.TypeDef;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Model of team on current match
 */
@EqualsAndHashCode(callSuper = true, of = {"status", "wonPlaceInMatch"})
@ToString(callSuper = true, of = {"status", "wonPlaceInMatch", "matchIndicator"})
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
    Set<TournamentMatchRivalParticipant> rivalParticipantList;

    @ManyToOne
    @JoinColumn(name = "match_id")
    private TournamentMatch tournamentMatch;
    /**
     * Reference to team on tournament
     */

    @ManyToOne
    @JoinColumn(name = "team_proposal_id")
    private TournamentTeamProposal teamProposal;

    @NotNull
    @Column(name = "status")
    @Enumerated(EnumType.STRING)
    private TournamentMatchRivalParticipantStatusType status;

    @Transient
    private TournamentMatchRivalParticipantStatusType prevStatus;

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
    @Enumerated(EnumType.ORDINAL)
    private TournamentWinnerPlaceType wonPlaceInMatch;

    public void setStatus(TournamentMatchRivalParticipantStatusType status) {
        prevStatus = this.status;
        this.status = status;
    }

    public boolean isStatusChanged() {
        return !this.status.equals(this.prevStatus);
    }

    public void setRivalParticipantsFromTournamentTeamParticipant(List<TournamentTeamParticipant> tournamentTeamParticipants) {
        rivalParticipantList = tournamentTeamParticipants.parallelStream()
                .map(p -> new TournamentMatchRivalParticipant(this, p))
                .collect(Collectors.toSet());
    }
}


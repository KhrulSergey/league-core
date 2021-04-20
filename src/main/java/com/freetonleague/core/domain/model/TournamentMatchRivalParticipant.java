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
 * Model of team participant on current match
 */
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@SuperBuilder
@Getter
@Setter
@Entity
@TypeDef(name = "jsonb", typeClass = JsonBinaryType.class)
@Table(schema = "public", name = "tournament_match_rival_participant")
@SequenceGenerator(name = "base_entity_seq", sequenceName = "tournament_match_rival_participant_id_seq", schema = "public", allocationSize = 1)
public class TournamentMatchRivalParticipant extends ExtendedBaseEntity {

    public TournamentMatchRivalParticipant(TournamentMatchRival tournamentMatchRival,
                                           TournamentTeamParticipant tournamentTeamParticipant) {
        this.tournamentMatchRival = tournamentMatchRival;
        this.tournamentTeamParticipant = tournamentTeamParticipant;
        this.status = TournamentMatchRivalParticipantStatusType.ACTIVE;
    }

    /**
     * Reference to tournament match rival (team)
     */
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "match_rival_id")
    private TournamentMatchRival tournamentMatchRival;

    /**
     * Reference to team participant on current tournament
     */
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "tournament_team_participant_id")
    private TournamentTeamParticipant tournamentTeamParticipant;

    @NotNull
    @Column(name = "status")
    @Enumerated(EnumType.STRING)
    private TournamentMatchRivalParticipantStatusType status;

    /**
     * Indicators (score) of participant on current match
     */
    @Type(type = "jsonb")
    @Column(name = "indicators", columnDefinition = "jsonb")
    private List<GameDisciplineIndicatorDto> participantIndicator;

}


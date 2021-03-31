package com.freetonleague.core.domain.model;

import com.freetonleague.core.domain.enums.TournamentTeamParticipantStatusType;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

import javax.persistence.*;

/**
 * Team participants used in proposal to tournament
 */
@EqualsAndHashCode(callSuper = true, exclude = "tournamentTeamProposal")
@Getter
@Setter
@NoArgsConstructor
@Entity
@SuperBuilder
@Table(schema = "public", name = "tournament_team_participants")
@SequenceGenerator(name = "base_entity_seq", sequenceName = "tournament_team_participants_id_seq", allocationSize = 1, schema = "public")
public class TournamentTeamParticipant extends BaseEntity {

    //Properties
    /**
     * Reference to Team Proposal to participate in tournament
     */
    @ManyToOne
    @JoinColumn(name = "tournament_team_proposal_id", unique = true)
    private TournamentTeamProposal tournamentTeamProposal;

    /** Reference to Team Participant entity */
    @ManyToOne
    @JoinColumn(name = "team_participant_id", nullable = false)
    private TeamParticipant teamParticipant;

    /** Reference to LeagueId user */
    @ManyToOne
    @JoinColumn(name = "league_id", referencedColumnName = "league_id", nullable = false)
    private User user;

    /** Team participant status (role) in current tournament (tournamentTeamProposal->tournament) */
    @Column(name = "status")
    @Enumerated(EnumType.STRING)
    private TournamentTeamParticipantStatusType status;

}

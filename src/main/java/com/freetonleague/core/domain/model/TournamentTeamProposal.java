package com.freetonleague.core.domain.model;

import com.freetonleague.core.domain.enums.TournamentTeamParticipantStatusType;
import com.freetonleague.core.domain.enums.TournamentTeamStateType;
import com.freetonleague.core.domain.enums.TournamentTeamType;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;


/**
 * Team proposal to participate in tournament
 */
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@SuperBuilder
@Getter
@Setter
@Entity
@Table(schema = "public", name = "tournament_team_proposal")
@SequenceGenerator(name = "base_entity_seq", sequenceName = "tournament_team_proposal_id_seq", schema = "public", allocationSize = 1)
public class TournamentTeamProposal extends BaseEntity {

    //Properties
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "team_id")
    private Team team;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "tournament_id")
    private Tournament tournament;

    /**
     * State of team participation in tournament
     */
    @NotNull
    @Column(name = "status")
    @Enumerated(EnumType.STRING)
    private TournamentTeamStateType state;

    /**
     * Type of team that participate in tournament
     */
    @NotNull
    @Column(name = "type")
    @Enumerated(EnumType.STRING)
    private TournamentTeamType type;

    /**
     * Team participant list with their role (status) in tournament
     */
    @OneToMany(mappedBy = "tournamentTeamProposal", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<TournamentTeamParticipant> tournamentTeamParticipantList;

    public Set<TournamentTeamParticipant> getMainTournamentTeamParticipantList() {
        return tournamentTeamParticipantList.parallelStream()
                .filter(p -> p.getStatus() == TournamentTeamParticipantStatusType.MAIN)
                .collect(Collectors.toSet());
    }
}

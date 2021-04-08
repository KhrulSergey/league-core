package com.freetonleague.core.domain.model;

import com.freetonleague.core.domain.enums.TournamentWinnerPlaceType;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

import javax.persistence.*;
import javax.validation.constraints.NotNull;

@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@SuperBuilder
@Getter
@Setter
@Entity
@Table(schema = "public", name = "tournament_winners")
@SequenceGenerator(name = "base_entity_seq", sequenceName = "tournament_winners_id_seq", schema = "public", allocationSize = 1)
public class TournamentWinner extends ExtendedBaseEntity {

    //Properties
    /**
     * Reference to tournament
     */
    @ManyToOne
    @JoinColumn(name = "tournament_id")
    private Tournament tournament;

    /**
     * Reference to team proposal (team) on current tournament
     */
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "team_proposal_id")
    private TournamentTeamProposal teamProposal;

    @NotNull
    @Enumerated(EnumType.ORDINAL)
    @Column(name = "winner_place")
    private TournamentWinnerPlaceType winnerPlaceType;
}

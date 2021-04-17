package com.freetonleague.core.service;


import com.freetonleague.core.domain.model.Tournament;
import com.freetonleague.core.domain.model.TournamentTeamParticipant;
import com.freetonleague.core.domain.model.TournamentTeamProposal;

import java.util.List;

public interface TournamentTeamService {

    /**
     * Returns tournament team proposal (request to participate on tournament).
     *
     * @param tournamentTeamProposal data to be saved id DB
     * @return new tournament team proposal
     */
    TournamentTeamProposal addProposal(TournamentTeamProposal tournamentTeamProposal);

    /**
     * Returns founded participant by id
     *
     * @param id of team to search
     * @return team entity
     */
    TournamentTeamParticipant getTournamentTeamParticipantById(long id);

    /**
     * Quit requested team (in team proposal) from tournament.
     * TournamentTeamProposal marked as CANCELLED
     *
     * @param tournamentTeamProposal data to get info about team / tournament and delete team proposal
     */
    void quitFromTournament(TournamentTeamProposal tournamentTeamProposal);

    List<TournamentTeamProposal> getActiveTeamProposalByTournament(Tournament tournament);
}

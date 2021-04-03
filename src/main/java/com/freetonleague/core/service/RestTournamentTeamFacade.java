package com.freetonleague.core.service;

import com.freetonleague.core.domain.dto.TournamentTeamProposalDto;
import com.freetonleague.core.domain.model.User;

/**
 * Service-facade for managing tournament team proposal and team composition
 */
public interface RestTournamentTeamFacade {

    /**
     * Registry new team to tournament
     *
     * @param tournamentId    identify of tournament
     * @param teamId          identify of team
     * @param teamProposalDto Team proposal data to be added
     * @return Added team proposal
     */
    TournamentTeamProposalDto createProposalToTournament(long tournamentId, long teamId, TournamentTeamProposalDto teamProposalDto, User user);

    /**
     * Quit team from tournament
     *
     * @param tournamentId identify of tournament
     * @param teamId       identify of team
     * @param user         current user from Session
     */
    void quitFromTournament(long tournamentId, long teamId, User user);
}



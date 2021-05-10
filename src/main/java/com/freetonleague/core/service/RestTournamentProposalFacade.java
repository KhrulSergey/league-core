package com.freetonleague.core.service;

import com.freetonleague.core.domain.dto.TournamentTeamParticipantDto;
import com.freetonleague.core.domain.dto.TournamentTeamProposalBaseDto;
import com.freetonleague.core.domain.dto.TournamentTeamProposalDto;
import com.freetonleague.core.domain.enums.ParticipationStateType;
import com.freetonleague.core.domain.model.TournamentTeamParticipant;
import com.freetonleague.core.domain.model.TournamentTeamProposal;
import com.freetonleague.core.domain.model.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

/**
 * Service-facade for managing tournament team proposal and team composition
 */
public interface RestTournamentProposalFacade {


    /**
     * Get team proposal for tournament
     *
     * @param tournamentId identify of tournament
     * @param teamId       identify of team
     * @param user         current user from Session
     */
    TournamentTeamProposalDto getProposalFromTeamForTournament(long tournamentId, long teamId, User user);

    /**
     * Get team proposal list for tournament
     *
     * @param tournamentId identify of tournament
     * @param user         current user from Session
     */
    Page<TournamentTeamProposalBaseDto> getProposalListForTournament(Pageable pageable, long tournamentId, User user);

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
     * Edit team proposal to tournament (only state)
     *
     * @param tournamentId      identify of tournament
     * @param teamId            identify of team
     * @param teamProposalId    identify of team proposal
     * @param teamProposalState new status of team proposal
     * @return Modified team proposal
     */
    TournamentTeamProposalDto editProposalToTournament(Long tournamentId, Long teamId, Long teamProposalId,
                                                       ParticipationStateType teamProposalState, User user);

    /**
     * Quit team from tournament
     *
     * @param tournamentId identify of tournament
     * @param teamId       identify of team
     * @param user         current user from Session
     */
    void quitFromTournament(long tournamentId, long teamId, User user);

    /**
     * Returns tournament team proposal by id and user with privacy check
     */
    TournamentTeamProposal getVerifiedTeamProposalById(long id, User user, boolean checkUser);

    /**
     * Getting participant by TournamentTeamParticipantDto, verify team membership
     */
    TournamentTeamParticipant getVerifiedTournamentTeamParticipantByDto(
            TournamentTeamParticipantDto tournamentTeamParticipantDto, TournamentTeamProposal tournamentTeamProposal);

    /**
     * Getting participant by id, verify team membership
     */
    TournamentTeamParticipant getVerifiedTournamentTeamParticipantById(
            long tournamentTeamParticipantId, TournamentTeamProposal tournamentTeamProposal);
}



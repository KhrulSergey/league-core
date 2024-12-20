package com.freetonleague.core.service.tournament;


import com.freetonleague.core.domain.enums.ParticipationStateType;
import com.freetonleague.core.domain.model.*;
import com.freetonleague.core.domain.model.tournament.Tournament;
import com.freetonleague.core.domain.model.tournament.TournamentTeamParticipant;
import com.freetonleague.core.domain.model.tournament.TournamentTeamProposal;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Set;

public interface TournamentProposalService {

    /**
     * Returns tournament team proposal (request to participate on tournament) by id.
     *
     * @param id of tournament team proposal to search
     * @return tournament team proposal entity
     */
    TournamentTeamProposal getProposalById(long id);

    /**
     * Returns tournament team proposal (request to participate on tournament) by team and tournament.
     *
     * @param team       of proposal to search
     * @param tournament info to search proposal
     * @return tournament team proposal entity
     */
    TournamentTeamProposal getLastProposalByTeamAndTournament(Team team, Tournament tournament);

    /**
     * Returns tournament team proposal list (request to participate on tournament) by capitan of team and tournament.
     *
     * @param userCapitan capitan of team for proposal to search
     * @param tournament  to search connected proposal
     * @return tournament team proposal entity
     */
    TournamentTeamProposal getLastProposalByCapitanUserAndTournament(User userCapitan, Tournament tournament);

    /**
     * Returns list of all tournament team proposal filtered by requested params
     *
     * @param pageable   filtered params to search tournament team proposal
     * @param tournament params to search tournament team proposal
     * @return list of tournament team proposal entities
     */
    Page<TournamentTeamProposal> getProposalListForTournament(Pageable pageable, Tournament tournament, Boolean confirmed,
                                                              List<ParticipationStateType> stateList);

    /**
     * Returns tournament team proposal (request to participate on tournament).
     *
     * @param tournamentTeamProposal data to be saved id DB
     * @return new tournament team proposal
     */
    TournamentTeamProposal addProposal(TournamentTeamProposal tournamentTeamProposal);

    /**
     * Edit tournament team proposal in DB.
     *
     * @param tournamentTeamProposal to be edited
     * @return Edited tournament team proposal
     */
    TournamentTeamProposal editProposal(TournamentTeamProposal tournamentTeamProposal);

    /**
     * Quit requested team (in team proposal) from tournament.
     * TournamentTeamProposal should be marked as CANCELLED or REJECTED
     *
     * @param tournamentTeamProposal modified team proposal to tournament
     */
    TournamentTeamProposal cancelProposal(TournamentTeamProposal tournamentTeamProposal);

    /**
     * Returns list of approved team proposal list for specified tournament.
     */
    List<TournamentTeamProposal> getApprovedTeamProposalListByTournament(Tournament tournament);

    /**
     * Returns list of active team proposal list for specified tournament.
     */
    List<TournamentTeamProposal> getActiveTeamProposalListByTournament(Tournament tournament);

    /**
     * Returns founded participant by id
     *
     * @param id of team to search
     * @return team entity
     */
    TournamentTeamParticipant getTournamentTeamParticipantById(long id);

    /**
     * Returns list of discordId from user that specified in tournamentTeamProposal
     */
    Set<String> getUserDiscordIdListFromTeamProposal(TournamentTeamProposal tournamentTeamProposal);

    /**
     * Returns calculated participation fee for specified teamProposal
     */
    double calculateTeamParticipationFee(TournamentTeamProposal teamProposal);
}

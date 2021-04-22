package com.freetonleague.core.service;


import com.freetonleague.core.domain.enums.TournamentTeamStateType;
import com.freetonleague.core.domain.model.Team;
import com.freetonleague.core.domain.model.Tournament;
import com.freetonleague.core.domain.model.TournamentTeamParticipant;
import com.freetonleague.core.domain.model.TournamentTeamProposal;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface TournamentTeamService {

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
    TournamentTeamProposal getProposalByTeamAndTournament(Team team, Tournament tournament);

    /**
     * Returns list of all tournament team proposal filtered by requested params
     *
     * @param pageable   filtered params to search tournament team proposal
     * @param tournament params to search tournament team proposal
     * @return list of team entities
     */
    Page<TournamentTeamProposal> getProposalListForTournament(Pageable pageable, Tournament tournament);

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
     * TournamentTeamProposal marked as CANCELLED
     *
     * @param tournamentTeamProposal data to get info about team / tournament and delete team proposal
     */
    TournamentTeamProposal quitFromTournament(TournamentTeamProposal tournamentTeamProposal);

    /**
     * Returns list of approved team proposal list for specified tournament.
     */
    List<TournamentTeamProposal> getActiveTeamProposalListByTournament(Tournament tournament);

    /**
     * Returns "started" statuses for tournaments
     */
    List<TournamentTeamStateType> getTournamentTeamProposalActiveStateList();

    /**
     * Returns founded participant by id
     *
     * @param id of team to search
     * @return team entity
     */
    TournamentTeamParticipant getTournamentTeamParticipantById(long id);
}

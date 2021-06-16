package com.freetonleague.core.service;


import com.freetonleague.core.domain.dto.TournamentMatchRivalDto;
import com.freetonleague.core.domain.dto.TournamentTeamParticipantDto;
import com.freetonleague.core.domain.model.TournamentMatchRival;
import com.freetonleague.core.domain.model.TournamentSeries;
import com.freetonleague.core.domain.model.User;

import java.util.Set;

public interface RestTournamentMatchRivalFacade {

    /**
     * Returns founded tournament match by id
     *
     * @param id of tournament match to search
     * @return tournament match entity or NULL of not found
     */
    TournamentMatchRivalDto getMatchRival(long id);

    /**
     * Add new tournament series to DB.
     *
     * @param tournamentMatchRivalDto to be added
     * @return Added tournament series
     */
    TournamentMatchRivalDto addMatchRival(TournamentMatchRivalDto tournamentMatchRivalDto);

    /**
     * Edit tournament series in DB.
     *
     * @param tournamentMatchRivalDto to be edited
     * @return Edited tournament series
     */
    TournamentMatchRivalDto editMatchRival(TournamentMatchRivalDto tournamentMatchRivalDto);


    /**
     * Change match rival participant for specified match.
     *
     * @param matchId              Identity of a match
     * @param rivalId              Identity of a rival
     * @param rivalParticipantList list of new participant for rival (team) to fight in match
     * @param user                 current user from Session
     * @return Edited tournament matches
     */
    TournamentMatchRivalDto changeActiveMatchRivalParticipants(long matchId, long rivalId, Set<TournamentTeamParticipantDto> rivalParticipantList, User user);

    /**
     * Delete tournament match rival in DB.
     *
     * @param id identify of match rival to be deleted
     */
    void deleteMatchRival(long id, User user);

    /**
     * Delete tournament match participant rival in DB.
     *
     * @param id identify of match rival participant to be deleted
     */
    void deleteMatchRivalParticipant(long id, User user);

    /**
     * Returns tournament rival by id and user with privacy check
     */
    TournamentMatchRival getVerifiedMatchRivalById(long id);

    /**
     * Returns tournament rival by dto and user with privacy check
     */
    TournamentMatchRival getVerifiedMatchRivalByDto(TournamentMatchRivalDto matchRivalDto);

    TournamentMatchRival setGameIndicatorMultipliersToMatchRival(TournamentMatchRival rival, TournamentSeries tournamentSeries);

}

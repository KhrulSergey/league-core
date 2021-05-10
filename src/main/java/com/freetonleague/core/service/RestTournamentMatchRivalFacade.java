package com.freetonleague.core.service;


import com.freetonleague.core.domain.dto.TournamentMatchRivalDto;
import com.freetonleague.core.domain.dto.TournamentTeamParticipantDto;
import com.freetonleague.core.domain.model.TournamentMatchRival;
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
     * Mark 'deleted' tournament series in DB.
     *
     * @param tournamentMatchRivalDto to be deleted
     * @return tournament series with updated fields and deleted status
     */
    TournamentMatchRivalDto deleteMatchRival(TournamentMatchRivalDto tournamentMatchRivalDto);

    /**
     * Verify tournament match rival info with validation and business check
     */
    boolean verifyTournamentMatchRival(TournamentMatchRivalDto tournamentMatchRivalDto);

    /**
     * Returns tournament rival by id and user with privacy check
     */
    TournamentMatchRival getVerifiedMatchRivalById(long id);

    /**
     * Returns tournament rival by dto and user with privacy check
     */
    TournamentMatchRival getVerifiedMatchRivalByDto(TournamentMatchRivalDto matchRivalDto);
}

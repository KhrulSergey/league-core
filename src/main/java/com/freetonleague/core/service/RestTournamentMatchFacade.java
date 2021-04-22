package com.freetonleague.core.service;


import com.freetonleague.core.domain.dto.TournamentMatchDto;
import com.freetonleague.core.domain.model.TournamentMatch;
import com.freetonleague.core.domain.model.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface RestTournamentMatchFacade {

    /**
     * Returns founded tournament match by id
     *
     * @param id   of tournament match to search
     * @param user current user from Session
     * @return tournament match entity or NULL of not found
     */
    TournamentMatchDto getMatch(long id, User user);

    /**
     * Returns list of all tournament matches filtered by requested params
     *
     * @param pageable           filtered params to search tournament matches
     * @param tournamentSeriesId specified series to search suitable tournament matches
     * @param user               current user from Session
     * @return list of tournament matches entities
     */
    Page<TournamentMatchDto> getMatchList(Pageable pageable, long tournamentSeriesId, User user);

    /**
     * Add new tournament match.
     *
     * @param tournamentMatchDto to be added
     * @param user               current user from Session
     * @return Added tournament series
     */
    TournamentMatchDto addMatch(TournamentMatchDto tournamentMatchDto, User user);

    /**
     * Edit tournament match.
     *
     * @param matchId            Identity of a match
     * @param tournamentMatchDto to be edited
     * @param user               current user from Session
     * @return Edited tournament matches
     */
    TournamentMatchDto editMatch(long matchId, TournamentMatchDto tournamentMatchDto, User user);

    /**
     * Mark 'deleted' tournament matches in DB.
     *
     * @param matchId identify to be deleted
     * @param user    current user from Session
     * @return tournament matches with updated fields and deleted status
     */
    TournamentMatchDto deleteMatch(long matchId, User user);

    /**
     * Returns tournament match by DTO, with validation, business logic and user with privacy check
     */
    TournamentMatch getVerifiedTournamentMatchByDto(TournamentMatchDto tournamentMatchDto, User user, boolean checkUser);

    /**
     * Returns tournament match by id and user with privacy check
     */
    TournamentMatch getVerifiedMatchById(long id, User user, boolean checkUser);

}

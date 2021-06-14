package com.freetonleague.core.service;


import com.freetonleague.core.domain.dto.TournamentRoundDto;
import com.freetonleague.core.domain.model.TournamentRound;
import com.freetonleague.core.domain.model.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface RestTournamentRoundFacade {

    /**
     * Returns founded tournament round by id
     *
     * @param id   of tournament round to search
     * @param user current user from Session
     * @return tournament round entity or NULL of not found
     */
    TournamentRoundDto getRound(long id, User user);

    /**
     * Returns list of all tournament round filtered by requested params
     *
     * @param pageable     filtered params to search tournament round
     * @param tournamentId specified tournament to search suitable tournament round
     * @param user         current user from Session
     * @return list of tournament round entities
     */
    Page<TournamentRoundDto> getRoundList(Pageable pageable, long tournamentId, User user);

    /**
     * Returns current active round for tournament
     *
     * @param user current user from Session
     * @return active tournament round entity or NULL of not found
     */
    TournamentRoundDto getActiveRoundForTournament(long tournamentId, User user);

    /**
     * Add new tournament round.
     *
     * @param tournamentRoundDto to be added
     * @param user               current user from Session
     * @return Added tournament round
     */
    TournamentRoundDto addRound(TournamentRoundDto tournamentRoundDto, User user);

    /**
     * Generate all round for tournament.
     *
     * @param tournamentId specified tournament to generate tournament round list
     * @param user         current user from Session
     */
    void generateRoundsForTournament(long tournamentId, User user);

    /**
     * Edit tournament round.
     *
     * @param id                 Identity of a round
     * @param tournamentRoundDto data to be edited
     * @param user               current user from Session
     * @return Edited tournament round
     */
    TournamentRoundDto editRound(long id, TournamentRoundDto tournamentRoundDto, User user);

    /**
     * Mark 'deleted' tournament round.
     *
     * @param id   identify round to be deleted
     * @param user current user from Session
     * @return tournament round with updated fields and deleted status
     */
    TournamentRoundDto deleteRound(long id, User user);

    /**
     * Returns tournament round by id and user with privacy check
     */
    TournamentRound getVerifiedRoundById(long id);

    /**
     * Getting tournament settings by DTO with privacy check
     */
    TournamentRound getVerifiedRoundByDto(TournamentRoundDto tournamentRoundDto);
}

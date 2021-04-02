package com.freetonleague.core.service;

import com.freetonleague.core.domain.dto.TournamentBaseDto;
import com.freetonleague.core.domain.dto.TournamentDto;
import com.freetonleague.core.domain.model.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

/**
 * Service-facade for managing tournaments
 */
public interface RestTournamentFacade {

    /**
     * Returns founded tournament by id
     *
     * @param id   of tournament to search
     * @param user current user from Session
     * @return tournament entity
     */
    TournamentDto getTournament(long id, User user);

    /**
     * Returns founded tournament by id
     *
     * @param id   of tournament to search
     * @param user current user from Session
     * @return tournament entity
     */
    TournamentBaseDto getBaseTournament(long id, User user);

    /**
     * Returns list of all teams filtered by requested params
     *
     * @param pageable filtered params to search tournament
     * @param user     current user from Session
     * @return list of team entities
     */
    List<TournamentDto> getTournamentList(Pageable pageable, User user);

    /**
     * Returns list of all teams filtered by requested params
     *
     * @param pageable filtered params to search tournament
     * @return list of team entities
     */
    Page<TournamentBaseDto> getBaseTournamentList(Pageable pageable, User user);

    /**
     * Add new tournament to DB.
     *
     * @param tournamentDto to be added
     * @param user          current user from Session
     * @return Added tournament
     */
    TournamentDto addTournament(TournamentDto tournamentDto, User user);

    /**
     * Edit tournament in DB.
     *
     * @param tournamentDto to be edited
     * @param user          current user from Session
     * @return Edited tournament
     */
    TournamentDto editTournament(TournamentDto tournamentDto, User user);
}

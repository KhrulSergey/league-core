package com.freetonleague.core.service.tournament;

import com.freetonleague.core.domain.dto.tournament.TournamentDiscordInfoListDto;
import com.freetonleague.core.domain.dto.tournament.TournamentDto;
import com.freetonleague.core.domain.enums.TournamentStatusType;
import com.freetonleague.core.domain.model.tournament.Tournament;
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
     * Returns list of all teams filtered by requested params with detailed info
     *
     * @param pageable        filtered params to search tournament
     * @param user            current user from Session
     * @param creatorLeagueId filter params
     * @param statusList      filter params
     * @return list of team entities
     */
    Page<TournamentDto> getTournamentDetailedList(Pageable pageable, User user, String creatorLeagueId, List<Long> disciplineIdList, List<TournamentStatusType> statusList);

    /**
     * Returns list of all tournament filtered by requested params with base info
     *
     * @param pageable         filtered params to search tournament
     * @param user             current user from Session
     * @param disciplineIdList filter params of identifiers for game discipline
     * @param statusList       filter params
     * @return list of tournament entities
     */
    Page<TournamentDto> getTournamentList(Pageable pageable, User user, List<Long> disciplineIdList, List<TournamentStatusType> statusList);

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

    /**
     * Delete tournament in DB.
     *
     * @param id   of tournament to search
     * @param user current user from Session
     * @return deleted tournament
     */
    TournamentDto deleteTournament(long id, User user);

    /**
     * Returns discord info for active tournament list with embedded data of approved tournament team participants
     */
    TournamentDiscordInfoListDto getDiscordChannelsForActiveTournament();

    /**
     * Getting tournament by id and user with privacy check
     */
    Tournament getVerifiedTournamentById(long id);
}

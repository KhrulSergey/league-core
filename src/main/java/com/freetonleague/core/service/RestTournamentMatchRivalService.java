package com.freetonleague.core.service;


import com.freetonleague.core.domain.dto.TournamentMatchRivalDto;

public interface RestTournamentMatchRivalService {

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
}

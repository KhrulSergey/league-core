package com.freetonleague.core.service;


import com.freetonleague.core.domain.model.TournamentMatchRival;

public interface TournamentMatchRivalService {

    /**
     * Returns founded tournament match by id
     *
     * @param id of tournament match to search
     * @return tournament match entity or NULL of not found
     */
    TournamentMatchRival getMatchRival(long id);

    /**
     * Add new tournament series to DB.
     *
     * @param tournamentMatchRival to be added
     * @return Added tournament series
     */
    TournamentMatchRival addMatchRival(TournamentMatchRival tournamentMatchRival);

    /**
     * Edit tournament series in DB.
     *
     * @param tournamentMatchRival to be edited
     * @return Edited tournament series
     */
    TournamentMatchRival editMatchRival(TournamentMatchRival tournamentMatchRival);

    /**
     * Mark 'deleted' tournament series in DB.
     *
     * @param tournamentMatchRival to be deleted
     * @return tournament series with updated fields and deleted status
     */
    TournamentMatchRival deleteMatchRival(TournamentMatchRival tournamentMatchRival);

    /**
     * Returns sign of tournament series existence for specified id.
     *
     * @param id for which tournament series will be find
     * @return true is tournament series exists, false - if not
     */
    boolean isExistsTournamentMatchRivalById(long id);

    /**
     * Verify tournament match rival info with validation and business check
     */
    boolean verifyTournamentMatchRival(TournamentMatchRival tournamentMatchRival);
}

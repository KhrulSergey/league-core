package com.freetonleague.core.service;


import com.freetonleague.core.domain.model.TournamentMatch;
import com.freetonleague.core.domain.model.TournamentMatchRival;
import com.freetonleague.core.domain.model.TournamentMatchRivalParticipant;

import java.util.List;

public interface TournamentMatchRivalService {

    /**
     * Returns founded tournament match rival by id
     *
     * @param id of tournament match rival to search
     * @return tournament match rival entity or NULL of not found
     */
    TournamentMatchRival getMatchRival(long id);

    /**
     * Returns founded tournament match by id
     *
     * @param match of tournament match to search
     * @return tournament match entity or NULL of not found
     */
    List<TournamentMatchRival> getMatchRivalByMatch(TournamentMatch match);

    /**
     * Add new tournament match rival to DB.
     *
     * @param tournamentMatchRival to be added
     * @return Added tournament match rival
     */
    TournamentMatchRival addMatchRival(TournamentMatchRival tournamentMatchRival);

    /**
     * Edit tournament match rival in DB.
     *
     * @param tournamentMatchRival to be edited
     * @return Edited tournament match rival
     */
    TournamentMatchRival editMatchRival(TournamentMatchRival tournamentMatchRival);

    /**
     * Mark 'deleted' tournament match rival in DB.
     *
     * @param tournamentMatchRival to be deleted
     * @return tournament match rival with updated fields and deleted status
     */
    TournamentMatchRival archiveMatchRival(TournamentMatchRival tournamentMatchRival);

    /**
     * Delete tournament match rival by id
     *
     * @param tournamentMatchRival data to delete
     * @return true if delete successfully or false - if some error occurred
     */
    boolean deleteMatchRival(TournamentMatchRival tournamentMatchRival);

    /**
     * Delete tournament match rival participant by id
     *
     * @param tournamentMatchRivalParticipant data to delete
     * @return true if delete successfully or false - if some error occurred
     */
    boolean deleteMatchRivalParticipant(TournamentMatchRivalParticipant tournamentMatchRivalParticipant);

    /**
     * Returns sign of tournament match rival existence for specified id.
     *
     * @param id for which tournament match rival will be find
     * @return true is tournament match rival exists, false - if not
     */
    boolean isExistsTournamentMatchRivalById(long id);

    /**
     * Returns sign of tournament match rival participant existence for specified id.
     *
     * @param id for which tournament match rival participant will be find
     * @return true is tournament match rival participant exists, false - if not
     */
    boolean isExistsTournamentMatchRivalParticipantById(long id);

    /**
     * Verify tournament match rival info with validation and business check
     */
    boolean verifyTournamentMatchRival(TournamentMatchRival tournamentMatchRival);

    /**
     * Returns founded tournament rival participant by id
     *
     * @param id of tournament rival participant to search
     * @return tournament match rival participant entity or NULL of not found
     */
    TournamentMatchRivalParticipant getMatchRivalParticipant(long id);
}

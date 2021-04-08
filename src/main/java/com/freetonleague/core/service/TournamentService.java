package com.freetonleague.core.service;


import com.freetonleague.core.domain.enums.TournamentStatusType;
import com.freetonleague.core.domain.model.Tournament;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface TournamentService {

    /**
     * Returns founded tournament by id
     *
     * @param id of tournament to search
     * @return tournament entity
     */
    Tournament getTournament(long id);

    /**
     * Returns list of all tournaments filtered by requested params
     *
     * @param pageable   filtered params to search tournament
     * @param statusList filtered params to search tournament
     * @return list of tournaments entities
     */
    Page<Tournament> getTournamentList(Pageable pageable, List<TournamentStatusType> statusList);

    /**
     * Returns list of all tournaments on portal
     *
     * @return list of tournaments entities
     */
    List<Tournament> getAllActiveTournament();

    /**
     * Add new tournament to DB.
     *
     * @param tournament to be added
     * @return Added tournament
     */
    Tournament addTournament(Tournament tournament);

    /**
     * Edit tournament in DB.
     *
     * @param tournament to be edited
     * @return Edited tournament
     */
    Tournament editTournament(Tournament tournament);

    /**
     * Mark 'deleted' tournament in DB.
     *
     * @param tournament to be deleted
     * @return tournament with updated fields and deleted status
     */
    Tournament deleteTournament(Tournament tournament);

    /**
     * Returns sign of tournament existence for specified id.
     *
     * @param id for which tournament will be find
     * @return true is Tournament exists, false - if not
     */
    boolean isExistsTournamentById(long id);
}

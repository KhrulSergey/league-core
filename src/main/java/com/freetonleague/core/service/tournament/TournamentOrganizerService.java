package com.freetonleague.core.service.tournament;


import com.freetonleague.core.domain.model.tournament.TournamentOrganizer;

public interface TournamentOrganizerService {
    /**
     * Returns founded tournament organizer by id
     *
     * @param id of tournament to search
     * @return tournament entity
     */
    TournamentOrganizer get(long id);

    /**
     * Adding a new tournament organizer to DB.
     *
     * @param tournamentOrganizer data to add
     * @return added Tournament Organizer
     */
    TournamentOrganizer add(TournamentOrganizer tournamentOrganizer);

    /**
     * Edit an existing tournament organizer in DB.
     *
     * @param tournamentOrganizer Updated organizer's data to be modified in database
     * @return Edited tournament organizer
     */
    TournamentOrganizer edit(TournamentOrganizer tournamentOrganizer);
}

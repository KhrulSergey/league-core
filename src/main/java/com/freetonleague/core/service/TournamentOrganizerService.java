package com.freetonleague.core.service;


import com.freetonleague.core.domain.model.TournamentOrganizer;

public interface TournamentOrganizerService {

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

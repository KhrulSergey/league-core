package com.freetonleague.core.service.tournament;


import com.freetonleague.core.domain.model.tournament.Tournament;
import com.freetonleague.core.domain.model.tournament.TournamentRound;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface TournamentRoundService {

    /**
     * Returns founded tournament round by id
     *
     * @param id of tournament round to search
     * @return tournament round entity or NULL of not found
     */
    TournamentRound getRound(long id);

    /**
     * Returns list of all tournament round filtered by requested params
     *
     * @param pageable   filtered params to search tournament round
     * @param tournament specified tournament to search suitable tournament round
     * @return list of tournament round entities
     */
    Page<TournamentRound> getRoundList(Pageable pageable, Tournament tournament);

    /**
     * Returns current active round for tournament
     *
     * @return active tournament round entity or NULL of not found
     */
    TournamentRound getActiveRoundForTournament(Tournament tournament);

    /**
     * Add new tournament round to DB.
     *
     * @param tournamentRound to be added
     * @return Added tournament round
     */
    TournamentRound addRound(TournamentRound tournamentRound);

    /**
     * Generate tournament round, series and matches list for specified tournament and save to DB.
     *
     * @param tournament to generate round for
     * @return Sign of round list was created
     */
    boolean initiateTournamentBracketsWithRounds(Tournament tournament);

    /**
     * Compose new matches and rivals for next round (fill existed prototypes of series).
     */
    boolean composeNextRoundForTournament(Tournament tournament);

    /**
     * Edit tournament round in DB.
     *
     * @param tournamentRound to be edited
     * @return Edited tournament round
     */
    TournamentRound editRound(TournamentRound tournamentRound);

    /**
     * Mark 'deleted' tournament round in DB.
     *
     * @param tournamentRound to be archived
     * @return tournament round with updated fields and deleted status
     */
    TournamentRound archiveRound(TournamentRound tournamentRound);

    /**
     * Force delete tournament round with cascade entries from DB.
     *
     * @param tournamentRound to be deleted
     */
    boolean removeRound(TournamentRound tournamentRound);

    /**
     * Returns sign of tournament round existence for specified id.
     *
     * @param id for which tournament round will be find
     * @return true is tournament round exists, false - if not
     */
    boolean isExistsTournamentRoundById(long id);

    /**
     * Returns number of last active tournament round in specified tournament
     */
    int getLastActiveRoundNumberForTournament(Tournament tournament);

    /**
     * Returns next opened round for specified tournament
     */
    TournamentRound getNextOpenRoundForTournament(Tournament tournament);

    /**
     * Returns sign of all rounds for tournament was finished.
     */
    boolean isAllRoundsFinishedByTournament(Tournament tournament);
}

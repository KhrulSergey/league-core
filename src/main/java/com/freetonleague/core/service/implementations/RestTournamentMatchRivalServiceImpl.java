package com.freetonleague.core.service.implementations;


import com.freetonleague.core.domain.dto.TournamentMatchRivalDto;
import com.freetonleague.core.mapper.TournamentMatchRivalMapper;
import com.freetonleague.core.service.RestTournamentMatchRivalService;
import com.freetonleague.core.service.TournamentMatchRivalService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.validation.Validator;

/**
 * Service-facade for managing tournament match rival and rival participant
 */
@Slf4j
@RequiredArgsConstructor
@Service
public class RestTournamentMatchRivalServiceImpl implements RestTournamentMatchRivalService {

    private final TournamentMatchRivalService tournamentMatchRivalService;
    private final TournamentMatchRivalMapper tournamentMatchRivalMapper;
    private final Validator validator;

    /**
     * Returns founded tournament match by id
     *
     * @param id of tournament match to search
     * @return tournament match entity or NULL of not found
     */
    @Override
    public TournamentMatchRivalDto getMatchRival(long id) {
        return null;
    }

    /**
     * Add new tournament series to DB.
     *
     * @param tournamentMatchRivalDto to be added
     * @return Added tournament series
     */
    @Override
    public TournamentMatchRivalDto addMatchRival(TournamentMatchRivalDto tournamentMatchRivalDto) {
        return null;
    }

    /**
     * Edit tournament series in DB.
     *
     * @param tournamentMatchRivalDto to be edited
     * @return Edited tournament series
     */
    @Override
    public TournamentMatchRivalDto editMatchRival(TournamentMatchRivalDto tournamentMatchRivalDto) {
        return null;
    }

    /**
     * Mark 'deleted' tournament series in DB.
     *
     * @param tournamentMatchRivalDto to be deleted
     * @return tournament series with updated fields and deleted status
     */
    @Override
    public TournamentMatchRivalDto deleteMatchRival(TournamentMatchRivalDto tournamentMatchRivalDto) {
        return null;
    }

    /**
     * Verify tournament match rival info with validation and business check
     *
     * @param tournamentMatchRivalDto
     */
    @Override
    public boolean verifyTournamentMatchRival(TournamentMatchRivalDto tournamentMatchRivalDto) {
        return false;
    }
}

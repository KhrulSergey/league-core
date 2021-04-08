package com.freetonleague.core.service.implementations;

import com.freetonleague.core.domain.model.TournamentMatchRival;
import com.freetonleague.core.repository.TournamentMatchRivalParticipantRepository;
import com.freetonleague.core.repository.TournamentMatchRivalRepository;
import com.freetonleague.core.service.TournamentMatchRivalService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.validation.Validator;

@Slf4j
@RequiredArgsConstructor
@Service
public class TournamentMatchRivalServiceImpl implements TournamentMatchRivalService {

    private final TournamentMatchRivalRepository tournamentMatchRivalRepository;
    private final TournamentMatchRivalParticipantRepository tournamentMatchRivalParticipantRepository;
    private final Validator validator;

    /**
     * Returns founded tournament match by id
     */
    @Override
    public TournamentMatchRival getMatchRival(long id) {
        return null;
    }

    /**
     * Add new tournament series to DB.
     */
    @Override
    public TournamentMatchRival addMatchRival(TournamentMatchRival tournamentMatchRival) {
        return null;
    }

    /**
     * Edit tournament series in DB.
     */
    @Override
    public TournamentMatchRival editMatchRival(TournamentMatchRival tournamentMatchRival) {
        return null;
    }

    /**
     * Mark 'deleted' tournament series in DB.
     */
    @Override
    public TournamentMatchRival deleteMatchRival(TournamentMatchRival tournamentMatchRival) {
        return null;
    }

    /**
     * Returns sign of tournament series existence for specified id.
     */
    @Override
    public boolean isExistsTournamentMatchRivalById(long id) {
        return false;
    }

    /**
     * Verify tournament match rival info with validation and business check
     */
    @Override
    public boolean verifyTournamentMatchRival(TournamentMatchRival tournamentMatchRival) {
        return false;
    }
}

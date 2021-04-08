package com.freetonleague.core.service.implementations;


import com.freetonleague.core.domain.dto.TournamentMatchDto;
import com.freetonleague.core.domain.dto.TournamentMatchRivalParticipantDto;
import com.freetonleague.core.domain.model.User;
import com.freetonleague.core.mapper.TournamentMatchMapper;
import com.freetonleague.core.service.RestTournamentMatchRivalService;
import com.freetonleague.core.service.RestTournamentMatchService;
import com.freetonleague.core.service.TournamentMatchService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import javax.validation.Validator;
import java.util.List;

/**
 * Service-facade for managing tournament match
 */
@Slf4j
@RequiredArgsConstructor
@Service
public class RestTournamentMatchServiceImpl implements RestTournamentMatchService {

    private final TournamentMatchService tournamentMatchService;
    private final TournamentMatchMapper tournamentMatchMapper;
    private final RestTournamentMatchRivalService restTournamentMatchRivalService;
    private final Validator validator;

    /**
     * Returns founded tournament match by id
     *
     * @param id   of tournament match to search
     * @param user current user from Session
     * @return tournament match entity or NULL of not found
     */
    @Override
    public TournamentMatchDto getMatch(long id, User user) {
        return null;
    }

    /**
     * Returns list of all tournament matches filtered by requested params
     *
     * @param pageable           filtered params to search tournament matches
     * @param tournamentSeriesId specified series to search suitable tournament matches
     * @param user               current user from Session
     * @return list of tournament matches entities
     */
    @Override
    public Page<TournamentMatchDto> getMatchList(Pageable pageable, long tournamentSeriesId, User user) {
        return null;
    }

    /**
     * Add new tournament match.
     *
     * @param tournamentMatchDto to be added
     * @param user               current user from Session
     * @return Added tournament series
     */
    @Override
    public TournamentMatchDto addMatch(TournamentMatchDto tournamentMatchDto, User user) {
        return null;
    }

    /**
     * Edit tournament match.
     *
     * @param id                 Identity of a match
     * @param tournamentMatchDto to be edited
     * @param user               current user from Session
     * @return Edited tournament matches
     */
    @Override
    public TournamentMatchDto editMatch(long id, TournamentMatchDto tournamentMatchDto, User user) {
        return null;
    }

    /**
     * Mark 'deleted' tournament matches in DB.
     *
     * @param matchId identify to be deleted
     * @param user    current user from Session
     * @return tournament matches with updated fields and deleted status
     */
    @Override
    public TournamentMatchDto deleteMatch(long matchId, User user) {
        return null;
    }

    /**
     * Change match rival participant for specified match.
     *
     * @param matchId              Identity of a match
     * @param rivalId              Identity of a rival
     * @param rivalParticipantList list of new participant for rival (team) to fight in match
     * @param user                 current user from Session
     * @return Edited tournament matches
     */
    @Override
    public TournamentMatchDto editMatchRivalParticipant(long matchId, long rivalId, List<TournamentMatchRivalParticipantDto> rivalParticipantList, User user) {
        return null;
    }

    /**
     * Verify tournament match info with validation and business check
     *
     * @param tournamentMatchDto
     * @param user
     */
    @Override
    public boolean verifyTournamentMatch(TournamentMatchDto tournamentMatchDto, User user) {
        return false;
    }
}

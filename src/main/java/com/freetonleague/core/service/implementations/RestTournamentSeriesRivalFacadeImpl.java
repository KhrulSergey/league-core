package com.freetonleague.core.service.implementations;

import com.freetonleague.core.domain.dto.TournamentSeriesRivalDto;
import com.freetonleague.core.domain.model.TournamentSeriesRival;
import com.freetonleague.core.exception.ExceptionMessages;
import com.freetonleague.core.exception.TeamManageException;
import com.freetonleague.core.exception.ValidationException;
import com.freetonleague.core.service.RestTournamentSeriesRivalFacade;
import com.freetonleague.core.service.TournamentSeriesService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import javax.validation.Validator;
import java.util.Set;

import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;

/**
 * Service-facade for managing tournament series rival
 */
@Slf4j
@RequiredArgsConstructor
@Service
public class RestTournamentSeriesRivalFacadeImpl implements RestTournamentSeriesRivalFacade {

    private final TournamentSeriesService tournamentSeriesService;
    private final Validator validator;


    /**
     * Returns tournament series rival by id with privacy check
     */
    @Override
    public TournamentSeriesRival getVerifiedSeriesRivalById(long id) {
        TournamentSeriesRival tournamentSeriesRival = tournamentSeriesService.getSeriesRival(id);
        if (isNull(tournamentSeriesRival)) {
            log.debug("^ Tournament rival with requested id {} was not found. 'getVerifiedSeriesRivalById' in " +
                    "RestTournamentSeriesRivalFacade request denied", id);
            throw new TeamManageException(ExceptionMessages.TOURNAMENT_SERIES_RIVAL_NOT_FOUND_ERROR, "Tournament series rival with requested id " + id + " was not found");
        }
        return tournamentSeriesRival;
    }

    /**
     * Returns tournament series rival by dto with privacy check
     */
    @Override
    public TournamentSeriesRival getVerifiedSeriesRivalByDto(TournamentSeriesRivalDto seriesRivalDto) {
        if (isNull(seriesRivalDto)) {
            log.warn("~ parameter 'seriesRivalDto' is NULL for getVerifiedSeiesRivalByDto");
            throw new ValidationException(ExceptionMessages.TOURNAMENT_SERIES_RIVAL_VALIDATION_ERROR, "seriesRivalDto",
                    "parameter 'seriesRivalDto' is not set for get or modify tournament series rival");
        }
        Set<ConstraintViolation<TournamentSeriesRivalDto>> settingsViolations = validator.validate(seriesRivalDto);
        if (!settingsViolations.isEmpty()) {
            log.debug("^ transmitted tournament series rival dto: {} have constraint violations: {}",
                    seriesRivalDto, settingsViolations);
            throw new ConstraintViolationException(settingsViolations);
        }
        TournamentSeriesRival tournamentSeriesRival = null;
        if (nonNull(seriesRivalDto.getId())) {
            tournamentSeriesRival = this.getVerifiedSeriesRivalById(seriesRivalDto.getId());
            if (!seriesRivalDto.getTournamentSeriesId().equals(tournamentSeriesRival.getTournamentSeries().getId())) {
                log.warn("~ parameter 'SeriesRivalDto.tournamentSeriesId' isn't fit existed ref from SeriesRival to Series. " +
                        "Request to change reference from SeriesRival to other Series is prohibited in getVerifiedSeriesRivalByDto");
                throw new ValidationException(ExceptionMessages.TOURNAMENT_SERIES_RIVAL_VALIDATION_ERROR, "SeriesRivalDto.tournamentSeriesId",
                        "parameter 'tournament organizer' is not Series by id to tournament for getVerifiedSeriesRivalByDto");
            }
            tournamentSeriesRival.setWonPlaceInSeries(seriesRivalDto.getWonPlaceInSeries());
        }
        return tournamentSeriesRival;
    }
}

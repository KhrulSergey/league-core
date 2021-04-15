package com.freetonleague.core.service.implementations;


import com.freetonleague.core.domain.dto.TournamentSeriesDto;
import com.freetonleague.core.domain.enums.TournamentStatusType;
import com.freetonleague.core.domain.model.Tournament;
import com.freetonleague.core.domain.model.TournamentSeries;
import com.freetonleague.core.domain.model.User;
import com.freetonleague.core.exception.*;
import com.freetonleague.core.mapper.TournamentSeriesMapper;
import com.freetonleague.core.service.RestTournamentFacade;
import com.freetonleague.core.service.RestTournamentSeriesService;
import com.freetonleague.core.service.TournamentSeriesService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import javax.validation.Validator;
import java.util.Set;

import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;

/**
 * Service-facade for managing tournament series
 */
@Slf4j
@RequiredArgsConstructor
@Service
public class RestTournamentSeriesServiceImpl implements RestTournamentSeriesService {

    private final TournamentSeriesService tournamentSeriesService;
    private final TournamentSeriesMapper tournamentSeriesMapper;
    private final Validator validator;

    @Lazy
    @Autowired
    private RestTournamentFacade restTournamentFacade;

    /**
     * Returns founded tournament series by id
     */
    @Override
    public TournamentSeriesDto getSeries(long id, User user) {
        TournamentSeries tournamentSeries = this.getVerifiedSeriesById(id, user);
        return tournamentSeriesMapper.toDto(tournamentSeries);
    }

    /**
     * Returns list of all tournament series filtered by requested params
     */
    @Override
    public Page<TournamentSeriesDto> getSeriesList(Pageable pageable, long tournamentId, User user) {
        Tournament tournament = restTournamentFacade.getVerifiedTournamentById(tournamentId, user, true);
        return tournamentSeriesService.getSeriesList(pageable, tournament).map(tournamentSeriesMapper::toDto);
    }

    /**
     * Returns current active series for tournament
     */
    @Override
    public TournamentSeriesDto getActiveSeriesForTournament(long tournamentId, User user) {
        Tournament tournament = restTournamentFacade.getVerifiedTournamentById(tournamentId, user, true);
        return tournamentSeriesMapper.toDto(tournamentSeriesService.getActiveSeriesForTournament(tournament));
    }

    /**
     * Add new tournament series
     */
    //TODO make available only for orgs
    @Override
    public TournamentSeriesDto addSeries(TournamentSeriesDto tournamentSeriesDto, User user) {
        tournamentSeriesDto.setId(null);
        tournamentSeriesDto.setStatus(TournamentStatusType.CREATED);
        TournamentSeries newTournamentSeries = this.getVerifiedSeriesByDto(tournamentSeriesDto, user);

        newTournamentSeries = tournamentSeriesService.addSeries(newTournamentSeries);
        if (isNull(newTournamentSeries)) {
            log.error("!> error while creating tournament series from dto {} for user {}.", tournamentSeriesDto, user);
            throw new TournamentManageException(ExceptionMessages.TOURNAMENT_SERIES_CREATION_ERROR,
                    "Tournament series was not saved on Portal. Check requested params.");
        }
        return tournamentSeriesMapper.toDto(newTournamentSeries);
    }

    /**
     * Generate next active series for tournament.
     */
    //TODO make available only for orgs
    @Override
    public void generateSeriesForTournament(long tournamentId, User user) {
        Tournament tournament = restTournamentFacade.getVerifiedTournamentById(tournamentId, user, true);
        boolean result = tournamentSeriesService.generateSeriesForTournament(tournament);
        if (!result) {
            log.error("!> error while generated tournament series list for tournament id {} with user {}.", tournamentId, user);
            throw new TournamentManageException(ExceptionMessages.TOURNAMENT_SERIES_GENERATION_ERROR,
                    "Tournament series was not generated and saved on Portal. Check requested params.");
        }
    }

    /**
     * Edit tournament series.
     */
    //TODO make available only for orgs
    @Override
    public TournamentSeriesDto editSeries(long id, TournamentSeriesDto tournamentSeriesDto, User user) {
        if (isNull(tournamentSeriesDto) || tournamentSeriesDto.getId() != id) {
            log.warn("~ parameter 'tournamentSeriesDto.id' is not match specified id in parameters for editSeries");
            throw new ValidationException(ExceptionMessages.TOURNAMENT_SERIES_VALIDATION_ERROR, "tournamentSeriesDto.id",
                    "parameter 'tournamentSeriesDto.id' is not match specified id in parameters for editSeries");
        }
        TournamentSeries tournamentSeries = this.getVerifiedSeriesByDto(tournamentSeriesDto, user);

        tournamentSeries = tournamentSeriesService.editSeries(tournamentSeries);
        if (isNull(tournamentSeries)) {
            log.error("!> error while editing tournament series from dto {} for user {}.", tournamentSeriesDto, user);
            throw new TournamentManageException(ExceptionMessages.TOURNAMENT_SERIES_MODIFICATION_ERROR,
                    "Tournament series was not updated on Portal. Check requested params.");
        }
        return tournamentSeriesMapper.toDto(tournamentSeries);
    }

    /**
     * Mark 'deleted' tournament series.
     */
    //TODO make available only for orgs
    @Override
    public TournamentSeriesDto deleteSeries(long id, User user) {
        TournamentSeries tournamentSeries = this.getVerifiedSeriesById(id, user);
        tournamentSeries = tournamentSeriesService.deleteSeries(tournamentSeries);

        if (isNull(tournamentSeries)) {
            log.error("!> error while deleting tournament series with id {} for user {}.", id, user);
            throw new TournamentManageException(ExceptionMessages.TOURNAMENT_SERIES_MODIFICATION_ERROR,
                    "Tournament series was not deleted on Portal. Check requested params.");
        }
        return tournamentSeriesMapper.toDto(tournamentSeries);
    }

    /**
     * Returns tournament series by id and user with privacy check
     */
    @Override
    public TournamentSeries getVerifiedSeriesById(long id, User user) {
        if (isNull(user)) {
            log.debug("^ user is not authenticate. 'getVerifiedSeriesById' in RestTournamentSeriesService request denied");
            throw new UnauthorizedException(ExceptionMessages.AUTHENTICATION_ERROR, "'getVerifiedSeriesById' request denied");
        }
        TournamentSeries tournamentSeries = tournamentSeriesService.getSeries(id);
        if (isNull(tournamentSeries)) {
            log.debug("^ Tournament series with requested id {} was not found. 'getVerifiedSeriesById' in RestTournamentSeriesService request denied", id);
            throw new TeamManageException(ExceptionMessages.TOURNAMENT_SERIES_NOT_FOUND_ERROR, "Tournament series  with requested id " + id + " was not found");
        }
        if (tournamentSeries.getStatus() == TournamentStatusType.DELETED) {
            log.debug("^ Tournament series with requested id {} was {}. 'getVerifiedSeriesById' in RestTournamentSeriesService request denied", id, tournamentSeries.getStatus());
            throw new TeamManageException(ExceptionMessages.TOURNAMENT_SERIES_DISABLE_ERROR, "Active tournament series with requested id " + id + " was not found");
        }
        return tournamentSeries;
    }

    /**
     * Getting tournament settings by DTO with privacy check
     */
    @Override
    public TournamentSeries getVerifiedSeriesByDto(TournamentSeriesDto tournamentSeriesDto, User user) {
        if (isNull(tournamentSeriesDto)) {
            log.warn("~ parameter 'tournamentSeriesDto' is NULL for getVerifiedSeriesByDto");
            throw new ValidationException(ExceptionMessages.TOURNAMENT_SERIES_VALIDATION_ERROR, "tournamentSeriesDto",
                    "parameter 'tournamentSeriesDto' is not set for get or modify tournament series");
        }
        if (isNull(tournamentSeriesDto.getTournamentId())) {
            log.warn("~ parameter 'tournament id' is not set in tournamentSeriesDto for getVerifiedSeriesByDto");
            throw new ValidationException(ExceptionMessages.TOURNAMENT_SERIES_VALIDATION_ERROR, "tournament settings",
                    "parameter 'tournament id' is not set in tournamentSeriesDto for get or modify tournament series");
        }
        Tournament tournament = restTournamentFacade.getVerifiedTournamentById(tournamentSeriesDto.getTournamentId(),
                user, true);

        Set<ConstraintViolation<TournamentSeriesDto>> settingsViolations = validator.validate(tournamentSeriesDto);
        if (!settingsViolations.isEmpty()) {
            log.debug("^ transmitted tournament series dto: {} have constraint violations: {}",
                    tournamentSeriesDto, settingsViolations);
            throw new ConstraintViolationException(settingsViolations);
        }

        //Check existence of tournament series and it's status
        if (nonNull(tournamentSeriesDto.getId())) {
            getVerifiedSeriesById(tournamentSeriesDto.getId(), user);
        }
        TournamentSeries tournamentSeries = tournamentSeriesMapper.fromDto(tournamentSeriesDto);
        tournamentSeries.setTournament(tournament);

        return tournamentSeries;
    }
}

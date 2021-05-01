package com.freetonleague.core.service.implementations;


import com.freetonleague.core.domain.dto.TournamentSeriesDto;
import com.freetonleague.core.domain.enums.TournamentStatusType;
import com.freetonleague.core.domain.model.TournamentRound;
import com.freetonleague.core.domain.model.TournamentSeries;
import com.freetonleague.core.domain.model.User;
import com.freetonleague.core.exception.ExceptionMessages;
import com.freetonleague.core.exception.TeamManageException;
import com.freetonleague.core.exception.TournamentManageException;
import com.freetonleague.core.exception.ValidationException;
import com.freetonleague.core.mapper.TournamentSeriesMapper;
import com.freetonleague.core.security.permissions.CanManageTournament;
import com.freetonleague.core.service.RestTournamentRoundFacade;
import com.freetonleague.core.service.RestTournamentSeriesFacade;
import com.freetonleague.core.service.TournamentSeriesService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
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
public class RestTournamentSeriesFacadeImpl implements RestTournamentSeriesFacade {

    private final TournamentSeriesService tournamentSeriesService;
    private final TournamentSeriesMapper tournamentSeriesMapper;
    private final Validator validator;

    @Lazy
    @Autowired
    private RestTournamentRoundFacade restTournamentRoundFacade;

    /**
     * Returns founded tournament series by id
     */
    @Override
    public TournamentSeriesDto getSeries(long id, User user) {
        TournamentSeries tournamentSeries = this.getVerifiedSeriesById(id, user);
        return tournamentSeriesMapper.toDto(tournamentSeries);
    }

    //TODO delete until 01/07/21 if not needed
//    /**
//     * Returns list of all tournament series filtered by requested params
//     */
//    @Override
//    public Page<TournamentSeriesDto> getSeriesList(Pageable pageable, long tournamentId, User user) {
//        Tournament tournament = restTournamentFacade.getVerifiedTournamentById(tournamentId, user, true);
//        return tournamentSeriesService.getSeriesList(pageable, tournament).map(tournamentSeriesMapper::toDto);
//    }

    /**
     * Add new tournament series
     */
    @CanManageTournament
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
     * Edit tournament series.
     */
    @CanManageTournament
    @Override
    public TournamentSeriesDto editSeries(long id, TournamentSeriesDto tournamentSeriesDto, User user) {
        if (isNull(tournamentSeriesDto) || tournamentSeriesDto.getId() != id) {
            log.warn("~ parameter 'tournamentSeriesDto.id' is not match specified id in parameters for editSeries");
            throw new ValidationException(ExceptionMessages.TOURNAMENT_SERIES_VALIDATION_ERROR, "tournamentSeriesDto.id",
                    "parameter 'tournamentSeriesDto.id' is not match specified id in parameters for editSeries");
        }
        TournamentSeries tournamentSeries = this.getVerifiedSeriesByDto(tournamentSeriesDto, user);
        if (tournamentSeries.getStatus().isDeleted()) {
            log.warn("~ tournament series deleting was declined in editSeries. This operation should be done with specific method.");
            throw new TournamentManageException(ExceptionMessages.TOURNAMENT_SERIES_STATUS_DELETE_ERROR,
                    "Modifying tournament series was rejected. Check requested params and method.");
        }
        //Match can be finished only with setting the winner of the match
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
    @CanManageTournament
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
        TournamentSeries tournamentSeries = tournamentSeriesService.getSeries(id);
        if (isNull(tournamentSeries)) {
            log.debug("^ Tournament series with requested id {} was not found. 'getVerifiedSeriesById' in RestTournamentSeriesService request denied", id);
            throw new TeamManageException(ExceptionMessages.TOURNAMENT_SERIES_NOT_FOUND_ERROR, "Tournament series  with requested id " + id + " was not found");
        }
        if (tournamentSeries.getStatus().isDeleted()) {
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
        if (isNull(tournamentSeriesDto.getTournamentRoundId())) {
            log.warn("~ parameter 'tournament round id' is not set in tournamentSeriesDto for getVerifiedSeriesByDto");
            throw new ValidationException(ExceptionMessages.TOURNAMENT_SERIES_VALIDATION_ERROR, "tournament round id",
                    "parameter 'tournament round id' is not set in tournamentSeriesDto for get or modify tournament series");
        }
        TournamentRound tournamentRound = restTournamentRoundFacade.getVerifiedRoundById(tournamentSeriesDto.getTournamentRoundId(),
                user);

        Set<ConstraintViolation<TournamentSeriesDto>> settingsViolations = validator.validate(tournamentSeriesDto);
        if (!settingsViolations.isEmpty()) {
            log.debug("^ transmitted tournament series dto: {} have constraint violations: {}",
                    tournamentSeriesDto, settingsViolations);
            throw new ConstraintViolationException(settingsViolations);
        }
        if (tournamentSeriesDto.getStatus().isFinished()) {
            log.warn("~ tournament series can be finished only automatically when all match is finished." +
                    "Request to set status was rejected.");
            throw new TournamentManageException(ExceptionMessages.TOURNAMENT_MATCH_STATUS_FINISHED_ERROR,
                    "Modifying tournament match was rejected. Check requested params and method.");
        }

        TournamentSeries tournamentSeries = tournamentSeriesMapper.fromDto(tournamentSeriesDto);

        //Check existence of tournament series and it's status
        if (nonNull(tournamentSeriesDto.getId())) {
            TournamentSeries existedSeries = getVerifiedSeriesById(tournamentSeriesDto.getId(), user);
            tournamentSeries.setParentSeriesList(existedSeries.getParentSeriesList());
            tournamentSeries.setMatchList(existedSeries.getMatchList());
        }

        tournamentSeries.setTournamentRound(tournamentRound);
        return tournamentSeries;
    }
}

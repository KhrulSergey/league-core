package com.freetonleague.core.service.tournament.implementations;


import com.freetonleague.core.domain.dto.tournament.TournamentSeriesDto;
import com.freetonleague.core.domain.dto.tournament.TournamentSeriesRivalDto;
import com.freetonleague.core.domain.enums.tournament.TournamentStatusType;
import com.freetonleague.core.domain.enums.tournament.TournamentWinnerPlaceType;
import com.freetonleague.core.domain.model.User;
import com.freetonleague.core.domain.model.tournament.TournamentRound;
import com.freetonleague.core.domain.model.tournament.TournamentSeries;
import com.freetonleague.core.domain.model.tournament.TournamentSeriesRival;
import com.freetonleague.core.exception.TeamManageException;
import com.freetonleague.core.exception.TournamentManageException;
import com.freetonleague.core.exception.UnauthorizedException;
import com.freetonleague.core.exception.ValidationException;
import com.freetonleague.core.exception.config.ExceptionMessages;
import com.freetonleague.core.mapper.tournament.TournamentSeriesMapper;
import com.freetonleague.core.security.permissions.CanManageTournament;
import com.freetonleague.core.service.tournament.RestTournamentRoundFacade;
import com.freetonleague.core.service.tournament.RestTournamentSeriesFacade;
import com.freetonleague.core.service.tournament.RestTournamentSeriesRivalFacade;
import com.freetonleague.core.service.tournament.TournamentSeriesService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import javax.validation.Validator;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;
import static org.apache.commons.lang3.ObjectUtils.isNotEmpty;

/**
 * Service-facade for managing tournament series
 */
@Slf4j
@RequiredArgsConstructor
@Service
public class RestTournamentSeriesFacadeImpl implements RestTournamentSeriesFacade {

    private final TournamentSeriesService tournamentSeriesService;
    private final TournamentSeriesMapper tournamentSeriesMapper;
    private final RestTournamentSeriesRivalFacade restTournamentSeriesRivalFacade;
    private final Validator validator;

    @Lazy
    @Autowired
    private RestTournamentRoundFacade restTournamentRoundFacade;

    /**
     * Returns founded tournament series by id
     */
    @Override
    public TournamentSeriesDto getSeries(long id, User user) {
        TournamentSeries tournamentSeries = this.getVerifiedSeriesById(id);
        return tournamentSeriesMapper.toDto(tournamentSeries);
    }

    /**
     * Add new tournament series
     */
    @CanManageTournament
    @Override
    public TournamentSeriesDto addSeries(TournamentSeriesDto tournamentSeriesDto, User user) {
        tournamentSeriesDto.setId(null);
        tournamentSeriesDto.setStatus(TournamentStatusType.CREATED);
        TournamentSeries newTournamentSeries = this.getVerifiedSeriesByDto(tournamentSeriesDto);

        newTournamentSeries = tournamentSeriesService.addSeries(newTournamentSeries);
        if (isNull(newTournamentSeries)) {
            log.error("!> error while creating tournament series from dto '{}' for user '{}'.", tournamentSeriesDto, user);
            throw new TournamentManageException(ExceptionMessages.TOURNAMENT_SERIES_CREATION_ERROR,
                    "Tournament series was not saved on Portal. Check requested params.");
        }
        return tournamentSeriesMapper.toDto(newTournamentSeries);
    }

    /**
     * Generate OMT (match) for specified series.
     */
    @CanManageTournament
    @Override
    public TournamentSeriesDto generateOmtForSeries(long id, User user) {
        TournamentSeries tournamentSeries = this.getVerifiedSeriesById(id);
        tournamentSeries = tournamentSeriesService.generateOmtForSeries(tournamentSeries);
        if (isNull(tournamentSeries)) {
            log.error("!> error while generate Omt match for tournament series.id '{}'.", id);
            throw new TournamentManageException(ExceptionMessages.TOURNAMENT_SERIES_GENERATION_ERROR,
                    "OMT match was not generated and saved for tournament series " + id + ". Check requested params.");
        }
        return tournamentSeriesMapper.toDto(tournamentSeries);
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
        TournamentSeries tournamentSeries = this.getVerifiedSeriesByDto(tournamentSeriesDto);
        if (tournamentSeries.getStatus().isDeleted()) {
            log.warn("~ tournament series deleting was declined in editSeries. This operation should be done with specific method.");
            throw new TournamentManageException(ExceptionMessages.TOURNAMENT_SERIES_STATUS_DELETE_ERROR,
                    "Modifying tournament series was rejected. Check requested params and method.");
        }

        boolean isSeriesFinished = tournamentSeries.getStatus().isFinished();
        boolean isSeriesHasNoWinner = tournamentSeries.getHasNoWinner();
        boolean isSeriesWinnerIsSet = nonNull(tournamentSeries.getSeriesWinner());

        //Series can be finished only with setting winner places in list of rivals
        if (isSeriesFinished && ((isSeriesHasNoWinner && isSeriesWinnerIsSet) || (!isSeriesHasNoWinner && !isSeriesWinnerIsSet))) {
            log.warn("~ tournament series can be finished only with setting parameter 'hasNoWinner=false' and/or " +
                            "the winner of the series or set winner places in series rivals. " +
                            "Request to set status '{}', hasNoWinner '{}', winner '{}' and rivals '{}' was rejected.",
                    tournamentSeries.getStatus(), tournamentSeries.getHasNoWinner(),
                    tournamentSeries.getSeriesWinner(), tournamentSeries.getSeriesRivalList());
            throw new TournamentManageException(ExceptionMessages.TOURNAMENT_SERIES_STATUS_FINISHED_ERROR,
                    "Modifying tournament series was rejected. Check requested params and method.");
        }

        tournamentSeries = tournamentSeriesService.editSeries(tournamentSeries);
        if (isNull(tournamentSeries)) {
            log.error("!> error while editing tournament series from dto '{}' for user '{}'.", tournamentSeriesDto, user);
            throw new TournamentManageException(ExceptionMessages.TOURNAMENT_SERIES_MODIFICATION_ERROR,
                    "Tournament series was not updated on Portal. Check requested params.");
        }
        return tournamentSeriesMapper.toDto(tournamentSeries);
    }

    /**
     * Edit tournament series by rivals (set only winner of series and wonPlaceInSeries for rival).
     */
    @Override
    public TournamentSeriesDto editSeriesByRivals(long id, TournamentSeriesDto tournamentSeriesDto, User user) {
        if (isNull(user)) {
            log.debug("^ user is not authenticate. 'editSeriesByRivals' in RestTournamentSeriesFacade request denied");
            throw new UnauthorizedException(ExceptionMessages.AUTHENTICATION_ERROR, "'editSeriesByRivals' request denied");
        }
        if (isNull(tournamentSeriesDto) || tournamentSeriesDto.getId() != id) {
            log.warn("~ parameter 'tournamentSeriesDto.id' is not match specified id in parameters for editSeriesByRivals");
            throw new ValidationException(ExceptionMessages.TOURNAMENT_SERIES_VALIDATION_ERROR, "tournamentSeriesDto.id",
                    "parameter 'tournamentSeriesDto.id' is not match specified id in parameters for editSeriesByRivals");
        }
        Set<ConstraintViolation<TournamentSeriesDto>> settingsViolations = validator.validate(tournamentSeriesDto);
        if (!settingsViolations.isEmpty()) {
            log.debug("^ transmitted tournament series dto: '{}' have constraint violations: '{}'",
                    tournamentSeriesDto, settingsViolations);
            throw new ConstraintViolationException(settingsViolations);
        }

        // get current series by ID from DB
        TournamentSeries tournamentSeries = this.getVerifiedSeriesById(tournamentSeriesDto.getId());
        Boolean isSeriesModifiableByRival = tournamentSeriesService.isSeriesModifiableByRival(tournamentSeries);
        if (isNull(isSeriesModifiableByRival) || !isSeriesModifiableByRival) {
            log.warn("~ tournament series can be modified by rivals. Tournament is not self-hosted. Request rejected.");
            throw new TournamentManageException(ExceptionMessages.TOURNAMENT_SERIES_MODIFICATION_ERROR,
                    "Modifying tournament series by rival was rejected. Tournament is not self-hosted. Check requested params.");
        }

        if (TournamentStatusType.finishedStatusList.contains(tournamentSeries.getStatus())) {
            log.warn("~ tournament series has finished, unable to modify by rival declined in editSeriesByRivals.");
            throw new TournamentManageException(ExceptionMessages.TOURNAMENT_SERIES_MODIFICATION_ERROR,
                    "Modifying finished tournament series by rival was rejected. Check requested params and method.");
        }
        if (!tournamentSeriesService.isUserSeriesRivalParticipant(tournamentSeries, user)) {
            log.warn("~ user is not actively participate in specified tournament series has finished. Request to modify series is rejected.");
            throw new TournamentManageException(ExceptionMessages.TOURNAMENT_SERIES_MODIFICATION_ERROR,
                    "User is not actively participate in specified tournament series. Modifying tournament series was rejected.");
        }

        // check and compose match rival list (modify WonPlaceInMatch for existed rival)
        List<TournamentSeriesRivalDto> tournamentSeriesRivalDtoList = tournamentSeriesDto.getSeriesRivalList();
        List<TournamentSeriesRival> tournamentSeriesRivalList = null;
        if (isNotEmpty(tournamentSeriesRivalDtoList)) {
            tournamentSeriesRivalList = tournamentSeriesRivalDtoList.parallelStream()
                    .map(restTournamentSeriesRivalFacade::getVerifiedSeriesRivalByDtoForRival)
                    .filter(Objects::nonNull)
                    .collect(Collectors.toList());
        }
        if (isNotEmpty(tournamentSeriesRivalList)) {
            for (TournamentSeriesRival tournamentSeriesRival : tournamentSeriesRivalList) {
                tournamentSeriesRival.setTournamentSeries(tournamentSeries);
            }
            tournamentSeries.setSeriesRivalList(tournamentSeriesRivalList);
        }

        // try to define series winner from SeriesWinner field or rival from SeriesRivalList with First place
        TournamentSeriesRivalDto seriesRivalWinnerDto = tournamentSeriesDto.getSeriesWinner();
        TournamentSeriesRival seriesWinner = tournamentSeries.getSeriesWinner();
        if (isNotEmpty(tournamentSeriesRivalList)) {
            //try to find series winner (first place)
            seriesWinner = tournamentSeriesRivalList.parallelStream()
                    .filter(s -> nonNull(s.getWonPlaceInSeries())
                            && s.getWonPlaceInSeries().isWinner())
                    .findFirst().orElse(null);
        } else if (nonNull(seriesRivalWinnerDto)) {
            seriesRivalWinnerDto.setWonPlaceInSeries(TournamentWinnerPlaceType.FIRST);
            seriesWinner = restTournamentSeriesRivalFacade.getVerifiedSeriesRivalByDtoForRival(seriesRivalWinnerDto);
        }
        if (nonNull(seriesWinner)) {
            seriesWinner.setTournamentSeries(tournamentSeries);
            tournamentSeries.setSeriesWinner(seriesWinner);
        }

        tournamentSeries = tournamentSeriesService.editSeries(tournamentSeries);
        if (isNull(tournamentSeries)) {
            log.error("!> error while editing tournament series by rival from dto '{}' for user '{}'.", tournamentSeriesDto, user);
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
        TournamentSeries tournamentSeries = this.getVerifiedSeriesById(id);
        tournamentSeries = tournamentSeriesService.deleteSeries(tournamentSeries);

        if (isNull(tournamentSeries)) {
            log.error("!> error while deleting tournament series with id '{}' for user '{}'.", id, user);
            throw new TournamentManageException(ExceptionMessages.TOURNAMENT_SERIES_MODIFICATION_ERROR,
                    "Tournament series was not deleted on Portal. Check requested params.");
        }
        return tournamentSeriesMapper.toDto(tournamentSeries);
    }

    /**
     * Returns tournament series by id and user with privacy check
     */
    @Override
    public TournamentSeries getVerifiedSeriesById(long id) {
        TournamentSeries tournamentSeries = tournamentSeriesService.getSeries(id);
        if (isNull(tournamentSeries)) {
            log.debug("^ Tournament series with requested id '{}' was not found. 'getVerifiedSeriesById' in RestTournamentSeriesService request denied", id);
            throw new TeamManageException(ExceptionMessages.TOURNAMENT_SERIES_NOT_FOUND_ERROR, "Tournament series  with requested id " + id + " was not found");
        }
        if (tournamentSeries.getStatus().isDeleted()) {
            log.debug("^ Tournament series with requested id '{}' was '{}'. 'getVerifiedSeriesById' in RestTournamentSeriesService request denied", id, tournamentSeries.getStatus());
            throw new TeamManageException(ExceptionMessages.TOURNAMENT_SERIES_DISABLE_ERROR, "Active tournament series with requested id " + id + " was not found");
        }
        return tournamentSeries;
    }

    /**
     * Getting tournament settings by DTO with privacy check
     */
    @Override
    public TournamentSeries getVerifiedSeriesByDto(TournamentSeriesDto tournamentSeriesDto) {
        if (isNull(tournamentSeriesDto)) {
            log.warn("~ parameter 'tournamentSeriesDto' is NULL for getVerifiedSeriesByDto");
            throw new ValidationException(ExceptionMessages.TOURNAMENT_SERIES_VALIDATION_ERROR, "tournamentSeriesDto",
                    "parameter 'tournamentSeriesDto' is not set for add or modify tournament series");
        }
        Set<ConstraintViolation<TournamentSeriesDto>> settingsViolations = validator.validate(tournamentSeriesDto);
        if (!settingsViolations.isEmpty()) {
            log.debug("^ transmitted tournament series dto: '{}' have constraint violations: '{}'",
                    tournamentSeriesDto, settingsViolations);
            throw new ConstraintViolationException(settingsViolations);
        }

        TournamentSeries tournamentSeries = tournamentSeriesMapper.fromDto(tournamentSeriesDto);

        //Check existence of tournament series and fill main properties
        if (nonNull(tournamentSeriesDto.getId())) {
            TournamentSeries existedSeries = getVerifiedSeriesById(tournamentSeriesDto.getId());
            tournamentSeries.setParentSeriesList(existedSeries.getParentSeriesList());
            tournamentSeries.setChildSeries(existedSeries.getChildSeries());
            tournamentSeries.setMatchList(existedSeries.getMatchList());
            tournamentSeries.setSeriesRivalList(existedSeries.getSeriesRivalList());
            tournamentSeries.setTournamentRound(existedSeries.getTournamentRound());
            tournamentSeries.setPrevStatus(existedSeries.getStatus());
        }

        // check and compose match rival list (modify Status, WonPlaceInMatch, Indicators for rival)
        List<TournamentSeriesRivalDto> tournamentSeriesRivalDtoList = tournamentSeriesDto.getSeriesRivalList();
        List<TournamentSeriesRival> tournamentSeriesRivalList = null;
        if (isNotEmpty(tournamentSeriesRivalDtoList)) {
            tournamentSeriesRivalList = tournamentSeriesRivalDtoList.parallelStream()
                    .map(restTournamentSeriesRivalFacade::getVerifiedSeriesRivalByDto)
                    .filter(Objects::nonNull)
                    .collect(Collectors.toList());
        }
        if (isNotEmpty(tournamentSeriesRivalList)) {
            tournamentSeriesRivalList = tournamentSeriesRivalList.parallelStream()
                    .peek(s -> s.setTournamentSeries(tournamentSeries)).collect(Collectors.toList());
            tournamentSeries.setSeriesRivalList(tournamentSeriesRivalList);
        }

        // try to define series winner from SeriesWinner field or rival from SeriesRivalList with First place
        TournamentSeriesRivalDto seriesRivalWinnerDto = tournamentSeriesDto.getSeriesWinner();
        TournamentSeriesRival seriesWinner = null;
        if (nonNull(seriesRivalWinnerDto)) {
            seriesRivalWinnerDto.setWonPlaceInSeries(TournamentWinnerPlaceType.FIRST);
            seriesWinner = restTournamentSeriesRivalFacade.getVerifiedSeriesRivalByDto(seriesRivalWinnerDto);
        } else if (isNotEmpty(tournamentSeriesRivalList)) {
            //try to find series winner (first place)
            seriesWinner = tournamentSeriesRivalList.parallelStream()
                    .filter(s -> nonNull(s.getWonPlaceInSeries())
                            && s.getWonPlaceInSeries().isWinner())
                    .findFirst().orElse(null);
        }
        if (nonNull(seriesWinner)) {
            tournamentSeries.setSeriesWinner(seriesWinner);
        }

        if (isNull(tournamentSeries.getTournamentRound())) {
            TournamentRound tournamentRound = restTournamentRoundFacade.getVerifiedRoundById(tournamentSeriesDto.getTournamentRoundId());
            tournamentSeries.setTournamentRound(tournamentRound);
        }
        return tournamentSeries;
    }
}

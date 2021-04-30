package com.freetonleague.core.service.implementations;


import com.freetonleague.core.domain.dto.TournamentMatchDto;
import com.freetonleague.core.domain.dto.TournamentMatchRivalDto;
import com.freetonleague.core.domain.enums.TournamentStatusType;
import com.freetonleague.core.domain.model.TournamentMatch;
import com.freetonleague.core.domain.model.TournamentMatchRival;
import com.freetonleague.core.domain.model.TournamentSeries;
import com.freetonleague.core.domain.model.User;
import com.freetonleague.core.exception.*;
import com.freetonleague.core.mapper.TournamentMatchMapper;
import com.freetonleague.core.security.permissions.CanManageSystem;
import com.freetonleague.core.service.RestTournamentMatchFacade;
import com.freetonleague.core.service.RestTournamentMatchRivalFacade;
import com.freetonleague.core.service.RestTournamentSeriesFacade;
import com.freetonleague.core.service.TournamentMatchService;
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
 * Service-facade for managing tournament match
 */
@Slf4j
@RequiredArgsConstructor
@Service
public class RestTournamentMatchFacadeImpl implements RestTournamentMatchFacade {

    private final TournamentMatchService tournamentMatchService;
    private final TournamentMatchMapper tournamentMatchMapper;
    private final RestTournamentMatchRivalFacade restTournamentMatchRivalFacade;
    private final Validator validator;

    @Lazy
    @Autowired
    private RestTournamentSeriesFacade restTournamentSeriesFacade;

    /**
     * Returns founded tournament match by id
     */
    @Override
    public TournamentMatchDto getMatch(long id, User user) {
        return tournamentMatchMapper.toDto(this.getVerifiedMatchById(id, user, false));
    }

    /**
     * Returns list of all tournament matches filtered by requested params
     */
    @Override
    public Page<TournamentMatchDto> getMatchList(Pageable pageable, long tournamentSeriesId, User user) {
        TournamentSeries tournamentSeries = restTournamentSeriesFacade.getVerifiedSeriesById(tournamentSeriesId, user);
        return tournamentMatchService.getMatchList(pageable, tournamentSeries).map(tournamentMatchMapper::toDto);
    }

    /**
     * Add new tournament match.
     */
    @CanManageSystem
    @Override
    public TournamentMatchDto addMatch(TournamentMatchDto tournamentMatchDto, User user) {

        tournamentMatchDto.setId(null);
        tournamentMatchDto.setStatus(TournamentStatusType.CREATED);

        TournamentMatch tournamentMatch = this.getVerifiedTournamentMatchByDto(tournamentMatchDto, user, true);
        tournamentMatch = tournamentMatchService.addMatch(tournamentMatch);

        if (isNull(tournamentMatch)) {
            log.error("!> error while creating tournament match from dto {} for user {}.", tournamentMatchDto, user);
            throw new TournamentManageException(ExceptionMessages.TOURNAMENT_MATCH_CREATION_ERROR,
                    "Tournament match was not saved on Portal. Check requested params.");
        }
        return tournamentMatchMapper.toDto(tournamentMatch);
    }

    /**
     * Edit tournament match.
     */
    @CanManageSystem
    @Override
    public TournamentMatchDto editMatch(long matchId, TournamentMatchDto tournamentMatchDto, User user) {
        if (isNull(tournamentMatchDto) || tournamentMatchDto.getId() != matchId) {
            log.warn("~ parameter 'tournamentMatchDto.id' is not match specified id in parameters for editMatch");
            throw new ValidationException(ExceptionMessages.TOURNAMENT_MATCH_VALIDATION_ERROR, "tournamentMatchDto.id",
                    "parameter 'tournamentMatchDto.id' is not match specified id in parameters for editMatch");
        }
        TournamentMatch tournamentMatch = this.getVerifiedTournamentMatchByDto(tournamentMatchDto, user, true);

        if (tournamentMatch.getStatus().isDeleted()) {
            log.warn("~ tournament match deleting was declined in editMatch. This operation should be done with specific method.");
            throw new TournamentManageException(ExceptionMessages.TOURNAMENT_MATCH_STATUS_DELETE_ERROR,
                    "Modifying tournament match was rejected. Check requested params and method.");
        }

        //Match can be finished only with setting the winner of the match
        if ((tournamentMatch.getStatus().isFinished() && isNull(tournamentMatch.getMatchWinner()))
                || (nonNull(tournamentMatch.getMatchWinner()) && !tournamentMatch.getStatus().isFinished())) {
            log.warn("~ tournament match can be finished only with setting the winner of the match. " +
                    "Request to set status {} and winner {} was rejected.", tournamentMatch.getStatus(), tournamentMatch.getMatchWinner());
            throw new TournamentManageException(ExceptionMessages.TOURNAMENT_MATCH_STATUS_FINISHED_ERROR,
                    "Modifying tournament match was rejected. Check requested params and method.");
        }

        tournamentMatch = tournamentMatchService.editMatch(tournamentMatch);
        if (isNull(tournamentMatch)) {
            log.error("!> error while editing tournament match from dto {} for user {}.", tournamentMatchDto, user);
            throw new TournamentManageException(ExceptionMessages.TOURNAMENT_MATCH_MODIFICATION_ERROR,
                    "Tournament series was not updated on Portal. Check requested params.");
        }
        return tournamentMatchMapper.toDto(tournamentMatch);
    }

    /**
     * Mark 'deleted' tournament matches in DB.
     */
    @CanManageSystem
    @Override
    public TournamentMatchDto deleteMatch(long matchId, User user) {
        TournamentMatch tournamentMatch = this.getVerifiedMatchById(matchId, user, true);
        tournamentMatch = tournamentMatchService.deleteMatch(tournamentMatch);

        if (isNull(tournamentMatch)) {
            log.error("!> error while deleting tournament match with id {} for user {}.", matchId, user);
            throw new TournamentManageException(ExceptionMessages.TOURNAMENT_MATCH_MODIFICATION_ERROR,
                    "Tournament match was not deleted on Portal. Check requested params.");
        }
        return tournamentMatchMapper.toDto(tournamentMatch);
    }

    /**
     * Returns tournament match by DTO, with validation, business logic and user with privacy check
     */
    @Override
    public TournamentMatch getVerifiedTournamentMatchByDto(TournamentMatchDto tournamentMatchDto, User user, boolean checkUser) {
        if (checkUser && isNull(user)) {
            log.debug("^ user is not authenticate. 'addTournament' request denied");
            throw new UnauthorizedException(ExceptionMessages.AUTHENTICATION_ERROR, "'addTournament' request denied");
        }

        if (isNull(tournamentMatchDto)) {
            log.warn("~ parameter 'tournamentMatchDto' is NULL for getVerifiedTournamentMatchByDto");
            throw new ValidationException(ExceptionMessages.TOURNAMENT_MATCH_VALIDATION_ERROR, "tournamentSeriesDto",
                    "parameter 'tournamentMatchDto' is not set for get or modify tournament match");
        }
        if (isNull(tournamentMatchDto.getTournamentSeriesId())) {
            log.warn("~ parameter 'tournament series id' is not set in tournamentMatchDto for getVerifiedTournamentMatchByDto");
            throw new ValidationException(ExceptionMessages.TOURNAMENT_MATCH_VALIDATION_ERROR, "tournament series id",
                    "parameter 'tournament series id' is not set in tournamentMatchDto for get or modify tournament series");
        }
        TournamentSeries tournamentSeries = restTournamentSeriesFacade.getVerifiedSeriesById(
                tournamentMatchDto.getTournamentSeriesId(), user);

        Set<ConstraintViolation<TournamentMatchDto>> settingsViolations = validator.validate(tournamentMatchDto);
        if (!settingsViolations.isEmpty()) {
            log.debug("^ transmitted tournament match dto: {} have constraint violations: {}",
                    tournamentMatchDto, settingsViolations);
            throw new ConstraintViolationException(settingsViolations);
        }
        //Check existence of specified by id of tournament match and it's status
        if (nonNull(tournamentMatchDto.getId())) {
            TournamentMatch existedTournamentMatch =
                    this.getVerifiedMatchById(tournamentMatchDto.getId(), user, false);
            if (!existedTournamentMatch.getTournamentSeries().equals(tournamentSeries)) {
                log.warn("~ parameter 'tournamentMatchDto.tournamentSeriesId' is not equals TournamentSeries that was saved previously in DB. " +
                        "Request denied in getVerifiedTournamentMatchByDto");
                throw new ValidationException(ExceptionMessages.TOURNAMENT_MATCH_VALIDATION_ERROR, "tournamentSeriesId",
                        "parameter 'tournamentMatchDto.tournamentSeriesId' is not equals TournamentSeries that was saved previously in DB. " +
                                "Request denied in getVerifiedTournamentMatchByDto");
            }
        }

        //TODO Check embedded collections of tournament match rivals
        TournamentMatchRivalDto rivalWinnerDto = tournamentMatchDto.getMatchWinner();
        TournamentMatchRival rivalMatchRival = null;
        if (nonNull(rivalWinnerDto)) {
            if (isNull(rivalWinnerDto.getId())) {
                log.warn("~ parameter 'rivalWinnerDto.id' is NULL for getVerifiedTournamentMatchByDto");
                throw new ValidationException(ExceptionMessages.TOURNAMENT_MATCH_RIVAL_VALIDATION_ERROR, "rivalWinnerDto.id",
                        "parameter 'rivalWinnerDto.id' is not set for get or modify tournament match rival");
            }
            rivalMatchRival = restTournamentMatchRivalFacade.getVerifiedMatchRivalByDto(rivalWinnerDto, user);
        }

        TournamentMatch tournamentMatch = tournamentMatchMapper.fromDto(tournamentMatchDto);
        tournamentMatch.setTournamentSeries(tournamentSeries);
        tournamentMatch.setMatchWinner(rivalMatchRival);

        return tournamentMatch;
    }

    /**
     * Returns tournament match by id and user with privacy check
     */
    @Override
    public TournamentMatch getVerifiedMatchById(long id, User user, boolean checkUser) {
        TournamentMatch tournamentMatch = tournamentMatchService.getMatch(id);
        if (isNull(tournamentMatch)) {
            log.debug("^ Tournament match with requested id {} was not found. 'getVerifiedMatchById' in RestTournamentMatchService request denied", id);
            throw new TeamManageException(ExceptionMessages.TOURNAMENT_MATCH_NOT_FOUND_ERROR, "Tournament match with requested id " + id + " was not found");
        }
        if (tournamentMatch.getStatus().isDeleted()) {
            log.debug("^ Tournament match with requested id {} was {}. 'getVerifiedMatchById' in RestTournamentMatchService request denied", id, tournamentMatch.getStatus());
            throw new TeamManageException(ExceptionMessages.TOURNAMENT_MATCH_DISABLE_ERROR, "Active tournament match with requested id " + id + " was not found");
        }
        return tournamentMatch;
    }
}

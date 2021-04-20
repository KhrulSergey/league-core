package com.freetonleague.core.service.implementations;


import com.freetonleague.core.domain.dto.TournamentMatchDto;
import com.freetonleague.core.domain.enums.TournamentStatusType;
import com.freetonleague.core.domain.model.TournamentMatch;
import com.freetonleague.core.domain.model.TournamentSeries;
import com.freetonleague.core.domain.model.User;
import com.freetonleague.core.exception.ExceptionMessages;
import com.freetonleague.core.exception.TeamManageException;
import com.freetonleague.core.exception.TournamentManageException;
import com.freetonleague.core.exception.ValidationException;
import com.freetonleague.core.mapper.TournamentMatchMapper;
import com.freetonleague.core.service.RestTournamentMatchFacade;
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
    private final Validator validator;

    @Lazy
    @Autowired
    private RestTournamentSeriesFacade restTournamentSeriesFacade;

    /**
     * Returns founded tournament match by id
     */
    @Override
    public TournamentMatchDto getMatch(long id, User user) {
        return tournamentMatchMapper.toDto(this.getVerifiedMatchById(id, user));
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
    //TODO make available only for admin for emergency use
    @Override
    public TournamentMatchDto addMatch(TournamentMatchDto tournamentMatchDto, User user) {

        tournamentMatchDto.setId(null);
        tournamentMatchDto.setStatus(TournamentStatusType.CREATED);

        TournamentMatch tournamentMatch = this.getVerifiedTournamentMatchByDto(tournamentMatchDto, user);
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
    //TODO make available only for orgs
    @Override
    public TournamentMatchDto editMatch(long matchId, TournamentMatchDto tournamentMatchDto, User user) {
        if (isNull(tournamentMatchDto) || tournamentMatchDto.getId() != matchId) {
            log.warn("~ parameter 'tournamentMatchDto.id' is not match specified id in parameters for editMatch");
            throw new ValidationException(ExceptionMessages.TOURNAMENT_MATCH_VALIDATION_ERROR, "tournamentMatchDto.id",
                    "parameter 'tournamentMatchDto.id' is not match specified id in parameters for editMatch");
        }
        TournamentMatch tournamentMatch = this.getVerifiedTournamentMatchByDto(tournamentMatchDto, user);

        if (tournamentMatch.getStatus() == TournamentStatusType.DELETED) {
            log.warn("~ tournament match deleting was declined in editMatch. This operation should be done with specific method.");
            throw new TournamentManageException(ExceptionMessages.TOURNAMENT_MATCH_STATUS_DELETE_ERROR,
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
    //TODO make available only for orgs
    @Override
    public TournamentMatchDto deleteMatch(long matchId, User user) {
        TournamentMatch tournamentMatch = this.getVerifiedMatchById(matchId, user);
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
    public TournamentMatch getVerifiedTournamentMatchByDto(TournamentMatchDto tournamentMatchDto, User user) {
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
            this.getVerifiedMatchById(tournamentMatchDto.getId(), user);
        }

        //TODO Check collection of tournament match rivals

        TournamentMatch tournamentMatch = tournamentMatchMapper.fromDto(tournamentMatchDto);
        tournamentMatch.setTournamentSeries(tournamentSeries);

        return tournamentMatch;
    }

    /**
     * Returns tournament match by id and user with privacy check
     */
    @Override
    public TournamentMatch getVerifiedMatchById(long id, User user) {
        TournamentMatch tournamentMatch = tournamentMatchService.getMatch(id);
        if (isNull(tournamentMatch)) {
            log.debug("^ Tournament match with requested id {} was not found. 'getVerifiedMatchById' in RestTournamentMatchService request denied", id);
            throw new TeamManageException(ExceptionMessages.TOURNAMENT_MATCH_NOT_FOUND_ERROR, "Tournament match with requested id " + id + " was not found");
        }
        if (tournamentMatch.getStatus() == TournamentStatusType.DELETED) {
            log.debug("^ Tournament match with requested id {} was {}. 'getVerifiedMatchById' in RestTournamentMatchService request denied", id, tournamentMatch.getStatus());
            throw new TeamManageException(ExceptionMessages.TOURNAMENT_MATCH_DISABLE_ERROR, "Active tournament match with requested id " + id + " was not found");
        }
        return tournamentMatch;
    }
}

package com.freetonleague.core.service.implementations;


import com.freetonleague.core.domain.dto.TournamentMatchDto;
import com.freetonleague.core.domain.dto.TournamentMatchRivalDto;
import com.freetonleague.core.domain.enums.TournamentStatusType;
import com.freetonleague.core.domain.enums.TournamentWinnerPlaceType;
import com.freetonleague.core.domain.model.TournamentMatch;
import com.freetonleague.core.domain.model.TournamentMatchRival;
import com.freetonleague.core.domain.model.TournamentSeries;
import com.freetonleague.core.domain.model.User;
import com.freetonleague.core.exception.TeamManageException;
import com.freetonleague.core.exception.TournamentManageException;
import com.freetonleague.core.exception.ValidationException;
import com.freetonleague.core.exception.config.ExceptionMessages;
import com.freetonleague.core.mapper.TournamentMatchMapper;
import com.freetonleague.core.security.permissions.CanManageTournament;
import com.freetonleague.core.service.*;
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
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;
import static org.apache.commons.lang3.ObjectUtils.isNotEmpty;

/**
 * Service-facade for managing tournament match
 */
@Slf4j
@RequiredArgsConstructor
@Service
public class RestTournamentMatchFacadeImpl implements RestTournamentMatchFacade {

    private final TournamentMatchService tournamentMatchService;
    private final TournamentMatchRivalService tournamentMatchRivalService;
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
        return tournamentMatchMapper.toDto(this.getVerifiedMatchById(id));
    }

    /**
     * Returns list of all tournament matches filtered by requested params
     */
    @Override
    public Page<TournamentMatchDto> getMatchList(Pageable pageable, long tournamentSeriesId, User user) {
        TournamentSeries tournamentSeries = restTournamentSeriesFacade.getVerifiedSeriesById(tournamentSeriesId);
        return tournamentMatchService.getMatchList(pageable, tournamentSeries).map(tournamentMatchMapper::toDto);
    }

    /**
     * Add new tournament match.
     */
    @CanManageTournament
    @Override
    public TournamentMatchDto addMatch(TournamentMatchDto tournamentMatchDto, User user) {

        tournamentMatchDto.setId(null);
        tournamentMatchDto.setStatus(TournamentStatusType.CREATED);

        TournamentMatch tournamentMatch = this.getVerifiedTournamentMatchByDto(tournamentMatchDto);
        tournamentMatch = tournamentMatchService.addMatch(tournamentMatch);

        if (isNull(tournamentMatch)) {
            log.error("!> error while creating tournament match from dto '{}' for user '{}'.", tournamentMatchDto, user);
            throw new TournamentManageException(ExceptionMessages.TOURNAMENT_MATCH_CREATION_ERROR,
                    "Tournament match was not saved on Portal. Check requested params.");
        }
        return tournamentMatchMapper.toDto(tournamentMatch);
    }

    /**
     * Edit tournament match.
     */
    @CanManageTournament
    @Override
    public TournamentMatchDto editMatch(long matchId, TournamentMatchDto tournamentMatchDto, User user) {
        if (isNull(tournamentMatchDto) || tournamentMatchDto.getId() != matchId) {
            log.warn("~ parameter 'tournamentMatchDto.id' is not match specified id in parameters for editMatch");
            throw new ValidationException(ExceptionMessages.TOURNAMENT_MATCH_VALIDATION_ERROR, "tournamentMatchDto.id",
                    "parameter 'tournamentMatchDto.id' is not match specified id in parameters for editMatch");
        }
        TournamentMatch tournamentMatch = this.getVerifiedTournamentMatchByDto(tournamentMatchDto);

        if (tournamentMatch.getStatus().isDeleted()) {
            log.warn("~ tournament match deleting was declined in editMatch. This operation should be done with specific method.");
            throw new TournamentManageException(ExceptionMessages.TOURNAMENT_MATCH_STATUS_DELETE_ERROR,
                    "Modifying tournament match was rejected. Check requested params and method.");
        }

        //Match can be finished only with setting the winner of the match
        if ((tournamentMatch.getStatus().isFinished() && isNull(tournamentMatch.getMatchWinner()))
                || (nonNull(tournamentMatch.getMatchWinner()) && !tournamentMatch.getStatus().isFinished())) {
            log.warn("~ tournament match can be finished only with setting the winner of the match. " +
                    "Request to set status '{}' and winner '{}' was rejected.", tournamentMatch.getStatus(), tournamentMatch.getMatchWinner());
            throw new TournamentManageException(ExceptionMessages.TOURNAMENT_MATCH_STATUS_FINISHED_ERROR,
                    "Modifying tournament match was rejected. Check requested params and method.");
        }

        tournamentMatch = tournamentMatchService.editMatch(tournamentMatch);
        if (isNull(tournamentMatch)) {
            log.error("!> error while editing tournament match from dto '{}' for user '{}'.", tournamentMatchDto, user);
            throw new TournamentManageException(ExceptionMessages.TOURNAMENT_MATCH_MODIFICATION_ERROR,
                    "Tournament series was not updated on Portal. Check requested params.");
        }
        return tournamentMatchMapper.toDto(tournamentMatch);
    }

    /**
     * Edit tournament match by rivals (set only winner of match and wonPlaceInSeries for rival).
     */
    @Override
    public TournamentMatchDto editMatchByRivals(long matchId, TournamentMatchDto tournamentMatchDto, User user) {
        if (isNull(tournamentMatchDto) || tournamentMatchDto.getId() != matchId) {
            log.warn("~ parameter 'tournamentMatchDto.id' is not match specified id in parameters for editMatch");
            throw new ValidationException(ExceptionMessages.TOURNAMENT_MATCH_VALIDATION_ERROR, "tournamentMatchDto.id",
                    "parameter 'tournamentMatchDto.id' is not match specified id in parameters for editMatch");
        }
        Set<ConstraintViolation<TournamentMatchDto>> settingsViolations = validator.validate(tournamentMatchDto);
        if (!settingsViolations.isEmpty()) {
            log.debug("^ transmitted tournament match dto: '{}' have constraint violations: '{}'",
                    tournamentMatchDto, settingsViolations);
            throw new ConstraintViolationException(settingsViolations);
        }

        //get current series by ID from DB
        TournamentMatch tournamentMatch = this.getVerifiedMatchById(tournamentMatchDto.getId());
        Boolean isMatchModifiableByRival = tournamentMatchService.isMatchModifiableByRival(tournamentMatch);
        if (isNull(isMatchModifiableByRival) || !isMatchModifiableByRival) {
            log.warn("~ tournament match can be modified by rivals. Tournament is not self-hosted. Request rejected.");
            throw new TournamentManageException(ExceptionMessages.TOURNAMENT_SERIES_MODIFICATION_ERROR,
                    "Modifying tournament match by rival was rejected. Tournament is not self-hosted. Check requested params.");
        }
        if (TournamentStatusType.finishedStatusList.contains(tournamentMatch.getStatus())) {
            log.warn("~ tournament match has finished, unable to modify by rival declined in editMatchByRivals.");
            throw new TournamentManageException(ExceptionMessages.TOURNAMENT_MATCH_MODIFICATION_ERROR,
                    "Modifying finished tournament match by rival was rejected. Check requested params and method.");
        }
        if (!tournamentMatchRivalService.isUserMatchRivalParticipant(tournamentMatch, user)) {
            log.warn("~ user is not actively participate in specified tournament match. Request to modify match is rejected.");
            throw new TournamentManageException(ExceptionMessages.TOURNAMENT_SERIES_MODIFICATION_ERROR,
                    "User is not actively participate in specified tournament match. Modifying tournament match was rejected.");
        }

        // check and compose match rival list
        List<TournamentMatchRivalDto> tournamentMatchRivalDtoList = tournamentMatchDto.getMatchRivalList();
        List<TournamentMatchRival> tournamentMatchRivalList = null;
        if (isNotEmpty(tournamentMatchRivalDtoList)) {
            tournamentMatchRivalList = tournamentMatchRivalDtoList.parallelStream()
                    .map(restTournamentMatchRivalFacade::getVerifiedMatchRivalByDtoForRival)
                    .peek(rival -> rival.setTournamentMatch(tournamentMatch))
                    .collect(Collectors.toList());
        }
        if (isNotEmpty(tournamentMatchRivalList)) {
            tournamentMatch.setMatchRivalList(tournamentMatchRivalList);
        }

        // check and compose match rival winner
        TournamentMatchRivalDto matchWinnerDto = tournamentMatchDto.getMatchWinner();
        TournamentMatchRival matchWinner = tournamentMatch.getMatchWinner();
        if (isNotEmpty(tournamentMatchRivalList)) {
            //try to find series winner (first place)
            matchWinner = tournamentMatchRivalList.parallelStream()
                    .filter(s -> nonNull(s.getWonPlaceInMatch())
                            && s.getWonPlaceInMatch().isWinner())
                    .findFirst().orElse(null);
        } else if (nonNull(matchWinnerDto)) {
            matchWinnerDto.setWonPlaceInMatch(TournamentWinnerPlaceType.FIRST);
            if (isNull(matchWinnerDto.getId())) {
                log.warn("~ parameter 'matchWinnerDto.id' is NULL for editMatchByRivals");
                throw new ValidationException(ExceptionMessages.TOURNAMENT_MATCH_RIVAL_VALIDATION_ERROR, "matchWinnerDto.id",
                        "parameter 'matchWinnerDto.id' is not set for add or modify tournament match");
            }
            matchWinner = restTournamentMatchRivalFacade.getVerifiedMatchRivalByDtoForRival(matchWinnerDto);
        }

        if (nonNull(matchWinner)) {
            tournamentMatch.setMatchWinner(matchWinner);
        }

        TournamentMatch savedMatch = tournamentMatchService.editMatch(tournamentMatch);
        if (isNull(savedMatch)) {
            log.error("!> error while editing tournament match from dto '{}' for user '{}'.", tournamentMatchDto, user);
            throw new TournamentManageException(ExceptionMessages.TOURNAMENT_MATCH_MODIFICATION_ERROR,
                    "Tournament series was not updated on Portal. Check requested params.");
        }
        return tournamentMatchMapper.toDto(savedMatch);
    }

    /**
     * Mark 'deleted' tournament matches in DB.
     */
    @CanManageTournament
    @Override
    public TournamentMatchDto deleteMatch(long matchId, User user) {
        TournamentMatch tournamentMatch = this.getVerifiedMatchById(matchId);
        tournamentMatch = tournamentMatchService.deleteMatch(tournamentMatch);

        if (isNull(tournamentMatch)) {
            log.error("!> error while deleting tournament match with id '{}' for user '{}'.", matchId, user);
            throw new TournamentManageException(ExceptionMessages.TOURNAMENT_MATCH_MODIFICATION_ERROR,
                    "Tournament match was not deleted on Portal. Check requested params.");
        }
        return tournamentMatchMapper.toDto(tournamentMatch);
    }

    /**
     * Returns tournament match by DTO, with validation, business logic and user with privacy check
     */
    @Override
    public TournamentMatch getVerifiedTournamentMatchByDto(TournamentMatchDto tournamentMatchDto) {

        if (isNull(tournamentMatchDto)) {
            log.warn("~ parameter 'tournamentMatchDto' is NULL for getVerifiedTournamentMatchByDto");
            throw new ValidationException(ExceptionMessages.TOURNAMENT_MATCH_VALIDATION_ERROR, "tournamentSeriesDto",
                    "parameter 'tournamentMatchDto' is not set for get or modify tournament match");
        }
        Set<ConstraintViolation<TournamentMatchDto>> settingsViolations = validator.validate(tournamentMatchDto);
        if (!settingsViolations.isEmpty()) {
            log.debug("^ transmitted tournament match dto: '{}' have constraint violations: '{}'",
                    tournamentMatchDto, settingsViolations);
            throw new ConstraintViolationException(settingsViolations);
        }
        TournamentMatch tournamentMatch = tournamentMatchMapper.fromDto(tournamentMatchDto);

        //check and set series to match with verification
        TournamentSeries tournamentSeries = restTournamentSeriesFacade.getVerifiedSeriesById(
                tournamentMatchDto.getTournamentSeriesId());
        //Check existence of specified by id of tournament match and it's status
        if (nonNull(tournamentMatchDto.getId())) {
            TournamentMatch existedTournamentMatch =
                    this.getVerifiedMatchById(tournamentMatchDto.getId());
            if (!existedTournamentMatch.getTournamentSeries().equals(tournamentSeries)) {
                log.warn("~ parameter 'tournamentMatchDto.tournamentSeriesId' is not equals TournamentSeries that was saved previously in DB. " +
                        "Request denied in getVerifiedTournamentMatchByDto");
                throw new ValidationException(ExceptionMessages.TOURNAMENT_MATCH_VALIDATION_ERROR, "tournamentSeriesId",
                        "parameter 'tournamentMatchDto.tournamentSeriesId' is not equals TournamentSeries that was saved previously in DB. " +
                                "Request denied in getVerifiedTournamentMatchByDto");
            }
        }
        tournamentMatch.setTournamentSeries(tournamentSeries);

        // check and compose match rival list
        List<TournamentMatchRivalDto> tournamentMatchRivalDtoList = tournamentMatchDto.getMatchRivalList();
        List<TournamentMatchRival> tournamentMatchRivalList = null;
        if (isNotEmpty(tournamentMatchRivalDtoList)) {
            tournamentMatchRivalList = tournamentMatchRivalDtoList.parallelStream()
                    .map(restTournamentMatchRivalFacade::getVerifiedMatchRivalByDto)
                    .map(rival -> restTournamentMatchRivalFacade.setGameIndicatorMultipliersToMatchRival(rival, tournamentSeries))
                    .peek(rival -> rival.setTournamentMatch(tournamentMatch))
                    .collect(Collectors.toList());
        }
        tournamentMatch.setMatchRivalList(tournamentMatchRivalList);

        // check and compose match rival winner
        TournamentMatchRivalDto matchWinnerDto = tournamentMatchDto.getMatchWinner();
        TournamentMatchRival matchWinner = null;
        if (nonNull(matchWinnerDto)) {
            matchWinnerDto.setWonPlaceInMatch(TournamentWinnerPlaceType.FIRST);
            if (isNull(matchWinnerDto.getId())) {
                log.warn("~ parameter 'matchWinnerDto.id' is NULL for getVerifiedTournamentMatchByDto");
                throw new ValidationException(ExceptionMessages.TOURNAMENT_MATCH_RIVAL_VALIDATION_ERROR, "matchWinnerDto.id",
                        "parameter 'matchWinnerDto.id' is not set for get or modify tournament match");
            }
            matchWinner = restTournamentMatchRivalFacade.setGameIndicatorMultipliersToMatchRival(
                    restTournamentMatchRivalFacade.getVerifiedMatchRivalByDto(matchWinnerDto),
                    tournamentSeries
            );
        } else if (isNotEmpty(tournamentMatchRivalList)) {
            //try to find series winner (first place)
            matchWinner = tournamentMatchRivalList.parallelStream()
                    .filter(s -> nonNull(s.getWonPlaceInMatch())
                            && s.getWonPlaceInMatch().isWinner())
                    .findFirst().orElse(null);
        }
        if (nonNull(matchWinner)) {
            tournamentMatch.setMatchWinner(matchWinner);
        }

        return tournamentMatch;
    }

    /**
     * Returns tournament match by id and user with privacy check
     */
    @Override
    public TournamentMatch getVerifiedMatchById(long id) {
        TournamentMatch tournamentMatch = tournamentMatchService.getMatch(id);
        if (isNull(tournamentMatch)) {
            log.debug("^ Tournament match with requested id '{}' was not found. 'getVerifiedMatchById' in RestTournamentMatchService request denied", id);
            throw new TeamManageException(ExceptionMessages.TOURNAMENT_MATCH_NOT_FOUND_ERROR, "Tournament match with requested id " + id + " was not found");
        }
        if (tournamentMatch.getStatus().isDeleted()) {
            log.debug("^ Tournament match with requested id '{}' was '{}'. 'getVerifiedMatchById' in RestTournamentMatchService request denied", id, tournamentMatch.getStatus());
            throw new TeamManageException(ExceptionMessages.TOURNAMENT_MATCH_DISABLE_ERROR, "Active tournament match with requested id " + id + " was not found");
        }
        return tournamentMatch;
    }
}

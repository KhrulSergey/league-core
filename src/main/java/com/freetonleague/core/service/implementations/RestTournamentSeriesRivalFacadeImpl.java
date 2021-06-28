package com.freetonleague.core.service.implementations;

import com.freetonleague.core.domain.dto.TournamentSeriesRivalDto;
import com.freetonleague.core.domain.model.TournamentSeries;
import com.freetonleague.core.domain.model.TournamentSeriesRival;
import com.freetonleague.core.domain.model.TournamentTeamProposal;
import com.freetonleague.core.domain.model.User;
import com.freetonleague.core.exception.TeamManageException;
import com.freetonleague.core.exception.TournamentManageException;
import com.freetonleague.core.exception.ValidationException;
import com.freetonleague.core.exception.config.ExceptionMessages;
import com.freetonleague.core.mapper.TournamentSeriesRivalMapper;
import com.freetonleague.core.security.permissions.CanManageTournament;
import com.freetonleague.core.service.RestTournamentProposalFacade;
import com.freetonleague.core.service.RestTournamentSeriesFacade;
import com.freetonleague.core.service.RestTournamentSeriesRivalFacade;
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
 * Service-facade for managing tournament series rival
 */
@Slf4j
@RequiredArgsConstructor
@Service
public class RestTournamentSeriesRivalFacadeImpl implements RestTournamentSeriesRivalFacade {

    private final TournamentSeriesService tournamentSeriesService;
    private final TournamentSeriesRivalMapper seriesRivalMapper;
    private final Validator validator;

    @Lazy
    @Autowired
    private RestTournamentSeriesFacade restTournamentSeriesFacade;

    @Lazy
    @Autowired
    private RestTournamentProposalFacade restTournamentProposalFacade;

    /**
     * Delete tournament series rival.
     */
    @CanManageTournament
    @Override
    public void deleteSeriesRival(long id, User user) {
        TournamentSeriesRival tournamentSeriesRival = this.getVerifiedSeriesRivalById(id);
        boolean result = tournamentSeriesService.deleteSeriesRival(tournamentSeriesRival);
        if (!result) {
            log.debug("^ Tournament rival with requested id '{}' was not deleted. 'deleteSeriesRival' in " +
                    "RestTournamentSeriesRivalFacade request denied", id);
            throw new TeamManageException(ExceptionMessages.TOURNAMENT_SERIES_RIVAL_MODIFICATION_ERROR,
                    "Tournament series rival with requested id " + id + " was not deleted");
        }
    }

    /**
     * Returns tournament series rival by id with privacy check
     */
    @Override
    public TournamentSeriesRival getVerifiedSeriesRivalById(long id) {
        TournamentSeriesRival tournamentSeriesRival = tournamentSeriesService.getSeriesRival(id);
        if (isNull(tournamentSeriesRival)) {
            log.debug("^ Tournament rival with requested id '{}' was not found. 'getVerifiedSeriesRivalById' in " +
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
                    "parameter 'seriesRivalDto' is not set for add or modify tournament series rival");
        }
        Set<ConstraintViolation<TournamentSeriesRivalDto>> settingsViolations = validator.validate(seriesRivalDto);
        if (!settingsViolations.isEmpty()) {
            log.debug("^ transmitted tournament series rival dto: '{}' have constraint violations: '{}'",
                    seriesRivalDto, settingsViolations);
            throw new ConstraintViolationException(settingsViolations);
        }
        boolean isRivalWasAFK = seriesRivalDto.getStatus().isAFK();
        boolean isRivalHasNoneWinnerPlace = seriesRivalDto.getWonPlaceInSeries().isNone();
        if (isRivalWasAFK && !isRivalHasNoneWinnerPlace) {
            log.warn("~ tournament series rival.id '{}' can be set AFK status only with setting WonPlaceInSeries = NONE'. " +
                    "Request for verify rival was rejected.", seriesRivalDto.getId());
            throw new TournamentManageException(ExceptionMessages.TOURNAMENT_MATCH_RIVAL_VALIDATION_ERROR,
                    "Tournament series rival.id '{}' can be set AFK status only with setting WonPlaceInSeries = NONE. Check requested params and method.");
        }

        TournamentSeriesRival tournamentSeriesRival;
        if (nonNull(seriesRivalDto.getId())) {
            tournamentSeriesRival = this.getVerifiedSeriesRivalById(seriesRivalDto.getId());
            if (!seriesRivalDto.getTournamentSeriesId().equals(tournamentSeriesRival.getTournamentSeries().getId())) {
                log.warn("~ parameter 'SeriesRivalDto.tournamentSeriesId' isn't fit existed ref from SeriesRival to Series. " +
                        "Request to change reference from SeriesRival to other Series is prohibited in getVerifiedSeriesRivalByDto");
                throw new ValidationException(ExceptionMessages.TOURNAMENT_SERIES_RIVAL_VALIDATION_ERROR, "SeriesRivalDto.tournamentSeriesId",
                        "parameter 'tournamentSeriesId' isn't fit existed ref from SeriesRival to Series for getVerifiedSeriesRivalByDto");
            }
            tournamentSeriesRival.setStatus(seriesRivalDto.getStatus());
            tournamentSeriesRival.setWonPlaceInSeries(seriesRivalDto.getWonPlaceInSeries());
            tournamentSeriesRival.setSeriesIndicatorList(seriesRivalDto.getSeriesIndicatorList());
        } else {
            tournamentSeriesRival = seriesRivalMapper.fromDto(seriesRivalDto);
            TournamentTeamProposal teamProposal = restTournamentProposalFacade.getVerifiedTeamProposalById(seriesRivalDto.getTeamProposalId());
            tournamentSeriesRival.setTeamProposal(teamProposal);

            if (nonNull(seriesRivalDto.getTournamentSeriesId())) {
                TournamentSeries tournamentSeries = restTournamentSeriesFacade.getVerifiedSeriesById(seriesRivalDto.getTournamentSeriesId());
                tournamentSeriesRival.setTournamentSeries(tournamentSeries);
            }

            if (nonNull(seriesRivalDto.getParentTournamentSeriesId())) {
                TournamentSeries parentTournamentSeries = restTournamentSeriesFacade.getVerifiedSeriesById(seriesRivalDto.getParentTournamentSeriesId());
                tournamentSeriesRival.setParentTournamentSeries(parentTournamentSeries);
            }
        }

        return tournamentSeriesRival;
    }

    /**
     * Returns tournament series rival by dto with privacy check for modifying by rivals
     */
    @Override
    public TournamentSeriesRival getVerifiedSeriesRivalByDtoForRival(TournamentSeriesRivalDto seriesRivalDto) {
        if (isNull(seriesRivalDto)) {
            log.warn("~ parameter 'seriesRivalDto' is NULL for getVerifiedSeriesRivalByDtoForRival");
            throw new ValidationException(ExceptionMessages.TOURNAMENT_SERIES_RIVAL_VALIDATION_ERROR, "seriesRivalDto",
                    "parameter 'seriesRivalDto' is not set for modify tournament series rival");
        }
        if (isNull(seriesRivalDto.getId())) {
            log.warn("~ parameter 'seriesRivalDto.id' is NULL for getVerifiedSeriesRivalByDtoForRival");
            throw new ValidationException(ExceptionMessages.TOURNAMENT_SERIES_RIVAL_VALIDATION_ERROR, "seriesRivalDto.id",
                    "parameter 'seriesRivalDto.id' is not set for modify tournament series rival");
        }
        Set<ConstraintViolation<TournamentSeriesRivalDto>> settingsViolations = validator.validate(seriesRivalDto);
        if (!settingsViolations.isEmpty()) {
            log.debug("^ transmitted tournament series rival dto: '{}' have constraint violations: '{}'",
                    seriesRivalDto, settingsViolations);
            throw new ConstraintViolationException(settingsViolations);
        }
        TournamentSeriesRival tournamentSeriesRival = this.getVerifiedSeriesRivalById(seriesRivalDto.getId());
        if (!seriesRivalDto.getTournamentSeriesId().equals(tournamentSeriesRival.getTournamentSeries().getId())) {
            log.warn("~ parameter 'SeriesRivalDto.tournamentSeriesId' isn't fit existed ref from SeriesRival to Series. " +
                    "Request to change reference from SeriesRival to other Series is prohibited in getVerifiedSeriesRivalByDto");
            throw new ValidationException(ExceptionMessages.TOURNAMENT_SERIES_RIVAL_VALIDATION_ERROR, "SeriesRivalDto.tournamentSeriesId",
                    "parameter 'tournamentSeriesId' isn't fit existed ref from SeriesRival to Series for getVerifiedSeriesRivalByDto");
        }
        tournamentSeriesRival.setWonPlaceInSeries(seriesRivalDto.getWonPlaceInSeries());
        return tournamentSeriesRival;
    }
}

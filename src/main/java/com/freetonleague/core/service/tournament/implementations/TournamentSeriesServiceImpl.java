package com.freetonleague.core.service.tournament.implementations;


import com.freetonleague.core.domain.enums.tournament.TournamentStatusType;
import com.freetonleague.core.domain.enums.tournament.TournamentWinnerPlaceType;
import com.freetonleague.core.domain.model.User;
import com.freetonleague.core.domain.model.tournament.*;
import com.freetonleague.core.exception.CustomUnexpectedException;
import com.freetonleague.core.exception.config.ExceptionMessages;
import com.freetonleague.core.repository.tournament.TournamentSeriesRepository;
import com.freetonleague.core.repository.tournament.TournamentSeriesRivalRepository;
import com.freetonleague.core.service.tournament.TournamentEventService;
import com.freetonleague.core.service.tournament.TournamentGenerator;
import com.freetonleague.core.service.tournament.TournamentMatchService;
import com.freetonleague.core.service.tournament.TournamentSeriesService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

import javax.validation.ConstraintViolation;
import javax.validation.Validator;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;
import static org.apache.commons.lang3.BooleanUtils.isTrue;

@Slf4j
@RequiredArgsConstructor
@Service
public class TournamentSeriesServiceImpl implements TournamentSeriesService {

    private final TournamentSeriesRepository tournamentSeriesRepository;
    private final TournamentSeriesRivalRepository tournamentSeriesRivalRepository;
    private final TournamentMatchService tournamentMatchService;
    private final TournamentEventService tournamentEventService;
    private final Validator validator;

    @Autowired
    @Qualifier("singleEliminationGenerator")
    private TournamentGenerator singleEliminationGenerator;
    @Autowired
    @Qualifier("doubleEliminationGenerator")
    private TournamentGenerator doubleEliminationGenerator;
    @Autowired
    @Qualifier("survivalEliminationGenerator")
    private TournamentGenerator survivalEliminationGenerator;

    /**
     * Returns founded tournament series by id
     */
    @Override
    public TournamentSeries getSeries(long id) {
        log.debug("^ trying to get tournament series by id: '{}'", id);
        return tournamentSeriesRepository.findById(id).orElse(null);
    }

    /**
     * Returns list of all tournament series filtered by requested params
     */
    @Override
    public Page<TournamentSeries> getSeriesList(Pageable pageable, TournamentRound tournamentRound) {
        if (isNull(pageable) || isNull(tournamentRound)) {
            log.error("!> requesting getSeriesList for NULL pageable '{}' or NULL tournamentRound '{}'. Check evoking clients",
                    pageable, tournamentRound);
            return null;
        }
        log.debug("^ trying to get tournament series list with pageable params: '{}' and by tournament round id '{}'", pageable,
                tournamentRound.getId());
        return tournamentSeriesRepository.findAllByTournamentRound(pageable, tournamentRound);
    }

    /**
     * Add new tournament series to DB.
     */
    @Override
    public TournamentSeries addSeries(TournamentSeries tournamentSeries) {
        if (!this.verifyTournamentSeries(tournamentSeries, true)) {
            return null;
        }
        log.debug("^ trying to add new tournament series '{}'", tournamentSeries);
        return tournamentSeriesRepository.save(tournamentSeries);
    }

    /**
     * Generate tournament match (OMT) for specified series and returns updated series.
     */
    @Override
    public TournamentSeries generateOmtForSeries(TournamentSeries tournamentSeries) {
        if (isNull(tournamentSeries)) {
            log.error("!> requesting generateOmtForSeries for NULL tournamentSeries. Check evoking clients");
            return null;
        }
        Tournament tournament = tournamentSeries.getTournamentRound().getTournament();
        TournamentMatch OmtMatch = null;
        switch (tournament.getSystemType()) {
            case SINGLE_ELIMINATION:
                OmtMatch = singleEliminationGenerator.generateOmtForSeries(tournamentSeries);
                break;
            case DOUBLE_ELIMINATION:
                OmtMatch = doubleEliminationGenerator.generateOmtForSeries(tournamentSeries);
                break;
            case SURVIVAL_ELIMINATION:
                OmtMatch = survivalEliminationGenerator.generateOmtForSeries(tournamentSeries);
                break;
            default:
                break;
        }
        if (isNull(OmtMatch)) {
            log.error("!> error while generateOmtForSeries. Check stack trace");
            return null;
        }
        List<TournamentMatch> matchList = tournamentSeries.getMatchList();
        matchList.add(OmtMatch);
        tournamentSeries.setMatchList(matchList);
        log.debug("^ trying to save updated series with embedded new match '{}'", tournamentSeries);
        return tournamentSeriesRepository.save(tournamentSeries);
    }

    /**
     * Compose new matches and rivals for next round (fill existed prototypes of series).
     */
    @Override
    public boolean composeSequentialSeriesForPrevSeries(TournamentSeries prevTournamentSeries) {
        if (isNull(prevTournamentSeries)) {
            log.error("!> requesting composeSeriesRivalForTournamentSeries for NULL prevTournamentSeries. Check evoking clients");
            return false;
        }

        Tournament tournament = prevTournamentSeries.getTournamentRound().getTournament();
        if (!tournament.getTournamentSettings().getIsSequentialSeriesEnabled()) {
            log.error("!> requesting composeSeriesRivalForTournamentSeries for tournament with " +
                    "IsSequentialSeriesOn = false for series '{}'. Check evoking clients", prevTournamentSeries);
            return false;
        }
        log.debug("^ trying to compose and save next series for tournament.id {} for prevSeries '{}'. Composing series with rivals and matches",
                tournament.getId(), prevTournamentSeries);
        TournamentSeries tournamentSeries = null;
        switch (tournament.getSystemType()) {
            case SINGLE_ELIMINATION:
                tournamentSeries = singleEliminationGenerator.composeRivalForChildTournamentSeries(prevTournamentSeries);
                break;
            case DOUBLE_ELIMINATION:
                tournamentSeries = doubleEliminationGenerator.composeRivalForChildTournamentSeries(prevTournamentSeries);
                break;
            case SURVIVAL_ELIMINATION:
                tournamentSeries = survivalEliminationGenerator.composeRivalForChildTournamentSeries(prevTournamentSeries);
                break;
            default:
                break;
        }
        if (isNull(tournamentSeries)) {
            log.error("!> next series generation with composeSeriesRivalForTournamentSeries caused error. tournamentSeries is NULL. Check stack trace");
            throw new CustomUnexpectedException(ExceptionMessages.TOURNAMENT_SERIES_GENERATION_ERROR);
        }

        tournamentSeries = this.editSeries(tournamentSeries);
        return nonNull(tournamentSeries);
    }


    /**
     * Edit tournament series in DB.
     */
    @Transactional(isolation = Isolation.SERIALIZABLE)
    @Override
    public TournamentSeries editSeries(TournamentSeries tournamentSeries) {
        boolean checkEmbeddedMatchList = true;
        if (isNull(tournamentSeries.getId())) {
            log.error("!> requesting modify tournament series '{}' for non-existed tournament series. Check evoking clients",
                    tournamentSeries.getId());
            return null;
        }
        log.debug("^ trying to modify tournament series '{}'", tournamentSeries);
        if (tournamentSeries.getStatus().isFinished()) {
            tournamentSeries.setFinishedDate(LocalDateTime.now());
            if (!isTrue(tournamentSeries.getHasNoWinner()) && isNull(tournamentSeries.getSeriesWinner())) {
                TournamentSeriesRival seriesWinner = this.getCalculatedSeriesWinner(tournamentSeries);
                if (isNull(seriesWinner)) {
                    log.warn("~ modifying tournament series id '{}' with warning: Series winner was not defined or calculated.",
                            tournamentSeries.getId());
                    tournamentEventService.processSeriesHasNoWinner(tournamentSeries);
                    tournamentSeries.setHasNoWinner(true);
                } else {
                    tournamentSeries.setHasNoWinner(false);
                }
                tournamentSeries.setSeriesWinner(seriesWinner);
            }
            checkEmbeddedMatchList = false;
        }
        if (!this.verifyTournamentSeries(tournamentSeries, checkEmbeddedMatchList)) {
            return null;
        }
        TournamentStatusType prevStatus = tournamentSeries.getPrevStatus();
        tournamentSeries = tournamentSeriesRepository.saveAndFlush(tournamentSeries);
        tournamentSeries.setPrevStatus(prevStatus);
        if (tournamentSeries.isStatusChanged()) {
            this.handleTournamentSeriesStatusChanged(tournamentSeries);
        }
        return tournamentSeries;
    }

    /**
     * Mark 'deleted' tournament series in DB.
     */
    @Override
    public TournamentSeries deleteSeries(TournamentSeries tournamentSeries) {
        if (!this.verifyTournamentSeries(tournamentSeries, false)) {
            return null;
        }
        if (!this.isExistsTournamentSeriesById(tournamentSeries.getId())) {
            log.error("!> requesting delete tournament series for non-existed tournament series. Check evoking clients");
            return null;
        }
        log.debug("^ trying to set 'deleted' mark to tournament series '{}'", tournamentSeries);
        tournamentSeries.setStatus(TournamentStatusType.DELETED);
        tournamentSeries = tournamentSeriesRepository.save(tournamentSeries);
        this.handleTournamentSeriesStatusChanged(tournamentSeries);
        return tournamentSeries;
    }

    /**
     * Returns sign of tournament series existence for specified id.
     */
    @Override
    public boolean isExistsTournamentSeriesById(long id) {
        return tournamentSeriesRepository.existsById(id);
    }

    /**
     * Returns sign of all series for round was finished.
     */
    @Override
    public boolean isAllSeriesFinishedByRound(TournamentRound tournamentRound) {
        return tournamentRound.getSeriesList().parallelStream()
                .map(TournamentSeries::getStatus).allMatch(TournamentStatusType.finishedStatusList::contains);
    }

    /**
     * Returns founded tournament series rival by id
     */
    @Override
    public TournamentSeriesRival getSeriesRival(long id) {
        return tournamentSeriesRivalRepository.findById(id).orElse(null);
    }

    /**
     * Returns sign if user is tournament series rival participant
     */
    @Override
    public boolean isUserSeriesRivalParticipant(TournamentSeries tournamentSeries, User user) {
        return tournamentSeriesRivalRepository.isUserParticipateInSeries(tournamentSeries, user.getLeagueId());
    }

    /**
     * Returns sign if tournament series can be modified by series rival participant
     */
    @Override
    public Boolean isSeriesModifiableByRival(TournamentSeries tournamentSeries) {
        return tournamentSeriesRepository.isSeriesModifiableByRival(tournamentSeries);
    }

    /**
     * Delete tournament series rival by id
     */
    @Override
    public boolean deleteSeriesRival(TournamentSeriesRival tournamentSeriesRival) {
        if (isNull(tournamentSeriesRival) || isNull(tournamentSeriesRival.getId())) {
            log.error("!> requesting delete series rival for NULL data (id). Check evoking clients");
            return false;
        }
        if (!this.isExistsSeriesRivalById(tournamentSeriesRival.getId())) {
            log.error("!> requesting delete series rival for non-existed entry. Check evoking clients");
            return false;
        }
        log.debug("^ trying to delete tournament series rival '{}'", tournamentSeriesRival);
        tournamentSeriesRivalRepository.delete(tournamentSeriesRival);
        return true;
    }

    //TODO calculate all winners of series (from 1 to 8 place)
    private TournamentSeriesRival getCalculatedSeriesWinner(TournamentSeries tournamentSeries) {
        log.debug("^ trying to calculate winner for series.id '{}'", tournamentSeries.getId());
        List<TournamentMatch> finishedMatchList = tournamentSeries.getMatchList().parallelStream()
                .filter(m -> TournamentStatusType.finishedStatusList.contains(m.getStatus()))
                .collect(Collectors.toList());
        if (finishedMatchList.size() != tournamentSeries.getMatchList().size()) {
            log.error("!> requesting calculated series winner for not all match finished or declined in series.id: '{}'. " +
                    "Check evoking clients", tournamentSeries.getId());
            return null;
        }
        Map<TournamentTeamProposal, Long> matchRivalWinnerMap = finishedMatchList.parallelStream()
                .filter(Predicate.not(TournamentMatch::getHasNoWinner))
                .map(m -> tournamentMatchService.getMatchWinner(m, false))
                .collect(Collectors.groupingBy(TournamentMatchRival::getTeamProposal, Collectors.counting()));

        TournamentTeamProposal seriesWinnerProposal = null;
        long maxScore = 0;
        for (Map.Entry<TournamentTeamProposal, Long> entry : matchRivalWinnerMap.entrySet()) {
            if (maxScore < entry.getValue()) {
                maxScore = entry.getValue();
                seriesWinnerProposal = entry.getKey();
            } else if (maxScore == entry.getValue()) {
                //if score is the same, than we don't have a rival with advantage, so we have no winner
                seriesWinnerProposal = null;
            }
        }

        if (isNull(seriesWinnerProposal)) {
            log.warn("!> tournament series id '{}' has no rival with advantage score. Series has no winner",
                    tournamentSeries.getId());
            return null;
        }

        TournamentSeriesRival seriesWinner = tournamentSeriesRivalRepository.findByTournamentSeriesAndTeamProposal(
                tournamentSeries, seriesWinnerProposal);
        if (isNull(seriesWinner)) {
            log.error("!> requesting calculateSeriesWinner tournament series winner with tournamentTeamProposal.id '{}' " +
                            "for non-existed reference to TournamentSeriesRival. Check evoking clients",
                    seriesWinnerProposal.getId());
            return null;
        }
        seriesWinner.setWonPlaceInSeries(TournamentWinnerPlaceType.FIRST);
        log.debug("^ calculated winner for series.id '{}' is rival.id '{}' with proposal.id '{}'", tournamentSeries.getId(),
                seriesWinner.getId(), seriesWinner.getTeamProposal().getId());
        return seriesWinner;
    }

    /**
     * Validate tournament parameters and settings to modify
     */
    private boolean verifyTournamentSeries(TournamentSeries tournamentSeries, boolean checkEmbeddedMatchList) {
        if (isNull(tournamentSeries)) {
            log.error("!> requesting modify tournament series with verifyTournamentSeries for NULL tournamentSeries. Check evoking clients");
            return false;
        }
        Set<ConstraintViolation<TournamentSeries>> violations = validator.validate(tournamentSeries);
        if (!violations.isEmpty()) {
            log.error("!> requesting modify tournament series '{}' with verifyTournamentSeries for tournament series with ConstraintViolations. Check evoking clients",
                    tournamentSeries.getId());
            return false;
        }
        List<TournamentMatch> tournamentMatches = tournamentSeries.getMatchList();
        if (nonNull(tournamentMatches) && checkEmbeddedMatchList) {
            for (TournamentMatch match : tournamentMatches) {
                if (!tournamentMatchService.verifyTournamentMatch(match)) {
                    log.error("!> requesting modify tournament series '{}' with verifyTournamentSeries for tournament match with ConstraintViolations. Check evoking clients",
                            tournamentSeries.getId());
                    return false;
                }
            }
        }
        return true;
    }

    /**
     * Returns sign of series rival existence for specified id.
     */
    private boolean isExistsSeriesRivalById(long id) {
        return tournamentSeriesRivalRepository.existsById(id);

    }

    /**
     * Prototype for handle tournament status
     */
    private void handleTournamentSeriesStatusChanged(TournamentSeries tournamentSeries) {
        log.warn("~ status for tournament series id '{}' was changed from '{}' to '{}' ",
                tournamentSeries.getId(), tournamentSeries.getPrevStatus(), tournamentSeries.getStatus());
        tournamentEventService.processSeriesStatusChange(tournamentSeries, tournamentSeries.getStatus());
        tournamentSeries.setPrevStatus(tournamentSeries.getStatus());
    }
}

package com.freetonleague.core.service.implementations;


import com.freetonleague.core.domain.enums.TournamentMatchRivalParticipantStatusType;
import com.freetonleague.core.domain.enums.TournamentStatusType;
import com.freetonleague.core.domain.enums.TournamentWinnerPlaceType;
import com.freetonleague.core.domain.model.*;
import com.freetonleague.core.repository.TournamentSeriesRepository;
import com.freetonleague.core.service.TournamentMatchService;
import com.freetonleague.core.service.TournamentSeriesService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import javax.validation.ConstraintViolation;
import javax.validation.Validator;
import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;

@Slf4j
@RequiredArgsConstructor
@Service
public class TournamentSeriesServiceImpl implements TournamentSeriesService {

    private final TournamentSeriesRepository tournamentSeriesRepository;
    private final TournamentMatchService tournamentMatchService;
    private final Validator validator;

    /**
     * Returns founded tournament series by id
     */
    @Override
    public TournamentSeries getSeries(long id) {
        log.debug("^ trying to get tournament series by id: {}", id);
        return tournamentSeriesRepository.findById(id).orElse(null);
    }

    /**
     * Returns list of all tournament series filtered by requested params
     */
    @Override
    public Page<TournamentSeries> getSeriesList(Pageable pageable, TournamentRound tournamentRound) {
        if (isNull(pageable) || isNull(tournamentRound)) {
            log.error("!> requesting getSeriesList for NULL pageable {} or NULL tournamentRound {}. Check evoking clients",
                    pageable, tournamentRound);
            return null;
        }
        log.debug("^ trying to get tournament series list with pageable params: {} and by tournament round id {}", pageable,
                tournamentRound.getId());
        return tournamentSeriesRepository.findAllByTournamentRound(pageable, tournamentRound);
    }

    /**
     * Add new tournament series to DB.
     */
    @Override
    public TournamentSeries addSeries(TournamentSeries tournamentSeries) {
        if (!this.verifyTournamentSeries(tournamentSeries)) {
            return null;
        }
        log.debug("^ trying to add new tournament series {}", tournamentSeries);
        return tournamentSeriesRepository.save(tournamentSeries);
    }


    /**
     * Edit tournament series in DB.
     */
    @Override
    public TournamentSeries editSeries(TournamentSeries tournamentSeries) {
        if (!this.verifyTournamentSeries(tournamentSeries)) {
            return null;
        }
        if (!this.isExistsTournamentSeriesById(tournamentSeries.getId())) {
            log.error("!> requesting modify tournament series {} for non-existed tournament series. Check evoking clients",
                    tournamentSeries.getId());
            return null;
        }
        log.debug("^ trying to modify tournament series {}", tournamentSeries);
        if (tournamentSeries.getStatus().isFinished()) {
            tournamentSeries.setFinishedDate(LocalDateTime.now());
            tournamentSeries.setSeriesWinner(calculateSeriesWinner(tournamentSeries));
        }
        if (tournamentSeries.isStatusChanged()) {
            this.handleTournamentSeriesStatusChanged(tournamentSeries);
        }
        return tournamentSeriesRepository.save(tournamentSeries);
    }

    private TournamentSeriesRival calculateSeriesWinner(TournamentSeries tournamentSeries) {
        Map<TournamentTeamProposal, Long> matchRivalWinnerMap = tournamentSeries.getMatchList().parallelStream()
                .filter(m -> m.getStatus().isFinished()).map(TournamentMatch::getMatchWinner)
                .collect(Collectors.groupingBy(TournamentMatchRival::getTeamProposal, Collectors.counting()));

        Long max = matchRivalWinnerMap.values().stream().max(Comparator.naturalOrder()).orElse(null);
        if (isNull(max)) {
            log.error("!> requesting calculateSeriesWinner tournament series winner {} for non-existed rival with advantage score. Check evoking clients",
                    tournamentSeries.getId());
            return null;
        }
        Map.Entry<TournamentTeamProposal, Long> matchRivalWinnerEntry = tournamentSeries.getMatchList().parallelStream()
                .filter(m -> m.getStatus().isFinished())
                .map(TournamentMatch::getMatchWinner)
                .collect(Collectors.groupingBy(TournamentMatchRival::getTeamProposal, Collectors.counting()))
                .entrySet()
                .stream()
                .max(Map.Entry.comparingByValue()).orElse(null);

        if (isNull(matchRivalWinnerEntry) || isNull(matchRivalWinnerEntry.getKey())) {
            log.error("!> requesting calculateSeriesWinner tournament series winner {} for non-existed rival with advantage score. Check evoking clients",
                    tournamentSeries.getId());
            return null;
        }
        return TournamentSeriesRival.builder()
                .parentTournamentSeries(tournamentSeries)
                .tournamentSeries(null)
                .wonPlaceInSeries(TournamentWinnerPlaceType.FIRST)
                .status(TournamentMatchRivalParticipantStatusType.ACTIVE)
                .teamProposal(matchRivalWinnerEntry.getKey())
                .build();
    }

    /**
     * Mark 'deleted' tournament series in DB.
     */
    @Override
    public TournamentSeries deleteSeries(TournamentSeries tournamentSeries) {
        if (!this.verifyTournamentSeries(tournamentSeries)) {
            return null;
        }
        if (!this.isExistsTournamentSeriesById(tournamentSeries.getId())) {
            log.error("!> requesting delete tournament series for non-existed tournament series. Check evoking clients");
            return null;
        }
        log.debug("^ trying to set 'deleted' mark to tournament series {}", tournamentSeries);
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
     * Validate tournament parameters and settings to modify
     */
    private boolean verifyTournamentSeries(TournamentSeries tournamentSeries) {
        if (isNull(tournamentSeries)) {
            log.error("!> requesting modify tournament series with verifyTournamentSeries for NULL tournamentSeries. Check evoking clients");
            return false;
        }
        Set<ConstraintViolation<TournamentSeries>> violations = validator.validate(tournamentSeries);
        if (!violations.isEmpty()) {
            log.error("!> requesting modify tournament series {} with verifyTournamentSeries for tournament series with ConstraintViolations. Check evoking clients",
                    tournamentSeries.getId());
            return false;
        }
        List<TournamentMatch> tournamentMatches = tournamentSeries.getMatchList();
        if (nonNull(tournamentMatches)) {
            for (TournamentMatch match : tournamentMatches) {
                if (!tournamentMatchService.verifyTournamentMatch(match)) {
                    log.error("!> requesting modify tournament series {} with verifyTournamentSeries for tournament match with ConstraintViolations. Check evoking clients",
                            tournamentSeries.getId());
                    return false;
                }
            }
        }
        return true;
    }

    /**
     * Prototype for handle tournament status
     */
    private void handleTournamentSeriesStatusChanged(TournamentSeries tournamentSeries) {
        log.warn("~ status for tournament series id {} was changed from {} to {} ",
                tournamentSeries.getId(), tournamentSeries.getPrevStatus(), tournamentSeries.getStatus());
        tournamentSeries.setPrevStatus(tournamentSeries.getStatus());
    }
}

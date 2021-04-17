package com.freetonleague.core.service.implementations;


import com.freetonleague.core.domain.enums.TournamentStatusType;
import com.freetonleague.core.domain.model.Tournament;
import com.freetonleague.core.domain.model.TournamentMatch;
import com.freetonleague.core.domain.model.TournamentSeries;
import com.freetonleague.core.repository.TournamentSeriesRepository;
import com.freetonleague.core.service.TournamentGenerator;
import com.freetonleague.core.service.TournamentMatchService;
import com.freetonleague.core.service.TournamentSeriesService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import javax.validation.ConstraintViolation;
import javax.validation.Validator;
import java.util.List;
import java.util.Set;

import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;

@Slf4j
@RequiredArgsConstructor
@Service
public class TournamentSeriesServiceImpl implements TournamentSeriesService {

    private final TournamentSeriesRepository tournamentSeriesRepository;
    private final TournamentMatchService tournamentMatchService;
    private final Validator validator;

    @Autowired
    @Qualifier("singleEliminationGenerator")
    private TournamentGenerator singleEliminationGenerator;

    @Autowired
    @Qualifier("doubleEliminationGenerator")
    private TournamentGenerator doubleEliminationGenerator;

    private final List<TournamentStatusType> activeStatusList = List.of(
            TournamentStatusType.SIGN_UP,
            TournamentStatusType.ADJUSTMENT,
            TournamentStatusType.STARTED,
            TournamentStatusType.PAUSE
    );

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
    public Page<TournamentSeries> getSeriesList(Pageable pageable, Tournament tournament) {
        if (isNull(pageable) || isNull(tournament)) {
            log.error("!> requesting getSeriesList for NULL pageable {} or NULL tournament {}. Check evoking clients",
                    pageable, tournament);
            return null;
        }
        log.debug("^ trying to get tournament series list with pageable params: {} and by tournament id {}", pageable,
                tournament.getId());
        return tournamentSeriesRepository.findAllByTournament(pageable, tournament);
    }

    /**
     * Returns current active series for tournament
     */
    @Override
    public TournamentSeries getActiveSeriesForTournament(Tournament tournament) {
        if (isNull(tournament)) {
            log.error("!> requesting getActiveSeriesForTournament for NULL tournament. Check evoking clients");
            return null;
        }
        return tournamentSeriesRepository.findByStatusInAndTournament(activeStatusList, tournament);
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
     * Generate tournament series list for specified tournament and save to DB.
     */
    @Override
    public boolean generateSeriesForTournament(Tournament tournament) {
        if (isNull(tournament)) {
            log.error("!> requesting generateSeriesForTournament for NULL tournament. Check evoking clients");
            return false;
        }
        List<TournamentSeries> tournamentSeriesList = null;
        log.debug("^ trying to define component for generation algorithm {}", tournament.getSystemType());
        switch (tournament.getSystemType()) {
            case SINGLE_ELIMINATION:
                tournamentSeriesList = singleEliminationGenerator.generateSeriesForTournament(tournament);
                break;
            case DOUBLE_ELIMINATION:
                tournamentSeriesList = doubleEliminationGenerator.generateSeriesForTournament(tournament);
                break;
            default:
                break;
        }
        log.debug("^ trying to save series and matches from generator with series list size {}",
                nonNull(tournamentSeriesList) ? tournamentSeriesList.size() : null);
        if (nonNull(tournamentSeriesList)) {
            tournamentSeriesList.forEach(this::addSeries);
        } else {
            return false;
        }
        return true;
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
        if (tournamentSeries.isStatusChanged()) {
            this.handleTournamentSeriesStatusChanged(tournamentSeries);
        }
        return tournamentSeriesRepository.save(tournamentSeries);
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

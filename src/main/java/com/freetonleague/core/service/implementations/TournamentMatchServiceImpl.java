package com.freetonleague.core.service.implementations;

import com.freetonleague.core.domain.enums.TournamentStatusType;
import com.freetonleague.core.domain.model.TournamentMatch;
import com.freetonleague.core.domain.model.TournamentMatchRival;
import com.freetonleague.core.domain.model.TournamentSeries;
import com.freetonleague.core.repository.TournamentMatchRepository;
import com.freetonleague.core.service.TournamentEventService;
import com.freetonleague.core.service.TournamentMatchRivalService;
import com.freetonleague.core.service.TournamentMatchService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.validation.ConstraintViolation;
import javax.validation.Validator;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;

@Slf4j
@RequiredArgsConstructor
@Service
@Transactional
public class TournamentMatchServiceImpl implements TournamentMatchService {

    private final TournamentMatchRepository tournamentMatchRepository;
    private final TournamentMatchRivalService tournamentMatchRivalService;
    private final TournamentEventService tournamentEventService;
    private final Validator validator;

    /**
     * Returns founded tournament match by id
     */
    @Override
    public TournamentMatch getMatch(long id) {
        log.debug("^ trying to get tournament match by id: {}", id);
        return tournamentMatchRepository.findById(id).orElse(null);
    }

    /**
     * Returns list of all tournament matches filtered by requested params
     */
    @Override
    public Page<TournamentMatch> getMatchList(Pageable pageable, TournamentSeries tournamentSeries) {
        if (isNull(pageable) || isNull(tournamentSeries)) {
            log.error("!> requesting getMatchList for NULL pageable {} or NULL tournament {}. Check evoking clients",
                    pageable, tournamentSeries);
            return null;
        }
        log.debug("^ trying to get tournament match list with pageable params: {} and by tournament series id {}",
                pageable, tournamentSeries.getId());

        return tournamentMatchRepository.findAllByTournamentSeries(pageable, tournamentSeries);
    }

    /**
     * Add new tournament match to DB.
     */
    @Override
    public TournamentMatch addMatch(TournamentMatch tournamentMatch) {
        if (!this.verifyTournamentMatch(tournamentMatch)) {
            return null;
        }
        log.debug("^ trying to add new tournament match {}", tournamentMatch);
        return tournamentMatchRepository.save(tournamentMatch);
    }

    /**
     * Add tournament match list to DB.
     */
    @Override
    public List<TournamentMatch> addMatchList(List<TournamentMatch> tournamentMatchList) {
        log.debug("^ trying to add list of new tournament matches with size {}", tournamentMatchList.size());
        return tournamentMatchList.stream().map(this::addMatch).collect(Collectors.toList());
    }

    /**
     * Edit tournament match in DB.
     */
    @Override
    public TournamentMatch editMatch(TournamentMatch tournamentMatch) {
        if (!this.verifyTournamentMatch(tournamentMatch)) {
            return null;
        }
        if (!this.isExistsTournamentMatchById(tournamentMatch.getId())) {
            log.error("!> requesting modify tournament match {} for non-existed tournament match. Check evoking clients",
                    tournamentMatch.getId());
            return null;
        }
        log.debug("^ trying to modify tournament match {}", tournamentMatch);
        if (tournamentMatch.getStatus().isFinished()) {
            tournamentMatch.setFinishedDate(LocalDateTime.now());
        }
        tournamentMatch = tournamentMatchRepository.save(tournamentMatch);
        if (tournamentMatch.isStatusChanged()) {
            this.handleTournamentMatchStatusChanged(tournamentMatch);
        }
        return tournamentMatch;
    }

    /**
     * Mark 'deleted' tournament match in DB.
     */
    @Override
    public TournamentMatch deleteMatch(TournamentMatch tournamentMatch) {
        if (!this.verifyTournamentMatch(tournamentMatch)) {
            return null;
        }
        if (!this.isExistsTournamentMatchById(tournamentMatch.getId())) {
            log.error("!> requesting delete tournament match for non-existed tournament tournamentMatch. Check evoking clients");
            return null;
        }
        log.debug("^ trying to set 'deleted' mark to tournament match {}", tournamentMatch);
        tournamentMatch.setStatus(TournamentStatusType.DELETED);
        tournamentMatch = tournamentMatchRepository.save(tournamentMatch);
        this.handleTournamentMatchStatusChanged(tournamentMatch);
        return tournamentMatch;
    }

    /**
     * Returns sign of tournament match existence for specified id.
     */
    @Override
    public boolean isExistsTournamentMatchById(long id) {
        return tournamentMatchRepository.existsById(id);
    }

    /**
     * Returns sign of all match for series was finished.
     */
    @Override
    public boolean isAllMatchesFinishedBySeries(TournamentSeries tournamentSeries) {
        //TODO use it with EventService. Delete until 01/09/2021
//        int finishedMatchCount = tournamentMatchRepository.countByTournamentSeriesAndStatusIn(
//                tournamentSeries, this.finishedMatchStatusList);
//        int allMatchCount = tournamentMatchRepository.countByTournamentSeries(tournamentSeries);
        return tournamentSeries.getMatchList().parallelStream()
                .map(TournamentMatch::getStatus).allMatch(TournamentStatusType.getFinishedStatusList()::contains);
    }

    /**
     * Verify tournament match info with validation and business check
     */
    @Override
    public boolean verifyTournamentMatch(TournamentMatch tournamentMatch) {
        if (isNull(tournamentMatch)) {
            log.error("!> requesting modify tournament series with verifyTournamentMatch for NULL tournamentMatch. Check evoking clients");
            return false;
        }
        Set<ConstraintViolation<TournamentMatch>> violations = validator.validate(tournamentMatch);
        if (!violations.isEmpty()) {
            log.error("!> requesting modify tournament match id:{} name:{} with verifyTournamentMatch for tournament match with ConstraintViolations. Check evoking clients",
                    tournamentMatch.getId(), tournamentMatch.getName());
            return false;
        }
        Set<TournamentMatchRival> tournamentMatchRivals = tournamentMatch.getMatchRivalList();
        if (nonNull(tournamentMatchRivals)) {
            for (TournamentMatchRival matchRival : tournamentMatchRivals) {
                if (!tournamentMatchRivalService.verifyTournamentMatchRival(matchRival)) {
                    log.error("!> requesting modify tournament match id:{} name:{} with verifyTournamentMatch for tournament match rival with ConstraintViolations. Check evoking clients",
                            tournamentMatch.getId(), tournamentMatch.getName());
                    return false;
                }
            }
        }
        return true;
    }

    /**
     * Prototype for handle tournament match status
     */
    private void handleTournamentMatchStatusChanged(TournamentMatch tournamentMatch) {
        log.warn("~ status for tournament match id {} was changed from {} to {} ",
                tournamentMatch.getId(), tournamentMatch.getPrevStatus(), tournamentMatch.getStatus());
        //TODO check all match to be finished
        tournamentEventService.processMatchStatusChange(tournamentMatch, tournamentMatch.getStatus());
        tournamentMatch.setPrevStatus(tournamentMatch.getStatus());
    }
}
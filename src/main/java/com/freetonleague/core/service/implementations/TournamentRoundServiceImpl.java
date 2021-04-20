package com.freetonleague.core.service.implementations;


import com.freetonleague.core.domain.enums.TournamentStatusType;
import com.freetonleague.core.domain.model.Tournament;
import com.freetonleague.core.domain.model.TournamentRound;
import com.freetonleague.core.repository.TournamentRoundRepository;
import com.freetonleague.core.service.TournamentGenerator;
import com.freetonleague.core.service.TournamentRoundService;
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
public class TournamentRoundServiceImpl implements TournamentRoundService {

    private final TournamentRoundRepository tournamentRoundRepository;
    private final Validator validator;
    private final List<TournamentStatusType> activeStatusList = List.of(
            TournamentStatusType.SIGN_UP,
            TournamentStatusType.ADJUSTMENT,
            TournamentStatusType.STARTED,
            TournamentStatusType.PAUSE
    );
    @Autowired
    @Qualifier("singleEliminationGenerator")
    private TournamentGenerator singleEliminationGenerator;
    @Autowired
    @Qualifier("doubleEliminationGenerator")
    private TournamentGenerator doubleEliminationGenerator;

    /**
     * Returns founded tournament round by id
     */
    @Override
    public TournamentRound getRound(long id) {
        log.debug("^ trying to get tournament round by id: {}", id);
        return tournamentRoundRepository.findById(id).orElse(null);
    }

    /**
     * Returns list of all tournament round filtered by requested params
     */
    @Override
    public Page<TournamentRound> getRoundList(Pageable pageable, Tournament tournament) {
        if (isNull(pageable) || isNull(tournament)) {
            log.error("!> requesting getRoundList for NULL pageable {} or NULL tournament {}. Check evoking clients",
                    pageable, tournament);
            return null;
        }
        log.debug("^ trying to get tournament round list with pageable params: {} and by tournament id {}", pageable,
                tournament.getId());
        return tournamentRoundRepository.findAllByTournament(pageable, tournament);
    }

    /**
     * Returns current active round for tournament
     */
    @Override
    public TournamentRound getActiveRoundForTournament(Tournament tournament) {
        if (isNull(tournament)) {
            log.error("!> requesting getActiveRoundForTournament for NULL tournament. Check evoking clients");
            return null;
        }
        return tournamentRoundRepository.findByStatusInAndTournament(activeStatusList, tournament);
    }

    /**
     * Add new tournament round to DB.
     */
    @Override
    public TournamentRound addRound(TournamentRound tournamentRound) {
        if (!this.verifyTournamentRound(tournamentRound)) {
            return null;
        }
        log.debug("^ trying to add new tournament round {}", tournamentRound);
        return tournamentRoundRepository.save(tournamentRound);
    }

    /**
     * Generate tournament round list for specified tournament and save to DB.
     */
    @Override
    public boolean generateRoundForTournament(Tournament tournament) {
        if (isNull(tournament)) {
            log.error("!> requesting generateRoundForTournament for NULL tournament. Check evoking clients");
            return false;
        }
        List<TournamentRound> tournamentRoundList = null;
        log.debug("^ trying to define component for generation algorithm {}", tournament.getSystemType());
        switch (tournament.getSystemType()) {
            case SINGLE_ELIMINATION:
                tournamentRoundList = singleEliminationGenerator.generateRoundsForTournament(tournament);
                break;
            case DOUBLE_ELIMINATION:
                tournamentRoundList = doubleEliminationGenerator.generateRoundsForTournament(tournament);
                break;
            default:
                break;
        }
        log.debug("^ trying to save round and matches from generator with round list size {}",
                nonNull(tournamentRoundList) ? tournamentRoundList.size() : null);
        if (nonNull(tournamentRoundList)) {
            tournamentRoundList.forEach(this::addRound);
        } else {
            return false;
        }
        return true;
    }

    /**
     * Edit tournament round in DB.
     */
    @Override
    public TournamentRound editRound(TournamentRound tournamentRound) {
        if (!this.verifyTournamentRound(tournamentRound)) {
            return null;
        }
        if (!this.isExistsTournamentRoundById(tournamentRound.getId())) {
            log.error("!> requesting modify tournament round {} for non-existed tournament round. Check evoking clients",
                    tournamentRound.getId());
            return null;
        }
        log.debug("^ trying to modify tournament round {}", tournamentRound);
        if (tournamentRound.isStatusChanged()) {
            this.handleTournamentRoundStatusChanged(tournamentRound);
        }
        return tournamentRoundRepository.save(tournamentRound);
    }

    /**
     * Mark 'deleted' tournament round in DB.
     */
    @Override
    public TournamentRound deleteRound(TournamentRound tournamentRound) {
        if (!this.verifyTournamentRound(tournamentRound)) {
            return null;
        }
        if (!this.isExistsTournamentRoundById(tournamentRound.getId())) {
            log.error("!> requesting delete tournament round for non-existed tournament round. Check evoking clients");
            return null;
        }
        log.debug("^ trying to set 'deleted' mark to tournament round {}", tournamentRound);
        tournamentRound.setStatus(TournamentStatusType.DELETED);
        tournamentRound = tournamentRoundRepository.save(tournamentRound);
        this.handleTournamentRoundStatusChanged(tournamentRound);
        return tournamentRound;
    }

    /**
     * Returns sign of tournament round existence for specified id.
     */
    @Override
    public boolean isExistsTournamentRoundById(long id) {
        return tournamentRoundRepository.existsById(id);
    }

    /**
     * Returns number of last active tournament round in specified tournament
     */
    @Override
    public int getLastActiveRoundNumberForTournament(Tournament tournament) {
        return this.getActiveRoundForTournament(tournament).getRoundNumber();
    }

    /**
     * Validate tournament parameters and settings to modify
     */
    private boolean verifyTournamentRound(TournamentRound tournamentRound) {
        if (isNull(tournamentRound)) {
            log.error("!> requesting modify tournament round with verifyTournamentRound for NULL tournamentRound. Check evoking clients");
            return false;
        }
        Set<ConstraintViolation<TournamentRound>> violations = validator.validate(tournamentRound);
        if (!violations.isEmpty()) {
            log.error("!> requesting modify tournament round {} with verifyTournamentRound for tournament round with ConstraintViolations. Check evoking clients",
                    tournamentRound.getId());
            return false;
        }
        return true;
    }

    /**
     * Prototype for handle tournament status
     */
    private void handleTournamentRoundStatusChanged(TournamentRound tournamentRound) {
        log.warn("~ status for tournament round id {} was changed from {} to {} ",
                tournamentRound.getId(), tournamentRound.getPrevStatus(), tournamentRound.getStatus());
        tournamentRound.setPrevStatus(tournamentRound.getStatus());
    }
}

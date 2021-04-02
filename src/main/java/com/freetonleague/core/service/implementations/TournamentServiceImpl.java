package com.freetonleague.core.service.implementations;


import com.freetonleague.core.domain.model.Tournament;
import com.freetonleague.core.domain.model.TournamentSettings;
import com.freetonleague.core.repository.TournamentRepository;
import com.freetonleague.core.repository.TournamentSettingsRepository;
import com.freetonleague.core.service.TournamentService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import javax.validation.ConstraintViolation;
import javax.validation.Validator;
import java.util.Set;

import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;

@Slf4j
@RequiredArgsConstructor
@Service
public class TournamentServiceImpl implements TournamentService {

    private final TournamentRepository tournamentRepository;
    private final TournamentSettingsRepository tournamentSettingsRepository;
    private final Validator validator;

    //    нельзя удалить команду которая участвует в турнире

    /**
     * Returns founded tournament by id
     */
    @Override
    public Tournament getTournament(long id) {
        log.debug("^ trying to get tournament by id: {}", id);
        return tournamentRepository.findById(id).orElse(null);
    }

    /**
     * Returns list of all teams filtered by requested params
     */
    @Override
    public Page<Tournament> getTournamentList(Pageable pageable) {
        if (isNull(pageable)) {
            log.error("!> requesting getTournamentList for NULL pageable. Check evoking clients");
            return null;
        }
        log.debug("^ trying to get tournament list with pageable params: {}", pageable);
        return tournamentRepository.findAll(pageable);
    }

    /**
     * Add new tournament to DB.
     */
    @Override
    public Tournament addTournament(Tournament tournament) {
        if (!this.verifyTournament(tournament)) {
            return null;
        }
        return tournamentRepository.save(tournament);
    }

    /**
     * Edit tournament in DB.
     */
    @Override
    public Tournament editTournament(Tournament tournament) {
        if (!this.verifyTournament(tournament)) {
            return null;
        }
        if (!this.isExistsTournamentById(tournament.getId())) {
            log.error("!> requesting modify tournament with verifyTournament for NULL tournament. Check evoking clients");
            return null;
        }
        return tournamentRepository.save(tournament);
    }

    /**
     * Returns sign of tournament existence for specified id.
     */
    @Override
    public boolean isExistsTournamentById(long id) {
        return tournamentRepository.existsById(id);
    }

    /**
     * Validate tournament parameters and settings to modify
     */
    private boolean verifyTournament(Tournament tournament) {
        if (isNull(tournament)) {
            log.error("!> requesting modify tournament with verifyTournament for NULL tournament. Check evoking clients");
            return false;
        }
        Set<ConstraintViolation<Tournament>> violations = validator.validate(tournament);
        if (!violations.isEmpty()) {
            log.error("!> requesting modify tournament with verifyTournament for tournament with ConstraintViolations. Check evoking clients");
            return false;
        }
        TournamentSettings tournamentSettings = tournament.getTournamentSettings();
        if (nonNull(tournamentSettings)) {
            Set<ConstraintViolation<TournamentSettings>> settingsViolations = validator.validate(tournamentSettings);
            if (!settingsViolations.isEmpty()) {
                log.error("!> requesting modify tournament with verifyTournament for tournament settings with ConstraintViolations. Check evoking clients");
                return false;
            }
        }
        return true;
    }

}

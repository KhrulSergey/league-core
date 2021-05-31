package com.freetonleague.core.service.implementations;

import com.freetonleague.core.domain.model.TournamentOrganizer;
import com.freetonleague.core.repository.TournamentOrganizerRepository;
import com.freetonleague.core.service.TournamentOrganizerService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.validation.ConstraintViolation;
import javax.validation.Validator;
import java.util.Set;

import static java.util.Objects.isNull;

@Slf4j
@RequiredArgsConstructor
@Service
public class TournamentOrganizerServiceIml implements TournamentOrganizerService {

    private final TournamentOrganizerRepository organizerRepository;
    private final Validator validator;

    /**
     * Returns founded tournament organizer by id
     */
    @Override
    public TournamentOrganizer get(long id) {
        log.debug("^ try to get tournament organizer by id: '{}'", id);
        return organizerRepository.findById(id).orElse(null);
    }

    /**
     * Adding a new tournament organizer to DB.
     */
    @Override
    public TournamentOrganizer add(TournamentOrganizer tournamentOrganizer) {
        if (isNull(tournamentOrganizer)) {
            log.error("!> requesting add organizer for NULL tournamentOrganizer. Check evoking clients");
            return null;
        }
        Set<ConstraintViolation<TournamentOrganizer>> violations = validator.validate(tournamentOrganizer);
        if (!violations.isEmpty()) {
            log.error("!> requesting add organizer for tournamentOrganizer '{}' with constraint violations: '{}'. Check evoking clients", tournamentOrganizer, violations);
            return null;
        }
        log.debug("^ trying to add organizer in DB: '{}'", tournamentOrganizer);
        return organizerRepository.save(tournamentOrganizer);
    }

    /**
     * Edit an existing tournament organizer in DB.
     */
    @Override
    public TournamentOrganizer edit(TournamentOrganizer tournamentOrganizer) {
        if (isNull(tournamentOrganizer)) {
            log.error("!> requesting edit organizer for NULL tournamentOrganizer. Check evoking clients");
            return null;
        }
        Set<ConstraintViolation<TournamentOrganizer>> violations = validator.validate(tournamentOrganizer);
        if (!violations.isEmpty()) {
            log.error("!> requesting edit organizer for tournamentOrganizer '{}' with constraint violations: '{}'. Check evoking clients", tournamentOrganizer, violations);
            return null;
        }
        if (!organizerRepository.existsById(tournamentOrganizer.getId())) {
            log.error("!> requesting edit organizer for non-existent tournamentOrganizer '{}'. Check evoking clients", tournamentOrganizer);
            return null;
        }
        log.debug("^ trying to modify organizer in DB: '{}'", tournamentOrganizer);
        return organizerRepository.save(tournamentOrganizer);
    }
}

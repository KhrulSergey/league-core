package com.freetonleague.core.service.implementations;

import com.freetonleague.core.domain.model.Team;
import com.freetonleague.core.repository.TeamRepository;
import com.freetonleague.core.service.TeamService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import javax.validation.Validator;
import java.util.Set;

@Transactional(propagation = Propagation.REQUIRES_NEW)
@Service
@RequiredArgsConstructor
@Slf4j
public class TeamServiceImpl implements TeamService {
    private final TeamRepository teamRepository;
    private final Validator validator;

    private static final String TEAM_NOT_FOUND = "Team does not exist";
    private static final String PARTICIPANT_NOT_FOUND = "Participant does not exist";

    /**
     * Add new team to DB.
     *
     * @param team Team to be added
     * @return Added team
     */
    @Override
    public Team add(Team team) {
        Set<ConstraintViolation<Team>> violations = validator.validate(team);
        if (violations.isEmpty()) {
            log.debug("add team: {}", team);
            return teamRepository.saveAndFlush(team);
        } else {
            log.warn("team: {} have constraint violations: {}", team, violations);
            throw new ConstraintViolationException(violations);
        }
    }

    /**
     * Edit team in DB.
     * Example - captain, logo, name
     *
     * @param team Team to be edited
     * @return Edited team
     */
    @Override
    public Team edit(Team team) {
        Team updatedTeam;
        Set<ConstraintViolation<Team>> violations = validator.validate(team);
        if (teamRepository.existsById(team.getId())) {
            if (violations.isEmpty()) {
                log.debug("team: {} is edited", team);
                updatedTeam = teamRepository.saveAndFlush(team);
            } else {
                log.warn("edited team: {} have constraint violations: {}", team, violations);
                throw new ConstraintViolationException(violations);
            }
        } else {
            log.warn("team: {} is not exist", team);
            throw new ConstraintViolationException(violations);
        }
        return updatedTeam;
    }
}

package com.freetonleague.core.service.implementations;

import com.freetonleague.core.domain.enums.ParticipantStatusType;
import com.freetonleague.core.domain.model.Participant;
import com.freetonleague.core.domain.model.Team;
import com.freetonleague.core.domain.model.User;
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
import java.util.List;
import java.util.Set;

import static java.util.Objects.nonNull;

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

    /**
     * Returns founded team by id
     */
    @Override
    public Team getById(long id) {
        return null;
    }

    /**
     * Returns list of all teams
     */
    @Override
    public List<Team> getList() {
        return null;
    }

    /**
     * Get the list of teams for current user
     */
    @Override
    public List<Team> getListByUser(User user) {
        return null;
    }

    /**
     * Expel (exclude) from requested team the specified participant.
     * Accessible only for a capitan of the team
     */
    @Override
    public Team expel(Team team, Participant participant) {
        return null;
    }

    /**
     * Delete (disband) all the band.
     * Accessible only for a capitan of the team
     */
    @Override
    public void delete(Team team) {

    }

    /**
     * Returns sign of user participation in the specified team
     */
    @Override
    public Participant getParticipantOfTeamByUser(Team team, User user) {
        return team.getParticipantList().stream().filter(p -> p.getUser().equals(user)).findFirst().orElse(null);
    }

    /**
     * Returns sign of user participation in the specified team
     */
    @Override
    public ParticipantStatusType getUserParticipantStatusOfTeam(Team team, User user) {
        Participant participant = team.getParticipantList().parallelStream()
                .filter(p -> p.getUser().equals(user))
                .findFirst()
                .orElse(null);
        return nonNull(participant) ? participant.getStatus() : null;
    }
}

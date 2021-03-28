package com.freetonleague.core.service.implementations;

import com.freetonleague.core.domain.enums.TeamParticipantStatusType;
import com.freetonleague.core.domain.enums.TeamStateType;
import com.freetonleague.core.domain.model.Team;
import com.freetonleague.core.domain.model.TeamParticipant;
import com.freetonleague.core.domain.model.User;
import com.freetonleague.core.exception.ExceptionMessages;
import com.freetonleague.core.exception.TeamManageException;
import com.freetonleague.core.repository.TeamRepository;
import com.freetonleague.core.service.TeamParticipantService;
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

import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;

@Transactional(propagation = Propagation.REQUIRES_NEW)
@Service
@RequiredArgsConstructor
@Slf4j
public class TeamServiceImpl implements TeamService {
    private final TeamRepository teamRepository;
    private final TeamParticipantService teamParticipantService;
    private final Validator validator;

    /**
     * Add new team to DB.
     */
    @Override
    public Team add(Team team) {
        Set<ConstraintViolation<Team>> violations = validator.validate(team);
        if (!violations.isEmpty()) {
            log.warn("~ team: {} have constraint violations: {}", team, violations);
            throw new ConstraintViolationException(violations);
        }
        log.debug("^ trying to add team in DB: {}", team);
        return teamRepository.save(team);
    }

    /**
     * Edit team in DB.
     */
    @Override
    public Team edit(Team team) {
        Set<ConstraintViolation<Team>> violations = validator.validate(team);
        if (!violations.isEmpty()) {
            log.warn("edited team: {} have constraint violations: {}", team, violations);
            throw new ConstraintViolationException(violations);
        }
        if (!teamRepository.existsById(team.getId())) {
            log.warn("team: {} is not exist", team);
            throw new TeamManageException(ExceptionMessages.TEAM_NOT_FOUND_ERROR,
                    "Requested team with id " + team.getId() + " was not found");
        }

        log.debug("^ trying to modify team in DB: {}", team);
        return teamRepository.saveAndFlush(team);
    }

    /**
     * Returns founded team by id
     */
    @Override
    public Team getById(long id) {
        log.debug("^ trying to get team by id: {}", id);
        return teamRepository.findById(id).orElse(null);
    }

    /**
     * Returns founded team by id
     */
    @Override
    public Team getByName(String teamName) {
        log.debug("^ trying to get team by name: {}", teamName);
        return teamRepository.findByName(teamName);
    }

    /**
     * Returns list of all teams
     */
    @Override
    public List<Team> getList() {
        log.debug("^ trying to get list of all teams");
        return teamRepository.findAll();
    }

    /**
     * Get the list of teams for current user
     */
    @Override
    public List<Team> getListByUser(User user) {
        log.debug("^ trying to get list of all teams, where user is a participant");
        if (isNull(user)) {
            log.error("!> requesting getListByUser for NULL user. Check evoking clients");
            return null;
        }
        return teamRepository.findAllByUserParticipation(user);
    }

    /**
     * Expel (exclude) from requested team the specified participant.
     * Accessible only for a captain of the team
     */
    @Override
    public Team expelParticipant(Team team, TeamParticipant teamParticipant) {
        if (isNull(team) || isNull(teamParticipant)) {
            log.error("!> requesting expelParticipant for NULL team {} or NULL participant {}. Check evoking clients", team, teamParticipant);
            return null;
        }
        if (!team.getParticipantList().contains(teamParticipant)) {
            log.error("!> requesting expelParticipant for exclude participant {} who isn't a member of the team {}. " +
                    "Check evoking clients", teamParticipant, team);
            return null;
        }
        log.debug("^ trying to expel participant {} from team {}", teamParticipant, team.getId());
        teamParticipantService.expelParticipant(teamParticipant);
        return teamRepository.findById(team.getId()).orElse(null);
    }

    /**
     * Disband (delete) all the band.
     * Accessible only for a captain of the team
     */
    @Override
    public void disband(Team team) {
        if (isNull(team)) {
            log.error("!> requesting disband for NULL team. Check evoking clients");
            return;
        }
        log.debug("^ trying to disband the team  {}", team);
        team.getParticipantList().parallelStream() // expel all participants
                .forEach(teamParticipantService::expelParticipant);
        team.setStatus(TeamStateType.DELETED);
        teamRepository.saveAndFlush(team); // save changes to team
    }

    /**
     * Returns sign of user participation in the specified team
     */
    @Override
    public TeamParticipant getParticipantOfTeamByUser(Team team, User user) {
        return team.getParticipantList().stream()
                .filter(p -> p.getUser().getLeagueId().equals(user.getLeagueId()))
                .findFirst().orElse(null);
    }

    /**
     * Returns sign of user participation in the specified team
     */
    @Override
    public TeamParticipantStatusType getUserParticipantStatusOfTeam(Team team, User user) {
        TeamParticipant teamParticipant = team.getParticipantList().parallelStream()
                .filter(p -> p.getUser().getLeagueId().equals(user.getLeagueId()))
                .findFirst()
                .orElse(null);
        return nonNull(teamParticipant) ? teamParticipant.getStatus() : null;
    }
}

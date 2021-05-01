package com.freetonleague.core.service.implementations;

import com.freetonleague.core.domain.dto.TeamBaseDto;
import com.freetonleague.core.domain.dto.TeamDto;
import com.freetonleague.core.domain.dto.TeamExtendedDto;
import com.freetonleague.core.domain.enums.TeamParticipantStatusType;
import com.freetonleague.core.domain.enums.TeamStateType;
import com.freetonleague.core.domain.model.Team;
import com.freetonleague.core.domain.model.TeamParticipant;
import com.freetonleague.core.domain.model.User;
import com.freetonleague.core.exception.*;
import com.freetonleague.core.mapper.TeamMapper;
import com.freetonleague.core.service.RestTeamFacade;
import com.freetonleague.core.service.RestTeamParticipantFacade;
import com.freetonleague.core.service.TeamService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import javax.validation.Validator;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Set;

import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;

@Slf4j
@Service
@RequiredArgsConstructor
public class RestTeamFacadeImpl implements RestTeamFacade {

    private final TeamService teamService;
    private final RestTeamParticipantFacade teamParticipantFacade;
    private final TeamMapper teamMapper;
    private final Validator validator;

    /**
     * Returns founded team by id
     * If current user is participant, then returns all data about team
     * Else - only standard info
     */
    @Override
    public TeamBaseDto getTeamById(long id, User user) {
        Team team = this.getVerifiedTeamById(id, user, true);
        TeamBaseDto teamDto;
        if (nonNull(teamService.getUserParticipantStatusOfTeam(team, user))) {
            teamDto = teamMapper.toExtendedDto(team);
        } else {
            teamDto = teamMapper.toDto(team);
        }
        return teamDto;
    }

    /**
     * Returns list of all teams
     * Available only base info
     */
    @Override
    public List<TeamBaseDto> getTeamList(User user) {
        if (isNull(user)) {
            log.debug("^ user is not authenticate. 'getTeamList' request denied");
            throw new UnauthorizedException(ExceptionMessages.AUTHENTICATION_ERROR, "'getTeamList' request denied");
        }
        return teamMapper.toBaseDto(teamService.getTeamList());
    }

    /**
     * Registry new team on platform
     */
    @Override
    public TeamDto addTeam(TeamBaseDto teamDto, User user) {
        if (isNull(user)) {
            log.debug("^ user is not authenticate. 'addTeam' request denied");
            throw new UnauthorizedException(ExceptionMessages.AUTHENTICATION_ERROR, "'addTeam' request denied");
        }
        Set<ConstraintViolation<TeamBaseDto>> violations = validator.validate(teamDto);
        if (!violations.isEmpty()) {
            log.debug("^ transmitted TeamBaseDto: {} have constraint violations: {}", teamDto, violations);
            throw new ConstraintViolationException(violations);
        }
        if (nonNull(teamService.getTeamByName(teamDto.getName()))) {
            log.warn("~ parameter 'name' is not unique for addTeam");
            throw new ValidationException(ExceptionMessages.TEAM_DUPLICATE_BY_NAME_ERROR, "name", "parameter name is not unique for addTeam");
        }

        teamDto.setId(null);
        Team newTeam = teamMapper.fromDto(teamDto);
        TeamParticipant captain = TeamParticipant.builder() // save current user as captain
                .user(user)
                .team(newTeam)
                .status(TeamParticipantStatusType.CAPTAIN)
                .joinAt(LocalDateTime.now())
                .build();
        //set captain to team and  1st participant
        newTeam.setCaptain(captain);
        newTeam.setParticipantList(Collections.singleton(captain));
        newTeam.setStatus(TeamStateType.ACTIVE);
        //save team and captain by Cascade persistence
        newTeam = teamService.addTeam(newTeam);

        if (isNull(newTeam)) {
            log.error("!> error while creating team from dto {} for user {}.", teamDto, user);
            throw new TeamManageException(ExceptionMessages.TEAM_CREATION_ERROR, "Team was not saved on Portal. Check requested params.");
        }
        return teamMapper.toDto(newTeam);
    }

    /**
     * Edit team on Portal.
     * Editable fields only logo, name
     */
    @Override
    public TeamExtendedDto editTeam(long id, TeamBaseDto teamDto, User user) {
        Team team = this.getVerifiedTeamById(id, user, true);
        Set<ConstraintViolation<TeamBaseDto>> violations = validator.validate(teamDto);
        if (!violations.isEmpty()) {
            log.debug("^ transmitted TeamBaseDto: {} have constraint violations: {}", teamDto, violations);
            throw new ConstraintViolationException(violations);
        }
        if (!team.isCaptain(user) && !user.isAdmin()) {
            log.warn("~ forbiddenException for modifying team from dto {} for user {}.", teamDto, user);
            throw new ForbiddenException(ExceptionMessages.TEAM_FORBIDDEN_ERROR);
        }

        if (!team.getName().equals(teamDto.getName()) && nonNull(teamService.getTeamByName(teamDto.getName()))) {
            log.warn("~ parameter 'name' is not unique for editTeam");
            throw new ValidationException(ExceptionMessages.TEAM_DUPLICATE_BY_NAME_ERROR, "name", "parameter name is not unique for editTeam");
        }

        team.setName(teamDto.getName());
        team.setTeamLogoFileName(teamDto.getTeamLogoFileName());
        team = teamService.editTeam(team);
        if (isNull(team)) {
            log.error("!> error while modifying team from dto {} for user {}.", teamDto, user);
            throw new TeamManageException(ExceptionMessages.TEAM_MODIFY_ERROR, "Team was not modified on Portal. Check requested params.");
        }
        return teamMapper.toExtendedDto(team);
    }

    /**
     * Expel from requested team the specified participant.
     * Accessible only for a captain of the team
     */
    @Override
    public TeamExtendedDto expel(long id, long participantId, User user) {
        Team team = this.getVerifiedTeamById(id, user, true);
        if (!team.isCaptain(user)) {
            log.warn("~ forbiddenException for expelling participant id {} team from team {} for user {}.",
                    participantId, team, user);
            throw new TeamManageException(ExceptionMessages.TEAM_FORBIDDEN_ERROR, "Only captain can exclude participants from team.");
        }
        TeamParticipant teamParticipant = teamParticipantFacade.getTeamParticipant(participantId, team);
        if (teamParticipant.getStatus() != TeamParticipantStatusType.ACTIVE) {
            log.debug("^ forbiddenException for expelling participant id {} team from team {} for user {}.",
                    participantId, team, user);
            throw new TeamManageException(ExceptionMessages.TEAM_EXPELLING_PARTICIPANT_ERROR,
                    "Only active members can be excluded, participant status is " + teamParticipant.getStatus());
        }
        TeamExtendedDto teamDto = teamMapper.toExtendedDto(teamService.expelParticipant(team, teamParticipant, false));
        if (isNull(teamDto)) {
            log.error("!> error while expelling participant {} from team {}.", teamParticipant, team);
            throw new TeamManageException(ExceptionMessages.TEAM_MODIFY_ERROR, "Team was not modified on Portal. Check requested params.");
        }
        return teamDto;
    }


    /**
     * Disband all the band.
     * Accessible only for a captain of the team
     */
    @Override
    public void disband(long id, User user) {
        Team team = this.getVerifiedTeamById(id, user, true);
        if (!team.isCaptain(user)) {
            log.warn("~ forbiddenException for disband team id {} for user {}.", id, user);
            throw new ForbiddenException(ExceptionMessages.TEAM_FORBIDDEN_ERROR);
        }
        if (teamService.isTeamParticipateInActiveTournament(team)) {
            log.debug("^ Team is participate in active tournament. Team id {} can' be disband.", id);
            throw new TeamManageException(ExceptionMessages.TEAM_DISBAND_ERROR, "Team was not disband on Portal. Check requested params.");
        }
        teamService.disbandTeam(team);
    }

    /**
     * Quit current user from specified team
     */
    @Override
    public void quitUserFromTeam(long id, User user) {
        Team team = this.getVerifiedTeamById(id, user, true);
        TeamParticipant teamParticipant = teamService.getParticipantOfTeamByUser(team, user);
        if (isNull(teamParticipant) || teamParticipant.getStatus() != TeamParticipantStatusType.ACTIVE) {
            log.warn("~ forbiddenException for quitting user {} from team {}.", user, team);
            throw new TeamManageException(ExceptionMessages.TEAM_EXPELLING_ERROR, "Only active members can be excluded from team: captain, invited or deleted participant can't.");
        }
        teamService.expelParticipant(team, teamParticipant, true);
    }

    /**
     * Get the list of teams for current user
     */
    @Override
    public List<TeamExtendedDto> getUserTeamList(User user) {
        if (isNull(user)) {
            log.debug("^ user is not authenticate. 'getUserTeamList' request denied");
            throw new UnauthorizedException(ExceptionMessages.AUTHENTICATION_ERROR, "'getUserTeamList' request denied");
        }
        return teamMapper.toExtendedDto(teamService.getTeamListByUser(user));
    }

    /**
     * Getting team by id and user with privacy check
     */
    @Override
    public Team getVerifiedTeamById(long id, User user, boolean checkUser) {
        if (checkUser && isNull(user)) {
            log.debug("^ user is not authenticate. 'getVerifiedTeamById' in RestTeamParticipantFacade request denied");
            throw new UnauthorizedException(ExceptionMessages.AUTHENTICATION_ERROR, "'getVerifiedTeamById' request denied");
        }
        Team team = teamService.getTeamById(id);
        if (isNull(team)) {
            log.debug("^ Team with requested id {} was not found. 'getVerifiedTeamById' in RestTeamParticipantFacade request denied", id);
            throw new TeamManageException(ExceptionMessages.TEAM_NOT_FOUND_ERROR, "Team with requested id " + id + " was not found");
        }
        if (team.getStatus() != TeamStateType.ACTIVE) {
            log.debug("^ Team with requested id {} was {}. 'getVerifiedTeamById' in RestTeamParticipantFacade request denied", id, team.getStatus());
            throw new TeamManageException(ExceptionMessages.TEAM_DISABLE_ERROR, "Active team with requested id " + id + " was not found");
        }
        return team;
    }
}

package com.freetonleague.core.service.implementations;

import com.freetonleague.core.domain.dto.TeamBaseDto;
import com.freetonleague.core.domain.dto.TeamDto;
import com.freetonleague.core.domain.dto.TeamExtendedDto;
import com.freetonleague.core.domain.enums.ParticipantStatusType;
import com.freetonleague.core.domain.model.Participant;
import com.freetonleague.core.domain.model.Team;
import com.freetonleague.core.domain.model.User;
import com.freetonleague.core.exception.*;
import com.freetonleague.core.mapper.ParticipantMapper;
import com.freetonleague.core.mapper.TeamMapper;
import com.freetonleague.core.service.ParticipantService;
import com.freetonleague.core.service.RestTeamFacade;
import com.freetonleague.core.service.TeamService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import javax.validation.Validator;
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
    private final ParticipantService participantService;
    private final TeamMapper teamMapper;
    private final ParticipantMapper participantMapper;
    private final Validator validator;

    /**
     * Returns founded team by id
     * If current user is participant, then returns all data about team
     * Else - only standard info
     */
    @Override
    public TeamBaseDto getTeamById(long id, User user) {
        Team team = this.getVerifiedTeamById(id, user);
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
     *
     * @return list of team entities
     */
    @Override
    public List<TeamBaseDto> getTeamList(User user) {
        if (isNull(user)) {
            log.debug("^ user is not authenticate. 'getTeamList' request denied");
            throw new UnauthorizedException(ExceptionMessages.AUTHENTICATION_ERROR, "'getTeamList' request denied");
        }
        return teamMapper.toBaseDto(teamService.getList());
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
        teamDto.setId(null);
        Team newTeam = teamMapper.fromDto(teamDto);
        Participant capitan = Participant.builder()
                .user(user)
                .team(newTeam)
                .status(ParticipantStatusType.CAPITAN)
                .build();
        // save current user as capitan and 1st participant
        newTeam.setCaptain(capitan);
        newTeam.setParticipantList(Collections.singleton(capitan));
        newTeam = teamService.add(newTeam);
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
        Team team = this.getVerifiedTeamById(id, user);
        Set<ConstraintViolation<TeamBaseDto>> violations = validator.validate(teamDto);
        if (!violations.isEmpty()) {
            log.debug("^ transmitted TeamBaseDto: {} have constraint violations: {}", teamDto, violations);
            throw new ConstraintViolationException(violations);
        }
        if (!team.isCapitan(user)) {
            log.warn("~ forbiddenException for modifying team from dto {} for user {}.", teamDto, user);
            throw new ForbiddenException(ExceptionMessages.TEAM_FORBIDDEN_ERROR);
        }
        team.setName(teamDto.getName());
        team.setTeamLogoFileName(teamDto.getTeamLogoFileName());
        team = teamService.edit(team);
        if (isNull(team)) {
            log.error("!> error while modifying team from dto {} for user {}.", teamDto, user);
            throw new TeamManageException(ExceptionMessages.TEAM_MODIFY_ERROR, "Team was not modified on Portal. Check requested params.");
        }
        return teamMapper.toExtendedDto(team);
    }

    /**
     * Expel from requested team the specified participant.
     * Accessible only for a capitan of the team
     */
    @Override
    public TeamExtendedDto expel(long id, long participantId, User user) {
        Team team = this.getVerifiedTeamById(id, user);
        if (!team.isCapitan(user)) {
            log.warn("~ forbiddenException for expelling participant id {} team from team {} for user {}.",
                    participantId, team, user);
            throw new TeamManageException(ExceptionMessages.TEAM_FORBIDDEN_ERROR, "Only capitan can exclude participants from team.");
        }
        Participant participant = this.getTeamParticipant(participantId, team);
        TeamExtendedDto teamDto = teamMapper.toExtendedDto(teamService.expel(team, participant));
        if (isNull(teamDto)) {
            log.error("!> error while expelling participant {} from team {}.", participant, team);
            throw new TeamManageException(ExceptionMessages.TEAM_MODIFY_ERROR, "Team was not modified on Portal. Check requested params.");
        }
        return teamDto;
    }


    /**
     * Disband all the band.
     * Accessible only for a capitan of the team
     */
    @Override
    public void disband(long id, User user) {
        Team team = this.getVerifiedTeamById(id, user);
        if (!team.isCapitan(user)) {
            log.warn("~ forbiddenException for disband team {} for user {}.", team, user);
            throw new ForbiddenException(ExceptionMessages.TEAM_FORBIDDEN_ERROR);
        }
        teamService.delete(team);
    }

    /**
     * Quit current user from specified team
     */
    @Override
    public void quitUserFromTeam(long id, User user) {
        Team team = this.getVerifiedTeamById(id, user);
        Participant participant = teamService.getParticipantOfTeamByUser(team, user);
        if (isNull(participant) || participant.getStatus() != ParticipantStatusType.ACTIVE) {
            log.warn("~ forbiddenException for quitting user {} from team {}.", user, team);
            throw new TeamManageException(ExceptionMessages.TEAM_EXPELLING_ERROR, "Only active members can be excluded from team: capitan, invited or deleted participant can't.");
        }
        teamService.expel(team, participant);
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
        return teamMapper.toExtendedDto(teamService.getListByUser(user));
    }

    /**
     * Getting team by id and user with privacy check
     */
    private Team getVerifiedTeamById(long id, User user) {
        if (isNull(user)) {
            log.debug("^ user is not authenticate. 'getVerifiedTeamById' request denied");
            throw new UnauthorizedException(ExceptionMessages.AUTHENTICATION_ERROR, "'getVerifiedTeamById' request denied");
        }
        Team team = teamService.getById(id);
        if (isNull(team)) {
            throw new TeamManageException(ExceptionMessages.TEAM_NOT_FOUND_ERROR, "Team with requested id" + id + "was not found");
        }
        return team;
    }

    /**
     * Getting participant by id, verify team membering
     */
    private Participant getTeamParticipant(long participantId, Team team) {
        Participant participant = participantService.getById(participantId);
        if (isNull(participant)) {
            log.debug("^ Participant with requested id {} was not found. 'getTeamParticipant' request denied", participantId);
            throw new ParticipantManageException(ExceptionMessages.PARTICIPANT_NOT_FOUND_ERROR, "Participant with requested id" + participantId + "was not found");
        }
        if (!team.getParticipantList().contains(participant)) {
            log.debug("^ user is not authenticate. 'getUserTeamList' request denied");
            throw new TeamManageException(ExceptionMessages.TEAM_PARTICIPANT_NOT_FOUND_ERROR, "Participant with requested id" + participantId + "is not a member of team id " + team.getId());
        }
        return participant;
    }
}

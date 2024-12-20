package com.freetonleague.core.service.implementations;

import com.freetonleague.core.cloudclient.LeagueStorageClientService;
import com.freetonleague.core.domain.enums.ParticipationStateType;
import com.freetonleague.core.domain.enums.TeamParticipantStatusType;
import com.freetonleague.core.domain.enums.TeamStateType;
import com.freetonleague.core.domain.model.Team;
import com.freetonleague.core.domain.model.TeamParticipant;
import com.freetonleague.core.domain.model.tournament.TournamentTeamProposal;
import com.freetonleague.core.domain.model.User;
import com.freetonleague.core.repository.TeamRepository;
import com.freetonleague.core.service.*;
import com.freetonleague.core.service.tournament.TournamentProposalService;
import com.freetonleague.core.service.tournament.TournamentService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.validation.ConstraintViolation;
import javax.validation.Validator;
import java.util.Objects;
import java.util.Set;

import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;
import static org.apache.commons.lang3.StringUtils.isBlank;

@Transactional
@Service
@RequiredArgsConstructor
@Slf4j
public class TeamServiceImpl implements TeamService {
    private final TeamRepository teamRepository;
    private final TeamParticipantService teamParticipantService;
    private final LeagueStorageClientService leagueStorageClientService;
    private final Validator validator;

    @Lazy
    @Autowired
    private TeamEventService teamEventService;

    @Lazy
    @Autowired
    private TournamentService tournamentService;

    @Lazy
    @Autowired
    private TournamentProposalService tournamentProposalService;

    /**
     * Add new team to DB.
     */
    @Override
    public Team addTeam(Team team) {
        Set<ConstraintViolation<Team>> violations = validator.validate(team);
        if (!violations.isEmpty()) {
            log.error("!> requesting addTeam for team '{}' with constraint violations: '{}'. Check evoking clients", team, violations);
            return null;
        }
        log.debug("^ trying to add team in DB: '{}'", team);
        team.generateGUID();
        if (!isBlank(team.getLogoRawFile())) {
            team.setLogoHashKey(leagueStorageClientService.saveTeamLogo(team));
        }
        team = teamRepository.save(team);
        teamEventService.processTeamStatusChange(team, TeamStateType.CREATED);
        return team;
    }

    /**
     * Edit team in DB.
     */
    @Override
    public Team editTeam(Team team) {
        if (isNull(team)) {
            log.error("!> requesting editTeam for NULL team. Check evoking clients");
            return null;
        }
        Set<ConstraintViolation<Team>> violations = validator.validate(team);
        if (!violations.isEmpty()) {
            log.error("!> requesting editTeam for team '{}' with constraint violations: '{}'. Check evoking clients", team, violations);
            return null;
        }
        if (!teamRepository.existsById(team.getId())) {
            log.error("!> requesting editTeam for non-existent team '{}'. Check evoking clients", team);
            return null;
        }
        log.debug("^ trying to modify team in DB: '{}'", team);
        if (!isBlank(team.getLogoRawFile())) {
            team.setLogoHashKey(leagueStorageClientService.saveTeamLogo(team));
        }
        return teamRepository.saveAndFlush(team);
    }

    /**
     * Returns founded team by id
     */
    @Override
    public Team getTeamById(long id) {
        log.debug("^ trying to get team by id: '{}'", id);
        return teamRepository.findById(id).orElse(null);
    }

    /**
     * Returns founded team by id
     */
    @Override
    public Team getTeamByName(String teamName) {
        if (isBlank(teamName)) {
            log.error("!> requesting getByName for Blank teamName. Check evoking clients");
            return null;
        }
        log.debug("^ trying to get team by name: '{}'", teamName);
        return teamRepository.findByName(teamName);
    }

    /**
     * Returns list of all teams
     */
    @Override
    public Page<Team> getTeamList(Pageable pageable) {
        log.debug("^ trying to get list of all teams");
        return teamRepository.findAllExceptVirtual(pageable);
    }

    /**
     * Get the list of teams for current user
     */
    @Override
    public Page<Team> getTeamListByUser(Pageable pageable, User user) {
        log.debug("^ trying to get list of all teams, where user is a participant");
        if (isNull(user)) {
            log.error("!> requesting getListByUser for NULL user. Check evoking clients");
            return null;
        }
        return teamRepository.findAllByUserParticipation(pageable, user);
    }

    /**
     * Expel (exclude) from requested team the specified participant.
     * Accessible only for a captain of the team
     */
    @Override
    public Team expelParticipant(Team team, TeamParticipant teamParticipant, boolean isSelfQuit) {
        if (isNull(team) || isNull(teamParticipant)) {
            log.error("!> requesting expelParticipant for NULL team '{}' or NULL participant '{}'. Check evoking clients", team, teamParticipant);
            return null;
        }
        if (!teamRepository.existsById(team.getId())) {
            log.error("!> requesting expelParticipant for non-existent team '{}'. Check evoking clients", team);
            return null;
        }
        if (!team.getParticipantList().contains(teamParticipant)) {
            log.error("!> requesting expelParticipant for exclude participant '{}' who isn't a member of the team '{}'. " +
                    "Check evoking clients", teamParticipant, team);
            return null;
        }
        log.debug("^ trying to expel participant '{}' from team '{}'", teamParticipant, team.getId());
        teamParticipantService.expelParticipant(teamParticipant, isSelfQuit);
        return teamRepository.findById(team.getId()).orElse(null);
    }

    /**
     * Disband (delete) all the band.
     * Accessible only for a captain of the team
     */
    @Override
    public void disbandTeam(Team team) {
        if (isNull(team)) {
            log.error("!> requesting disband for NULL team. Check evoking clients");
            return;
        }
        if (!teamRepository.existsById(team.getId())) {
            log.error("!> requesting disbandTeam for non-existent team '{}'. Check evoking clients", team);
            return;
        }
        log.debug("^ trying to disband the team  '{}'", team);
        team.getParticipantList().parallelStream() // expel all participants
                .forEach(p -> teamParticipantService.expelParticipant(p, false));
        team.setStatus(TeamStateType.DELETED);
        teamRepository.saveAndFlush(team); // save changes to team
    }

    /**
     * Returns a sign of team activity on active tournaments on platform
     */
    @Override
    public boolean isTeamParticipateInActiveTournament(Team team) {
        // from all active tournament
        return tournamentService.getAllActiveTournament().parallelStream()
                // find team proposal if exists
                .map(t -> tournamentProposalService.getLastProposalByTeamAndTournament(team, t))
                .filter(Objects::nonNull)
                // check if any of proposal is approved (active)
                .map(TournamentTeamProposal::getState)
                .anyMatch(ParticipationStateType.activeProposalStateList::contains);
    }

    /**
     * Returns sign of user participation in the specified team
     */
    @Override
    public TeamParticipant getParticipantOfTeamByUser(Team team, User user) {
        if (isNull(team) || isNull(user)) {
            log.error("!> requesting getParticipantOfTeamByUser for NULL team ot NULL user. Check evoking clients");
            return null;
        }
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

package com.freetonleague.core.service.implementations;

import com.freetonleague.core.domain.dto.TournamentTeamParticipantDto;
import com.freetonleague.core.domain.dto.TournamentTeamProposalBaseDto;
import com.freetonleague.core.domain.dto.TournamentTeamProposalDto;
import com.freetonleague.core.domain.enums.ParticipationStateType;
import com.freetonleague.core.domain.enums.TournamentStatusType;
import com.freetonleague.core.domain.enums.TournamentTeamParticipantStatusType;
import com.freetonleague.core.domain.enums.TournamentTeamType;
import com.freetonleague.core.domain.model.*;
import com.freetonleague.core.exception.*;
import com.freetonleague.core.mapper.TournamentProposalMapper;
import com.freetonleague.core.security.permissions.CanManageTournament;
import com.freetonleague.core.service.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;

import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;
import static org.apache.commons.lang3.StringUtils.isBlank;

/**
 * Service-facade for managing tournament team proposal and team composition
 */
@Slf4j
@RequiredArgsConstructor
@Service
public class RestTournamentProposalFacadeImpl implements RestTournamentProposalFacade {

    private final RestTeamFacade restTeamFacade;
    private final TournamentService tournamentService;
    private final RestTournamentFacade restTournamentFacade;
    private final TournamentProposalService tournamentProposalService;
    private final TournamentProposalMapper tournamentProposalMapper;

    /**
     * Get team proposal for tournament
     */
    @Override
    public TournamentTeamProposalDto getProposalFromTeamForTournament(long tournamentId, long teamId, User user) {
        Team team = restTeamFacade.getVerifiedTeamById(teamId, user, false);
        Tournament tournament = restTournamentFacade.getVerifiedTournamentById(tournamentId, user, false);
        TournamentTeamProposal teamProposal = tournamentProposalService.getProposalByTeamAndTournament(team, tournament);
        return tournamentProposalMapper.toDto(teamProposal);
    }

    /**
     * Get team proposal list for tournament
     */
    @Override
    public Page<TournamentTeamProposalBaseDto> getProposalListForTournament(Pageable pageable, long tournamentId, User user) {
        Tournament tournament = restTournamentFacade.getVerifiedTournamentById(tournamentId, user, false);
        return tournamentProposalService.getProposalListForTournament(pageable, tournament).map(tournamentProposalMapper::toDto);
    }

    /**
     * Registry new team to tournament
     */
    @Override
    public TournamentTeamProposalDto createProposalToTournament(long tournamentId, long teamId,
                                                                TournamentTeamProposalDto teamProposalDto, User user) {
        Team team = restTeamFacade.getVerifiedTeamById(teamId, user, false);
        if (!team.isCaptain(user)) {
            log.warn("~ forbiddenException for create proposal to tournament for user {} from team {}.", user, team);
            throw new TeamParticipantManageException(ExceptionMessages.TOURNAMENT_TEAM_PROPOSAL_FORBIDDEN_ERROR,
                    "Only captain can apply and modify proposals to tournaments from team.");
        }
        Tournament tournament = restTournamentFacade.getVerifiedTournamentById(tournamentId, user, false);

        //check if proposal already existed
        TournamentTeamProposal teamProposal = tournamentProposalService.getProposalByTeamAndTournament(team, tournament);
        if (nonNull(teamProposal)) {
            log.warn("~ forbiddenException for create duplicate proposal from user {}. Already existed proposal {}.", user, teamProposal);
            throw new TeamParticipantManageException(ExceptionMessages.TOURNAMENT_TEAM_PROPOSAL_EXIST_ERROR,
                    "Duplicate proposal from team to the one tournament is prohibited. Request rejected.");
        }

        //check status of tournament
        if (tournament.getStatus() != TournamentStatusType.SIGN_UP) {
            log.warn("~ forbiddenException for create new proposal for team {} by user {} to tournament id {} and status {}. " +
                            "Tournament is closed for new proposals",
                    team.getId(), user, tournament.getId(), tournament.getStatus());
            throw new TeamParticipantManageException(ExceptionMessages.TOURNAMENT_TEAM_PROPOSAL_VERIFICATION_ERROR,
                    String.format("Tournament '%s' is closed for new proposals and have status '%s'. Request rejected.",
                            tournament.getId(), tournament.getStatus()));
        }

        TournamentTeamProposal newTeamProposal = TournamentTeamProposal.builder()
                .state(ParticipationStateType.CREATED)
                .type(TournamentTeamType.SOLID)
                .team(team)
                .tournament(tournament)
                .build();

        TournamentTeamProposal finalNewTeamProposal = newTeamProposal; // created final var just for parallelStream work
        List<TournamentTeamParticipant> tournamentTeamParticipantList = team.getParticipantList().parallelStream()
                .map(p -> this.createTournamentTeamParticipant(p, finalNewTeamProposal))
                .collect(Collectors.toList());
        newTeamProposal.setTournamentTeamParticipantList(tournamentTeamParticipantList);

        // Verify team participants, team bank account balance and other business staff
        this.verifyBusinessLogicOnTeamToParticipateTournament(tournament, team, tournamentTeamParticipantList);
        //save proposal
        newTeamProposal = tournamentProposalService.addProposal(newTeamProposal);
        if (isNull(newTeamProposal)) {
            log.error("!> error while creating tournament team proposal by team {} for tournament {} by user {}.", teamId, tournamentId, user);
            throw new TournamentManageException(ExceptionMessages.TOURNAMENT_TEAM_PROPOSAL_CREATION_ERROR,
                    "Team proposal was not saved on Portal. Check requested params.");
        }
        return tournamentProposalMapper.toDto(newTeamProposal);
    }

    /**
     * Edit team proposal to tournament (only state)
     */
    @CanManageTournament
    @Override
    public TournamentTeamProposalDto editProposalToTournament(Long tournamentId, Long teamId, Long teamProposalId,
                                                              ParticipationStateType teamProposalState, User user) {
        TournamentTeamProposal teamProposal;
        if (nonNull(teamProposalId)) {
            teamProposal = this.getVerifiedTeamProposalById(teamProposalId, user, false);
        } else if (nonNull(teamId) && nonNull(tournamentId)) {
            Team team = restTeamFacade.getVerifiedTeamById(teamId, user, false);
            //TODO enable editing proposal by Team Capitan
//            if (!team.isCaptain(user) || user.isAdmin()) {
//                log.warn("~ forbiddenException for modify proposal to tournament for user {} from team {}.", user, team);
//                throw new TeamParticipantManageException(ExceptionMessages.TOURNAMENT_TEAM_PROPOSAL_FORBIDDEN_ERROR,
//                        "Only captain can apply and modify proposals to tournaments from team.");
//            }
            Tournament tournament = restTournamentFacade.getVerifiedTournamentById(tournamentId, user, false);
            teamProposal = tournamentProposalService.getProposalByTeamAndTournament(team, tournament);
        } else {
            log.warn("~ forbiddenException for modify proposal to tournament for user {}. " +
                    "No valid parameters of team proposal was specified", user);
            throw new TeamParticipantManageException(ExceptionMessages.TOURNAMENT_TEAM_PROPOSAL_VALIDATION_ERROR,
                    "No valid parameters of team proposal was specified. Request rejected.");
        }
        if (isNull(teamProposal)) {
            log.debug("^ Tournament team proposal with requested parameters tournamentId {}, teamId {}, teamProposalId {} was not found. " +
                    "'editProposalToTournament' in RestTournamentTeamFacadeImpl request denied", tournamentId, teamId, teamProposalId);
            throw new TeamManageException(ExceptionMessages.TOURNAMENT_TEAM_PROPOSAL_NOT_FOUND_ERROR, "Tournament team proposal with requested id " + teamProposalId + " was not found");
        }

        teamProposal.setState(teamProposalState);
        TournamentTeamProposal savedTeamProposal = tournamentProposalService.editProposal(teamProposal);
        if (isNull(savedTeamProposal)) {
            log.error("!> error while modifying tournament team proposal {} for user {}.", teamProposal, user);
            throw new TournamentManageException(ExceptionMessages.TOURNAMENT_TEAM_PROPOSAL_MODIFICATION_ERROR,
                    "Team proposal was not saved on Portal. Check requested params.");
        }
        return tournamentProposalMapper.toDto(savedTeamProposal);
    }

    /**
     * Quit team from tournament
     */
    @CanManageTournament
    @Override
    public void quitFromTournament(long tournamentId, long teamId, User user) {
        Team team = restTeamFacade.getVerifiedTeamById(teamId, user, false);
        //TODO enable editing proposal by Team Capitan
//        if (!team.isCaptain(user)) {
//            log.warn("~ forbiddenException for modify proposal to tournament for user {} from team {}.", user, team);
//            throw new TeamParticipantManageException(ExceptionMessages.TOURNAMENT_TEAM_PROPOSAL_FORBIDDEN_ERROR,
//                    "Only captain can apply and modify proposals to tournaments from team.");
//        }
        Tournament tournament = restTournamentFacade.getVerifiedTournamentById(tournamentId, user, false);

        // check if tournament is already started
        if (TournamentStatusType.startedStatusList.contains(tournament.getStatus())) {
            log.warn("~ forbiddenException for modify proposal to started tournament.id {} for user {} from team {}.",
                    tournament.getId(), user, team);
            throw new TeamParticipantManageException(ExceptionMessages.TOURNAMENT_TEAM_PROPOSAL_QUIT_ERROR,
                    "Quit from already started or finished tournament is prohibited. Request is rejected.");
        }
        TournamentTeamProposal teamProposal = tournamentProposalService.getProposalByTeamAndTournament(team, tournament);

        // check if proposal is active
        if (!ParticipationStateType.activeProposalStateList.contains(teamProposal.getState())) {
            log.warn("~ forbiddenException for modify non-active proposal with state {} to tournament.id {} for user {} from team {}.",
                    teamProposal.getState(), tournament.getId(), user, team);
            throw new TeamParticipantManageException(ExceptionMessages.TOURNAMENT_TEAM_PROPOSAL_QUIT_ERROR,
                    String.format("Modify non-active proposal with state '%s' is prohibited. Request is rejected.",
                            teamProposal.getState()));
        }

        teamProposal = tournamentProposalService.quitFromTournament(teamProposal);
        if (isNull(teamProposal)) {
            log.error("!> error while quiting from tournament id {} for team id {} by user {}.", tournament.getId(), team.getId(), user);
            throw new TournamentManageException(ExceptionMessages.TOURNAMENT_TEAM_PROPOSAL_CREATION_ERROR,
                    "Team proposal was not modified on Portal and rejected by system. Check requested params.");
        }
    }

    /**
     * Returns tournament team proposal by id and user with privacy check
     */
    @Override
    public TournamentTeamProposal getVerifiedTeamProposalById(long id, User user, boolean checkUser) {
        if (checkUser && isNull(user)) {
            log.debug("^ user is not authenticate. 'getVerifiedTeamProposalById' in RestTournamentTeamFacadeImpl request denied");
            throw new UnauthorizedException(ExceptionMessages.AUTHENTICATION_ERROR, "'getVerifiedTeamProposalById' request denied");
        }

        TournamentTeamProposal tournamentTeamProposal = tournamentProposalService.getProposalById(id);
        if (isNull(tournamentTeamProposal)) {
            log.debug("^ Tournament team proposal with requested id {} was not found. 'getVerifiedTeamProposalById' in RestTournamentTeamFacadeImpl request denied", id);
            throw new TeamManageException(ExceptionMessages.TOURNAMENT_TEAM_PROPOSAL_NOT_FOUND_ERROR, "Tournament team proposal  with requested id " + id + " was not found");
        }
        //TODO check logic and make decision about need in restrict proposal modification for orgs
//        if (tournamentTeamProposal.getState().isRejected()) {
//            log.debug("^ Tournament team proposal with requested id {} was rejected by orgs. " +
//                            "'getVerifiedTeamProposalById' in RestTournamentTeamFacadeImpl request denied", id);
//            throw new TeamManageException(ExceptionMessages.TOURNAMENT_TEAM_PROPOSAL_VISIBLE_ERROR,
//                    "Tournament team proposal with requested id " + id + " was rejected by orgs. Modifying request denied");
//        }
        return tournamentTeamProposal;
    }

    /**
     * Getting participant by TournamentTeamParticipantDto, verify team membership
     */
    public TournamentTeamParticipant getVerifiedTournamentTeamParticipantByDto(TournamentTeamParticipantDto tournamentTeamParticipantDto, TournamentTeamProposal tournamentTeamProposal) {
        return this.getVerifiedTournamentTeamParticipantById(tournamentTeamParticipantDto.getId(), tournamentTeamProposal);
    }

    /**
     * Getting participant by id, verify team membership
     */
    public TournamentTeamParticipant getVerifiedTournamentTeamParticipantById(long tournamentTeamParticipantId, TournamentTeamProposal tournamentTeamProposal) {
        if (isNull(tournamentTeamProposal)) {
            log.error("^ requested getVerifiedTournamentTeamParticipant for NULL tournamentTeamProposal. Check evoking clients");
            return null;
        }
        TournamentTeamParticipant tournamentTeamParticipant = tournamentProposalService.getTournamentTeamParticipantById(tournamentTeamParticipantId);
        if (isNull(tournamentTeamParticipant)) {
            log.debug("^ Tournament team participant with requested id {} was not found. 'getVerifiedTournamentTeamParticipant' request denied",
                    tournamentTeamParticipantId);
            throw new TeamParticipantManageException(ExceptionMessages.TOURNAMENT_TEAM_PARTICIPANT_NOT_FOUND_ERROR,
                    "Tournament team participant with requested id " + tournamentTeamParticipantId + " was not found");
        }
        if (!tournamentTeamParticipant.getTournamentTeamProposal().equals(tournamentTeamProposal)) {
            log.warn("~ parameter 'tournamentTeamParticipantId' is not match specified tournamentTeamProposal for getVerifiedTournamentTeamParticipant");
            throw new ValidationException(ExceptionMessages.TOURNAMENT_SETTINGS_VALIDATION_ERROR, "tournamentTeamParticipantId",
                    "parameter 'tournamentTeamParticipantId' is not match specified tournamentTeamProposal for getVerifiedTournamentTeamParticipant");
        }
        return tournamentTeamParticipant;
    }

    /**
     * Verify team and it's settings to create participation on tournament
     */
    private void verifyBusinessLogicOnTeamToParticipateTournament(Tournament tournament, Team team,
                                                                  List<TournamentTeamParticipant> tournamentTeamParticipantList) {
        if (isNull(team) || isNull(tournament) || isNull(tournamentTeamParticipantList) || tournamentTeamParticipantList.isEmpty()) {
            log.error("!> requesting validateTeamToParticipateTournament for NULL team {} or NULL tournament {} " +
                            "or BLANK tournamentTeamParticipantList {}. Check evoking clients",
                    team, tournament, tournamentTeamParticipantList);
            throw new TournamentManageException(ExceptionMessages.TOURNAMENT_TEAM_PROPOSAL_VERIFICATION_ERROR,
                    "Team cant participate on tournament. Check requested params.");
        }

        AtomicReference<TournamentTeamParticipant> lastTeamParticipant = new AtomicReference<>();

        if (tournamentTeamParticipantList.parallelStream()
                .anyMatch(p -> {
                    lastTeamParticipant.set(p);
                    return isBlank(p.getUser().getDiscordId());
                })) {
            log.warn("~ requesting validateTeamToParticipateTournament for team {} with participant without Discord reference. At least for {}",
                    team, lastTeamParticipant.get());
            throw new TournamentManageException(ExceptionMessages.TOURNAMENT_TEAM_PROPOSAL_VERIFICATION_ERROR,
                    String.format("Team cant participate on tournament. There are team participant without Discord reference. At least for user with login '%s'",
                            lastTeamParticipant.get().getUser().getUsername()));
        }
    }

    /**
     * Returns new TournamentTeamParticipant from specified parameters
     */
    private TournamentTeamParticipant createTournamentTeamParticipant(TeamParticipant teamParticipant, TournamentTeamProposal teamProposal) {
        return TournamentTeamParticipant.builder()
                .status(TournamentTeamParticipantStatusType.MAIN) //TODO change logic to getting team-participants type form requested DTO
                .teamParticipant(teamParticipant)
                .tournamentTeamProposal(teamProposal)
                .user(teamParticipant.getUser())
                .build();
    }

    // TODO make creation proposal from DTO and process it with getVerifiedTeamProposalByDto
//    /**
//     * Getting tournament settings by DTO with privacy check
//     */
//    @Override
//    public TournamentTeamProposal getVerifiedTeamProposalByDto(TournamentTeamProposalBaseDto tournamentTeamProposalDto, User user) {
//        if (isNull(tournamentTeamProposalDto)) {
//            log.warn("~ parameter 'tournamentTeamProposalDto' is NULL for getVerifiedTeamProposalByDto");
//            throw new ValidationException(ExceptionMessages.TOURNAMENT_TEAM_PROPOSAL_VALIDATION_ERROR, "tournamentTeamProposalDto",
//                    "parameter 'tournamentTeamProposalDto' is not set for get or modify tournament team proposal");
//        }
//        if (isNull(tournamentTeamProposalDto.getTeam())) {
//            log.warn("~ parameter 'team' is not set in tournamentTeamProposalDto for getVerifiedTeamProposalByDto");
//            throw new ValidationException(ExceptionMessages.TOURNAMENT_TEAM_PROPOSAL_VALIDATION_ERROR, "team",
//                    "parameter 'team' is not set in tournamentTeamProposalDto for get or modify tournament team proposal");
//        }
//        Team team = restTeamFacade.getVerifiedTeamById(tournamentTeamProposalDto.getTeam().getId(), user, false);
//
//        if (isNull(tournamentTeamProposalDto.getTeam())) {
//            log.warn("~ parameter 'team' is not set in tournamentTeamProposalDto for getVerifiedTeamProposalByDto");
//            throw new ValidationException(ExceptionMessages.TOURNAMENT_TEAM_PROPOSAL_VALIDATION_ERROR, "team",
//                    "parameter 'team' is not set in tournamentTeamProposalDto for get or modify tournament team proposal");
//        }
//        Tournament tournament = restTournamentFacade.getVerifiedTournamentById(tournamentTeamProposalDto.ge().getId(), user, false);
//
//        Set<ConstraintViolation<TournamentTeamProposalBaseDto>> violations = validator.validate(tournamentTeamProposalDto);
//        if (!violations.isEmpty()) {
//            log.debug("^ transmitted tournament team proposal dto: {} have constraint violations: {}",
//                    tournamentTeamProposalDto, violations);
//            throw new ConstraintViolationException(violations);
//        }
//
//        // Check existence of tournament team proposal and it's status
//        if (nonNull(tournamentTeamProposalDto.getId())) {
//            TournamentTeamProposal existedTournamentTeamProposal = this.getVerifiedTeamProposalById(tournamentTeamProposalDto.getId(), user);
//        }
//        TournamentTeamProposal tournamentTeamProposal = tournamentProposalMapper.fromBaseDto(tournamentTeamProposalDto);
//        tournamentRound.setTournament(tournament);
//        return tournamentRound;
//    }
}



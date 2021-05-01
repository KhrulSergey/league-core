package com.freetonleague.core.service.implementations;

import com.freetonleague.core.domain.dto.*;
import com.freetonleague.core.domain.enums.TournamentStatusType;
import com.freetonleague.core.domain.model.*;
import com.freetonleague.core.exception.ExceptionMessages;
import com.freetonleague.core.exception.TournamentManageException;
import com.freetonleague.core.exception.UnauthorizedException;
import com.freetonleague.core.exception.ValidationException;
import com.freetonleague.core.mapper.TournamentMapper;
import com.freetonleague.core.security.permissions.CanManageTournament;
import com.freetonleague.core.service.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import javax.validation.Validator;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;
import static org.apache.commons.lang3.ObjectUtils.isEmpty;
import static org.apache.commons.lang3.ObjectUtils.isNotEmpty;
import static org.apache.commons.lang3.StringUtils.isBlank;

@Slf4j
@RequiredArgsConstructor
@Service
public class RestTournamentFacadeImpl implements RestTournamentFacade {

    private final TournamentService tournamentService;
    private final TournamentTeamService tournamentTeamService;
    private final TournamentOrganizerService tournamentOrganizerService;
    private final RestUserFacade restUserFacade;
    private final TournamentMapper tournamentMapper;
    private final RestGameDisciplineFacade gameDisciplineFacade;
    private final Validator validator;

    @Lazy
    @Autowired
    private RestTournamentTeamFacade restTournamentTeamFacade;

    /**
     * Returns founded tournament by id
     */
    @Override
    public TournamentDto getTournament(long id, User user) {
        return tournamentMapper.toDto(this.getVerifiedTournamentById(id, user, false));
    }

    /**
     * Returns list of all teams filtered by requested params with detailed info
     */
    @CanManageTournament
    @Override
    public Page<TournamentDto> getTournamentDetailedList(Pageable pageable, User user, String creatorLeagueId, List<TournamentStatusType> statusList) {
        User creatorUser = null;
        if (!isBlank(creatorLeagueId)) {
            creatorUser = restUserFacade.getVerifiedUserByLeagueId(creatorLeagueId);
        }
        return tournamentService.getTournamentList(pageable, creatorUser, statusList).map(tournamentMapper::toDto);
    }

    /**
     * Returns list of all teams filtered by requested params
     */
    @Override
    public Page<TournamentDto> getTournamentList(Pageable pageable, User user, List<TournamentStatusType> statusList) {
        return tournamentService.getTournamentList(pageable, null, statusList).map(tournamentMapper::toDto);
    }

    /**
     * Add new tournament to DB.
     */
    @CanManageTournament
    @Override
    public TournamentDto addTournament(TournamentDto tournamentDto, User user) {
        tournamentDto.setId(null);
        tournamentDto.setStatus(TournamentStatusType.CREATED);
        if (isNull(tournamentDto.getTournamentSettings())) {
            log.warn("~ parameter 'tournament settings' is NULL for addTournament. Request rejected.");
            throw new ValidationException(ExceptionMessages.TOURNAMENT_SETTINGS_VALIDATION_ERROR, "tournament settings",
                    "parameter 'tournament settings' is not set for addTournament. Please provide correct settings data.");
        }
        tournamentDto.getTournamentSettings().setId(null);
        tournamentDto.getTournamentSettings().setTournamentId(null);

        Tournament newTournament = this.getVerifiedTournamentByDto(tournamentDto, user);
        newTournament = tournamentService.addTournament(newTournament);

        if (isNull(newTournament)) {
            log.error("!> error while creating tournament from dto {} for user {}.", tournamentDto, user);
            throw new TournamentManageException(ExceptionMessages.TOURNAMENT_CREATION_ERROR,
                    "Tournament was not saved on Portal. Check requested params.");
        }
        return tournamentMapper.toDto(newTournament);
    }

    /**
     * Edit tournament in DB.
     */
    @CanManageTournament
    @Override
    public TournamentDto editTournament(TournamentDto tournamentDto, User user) {
        Tournament newTournament = this.getVerifiedTournamentByDto(tournamentDto, user);

        if (isNull(tournamentDto.getId())) {
            log.warn("~ parameter 'tournament id' is not set for editTournament");
            throw new ValidationException(ExceptionMessages.TOURNAMENT_SETTINGS_VALIDATION_ERROR, "tournament id",
                    "parameter 'tournament id' is not set for editTournament");
        }

        Tournament tournament = this.getVerifiedTournamentById(tournamentDto.getId(), user, true);
        if (tournamentDto.getStatus().isDeleted()) {
            log.warn("~ tournament deleting was declined in editTournament. This operation should be done with specific method.");
            throw new TournamentManageException(ExceptionMessages.TOURNAMENT_STATUS_DELETE_ERROR,
                    "Modifying tournament was rejected. Check requested params and method.");
        }

        if (isNull(tournamentDto.getTournamentSettings().getId())) {
            log.warn("~ parameter 'tournament settings id' is not set for editTournament");
            throw new ValidationException(ExceptionMessages.TOURNAMENT_SETTINGS_VALIDATION_ERROR, "tournament settings id",
                    "parameter 'tournament settings id' is not set for editTournament");
        }
        if (!tournamentDto.getTournamentSettings().getId().equals(tournament.getTournamentSettings().getId())) {
            log.warn("~ parameter 'tournament settings' is not match by id to tournament for editTournament");
            throw new ValidationException(ExceptionMessages.TOURNAMENT_SETTINGS_VALIDATION_ERROR, "tournament settings",
                    "parameter 'tournament settings' is not match by id to tournament for editTournament");
        }

        //Match can be finished only with setting the winner of the match
        if (this.verifyTournamentStatusBadlyFinished(tournamentDto.getStatus(), tournamentDto.getTournamentWinnerList(),
                tournamentDto.getIsForcedFinished())) {
            log.warn("~ tournament can be finished only with setting the winners of the tournament and flag isForcedFinished. " +
                    "Request to set status {} and winners {} was rejected.", tournamentDto.getStatus(), tournamentDto.getTournamentWinnerList());
            throw new TournamentManageException(ExceptionMessages.TOURNAMENT_STATUS_FINISHED_ERROR,
                    "Modifying tournament was rejected. Check requested params and method.");
        }
        newTournament = tournamentService.editTournament(newTournament);
        if (isNull(newTournament)) {
            log.error("!> error while modifying tournament from dto {} for user {}.", tournamentDto, user);
            throw new TournamentManageException(ExceptionMessages.TOURNAMENT_MODIFICATION_ERROR,
                    "Tournament was not updated on Portal. Check requested params.");
        }
        return tournamentMapper.toDto(newTournament);
    }


    /**
     * Delete (mark) tournament in DB.
     * Accessible only for admin
     */
    @CanManageTournament
    @Override
    public TournamentDto deleteTournament(long id, User user) {
        Tournament tournament = this.getVerifiedTournamentById(id, user, true);
        tournament = tournamentService.deleteTournament(tournament);

        if (isNull(tournament)) {
            log.error("!> error while deleting tournament with id {} for user {}.", id, user);
            throw new TournamentManageException(ExceptionMessages.TOURNAMENT_MODIFICATION_ERROR,
                    "Tournament was not deleted on Portal. Check requested params.");
        }
        return tournamentMapper.toDto(tournament);
    }

    /**
     * Returns discord info for active tournament list with embedded data of approved tournament team participants
     */
    @Override
    public TournamentDiscordInfoListDto getDiscordChannelsForActiveTournament() {
        List<TournamentDiscordChannelDto> tournamentDiscordInfoList = tournamentService.getAllActiveTournament()
                .parallelStream().map(this::composeDiscordChannelInfoForTournament).collect(Collectors.toList());
        return TournamentDiscordInfoListDto.builder()
                .rooms(tournamentDiscordInfoList)
                .build();
    }

    /**
     * Getting tournament by id and user with privacy check
     */
    @Override
    public Tournament getVerifiedTournamentById(long id, User user, boolean checkUser) {
        if (checkUser && isNull(user)) {
            log.debug("^ user is not authenticate. 'getVerifiedTournamentById' in RestTournamentFacadeImpl request denied");
            throw new UnauthorizedException(ExceptionMessages.AUTHENTICATION_ERROR, "'getVerifiedTournamentById' request denied");
        }
        Tournament tournament = tournamentService.getTournament(id);
        if (isNull(tournament)) {
            log.debug("^ Tournament with requested id {} was not found. 'getVerifiedTournamentById' in RestTournamentFacadeImpl request denied", id);
            throw new TournamentManageException(ExceptionMessages.TOURNAMENT_NOT_FOUND_ERROR, "Tournament with requested id " + id + " was not found");
        }
        if (tournament.getStatus().isDeleted()) {
            log.debug("^ Tournament with requested id {} was {}. 'getVerifiedTournamentById' in RestTournamentFacadeImpl request denied", id, tournament.getStatus());
            throw new TournamentManageException(ExceptionMessages.TOURNAMENT_VISIBLE_ERROR, "Visible tournament with requested id " + id + " was not found");
        }
        return tournament;
    }

    private TournamentDiscordChannelDto composeDiscordChannelInfoForTournament(Tournament tournament) {
        Set<String> tournamentInvolvedUsersDiscordIdList = tournamentTeamService.getActiveTeamProposalListByTournament(tournament)
                .parallelStream()
                .map(tournamentTeamService::getUserDiscordIdListFromTeamProposal)
                .flatMap(Collection::stream)
                .collect(Collectors.toSet());
        return TournamentDiscordChannelDto.builder()
                .discordChannelId(tournament.getDiscordChannelId())
                .gameDisciplineName(tournament.getGameDiscipline().getName())
                .tournamentGUID(tournament.getCoreId().toString())
                .tournamentInvolvedUsersDiscordIdList(tournamentInvolvedUsersDiscordIdList)
                .build();
    }

    /**
     * Getting tournament by DTO and user with deep validation and privacy check
     */
    private Tournament getVerifiedTournamentByDto(TournamentDto tournamentDto, User user) {
        if (isNull(user)) {
            log.debug("^ user is not authenticate. 'addTournament' request denied");
            throw new UnauthorizedException(ExceptionMessages.AUTHENTICATION_ERROR, "'addTournament' request denied");
        }

        // Verify base Tournament information
        Set<ConstraintViolation<TournamentDto>> violations = validator.validate(tournamentDto);
        if (!violations.isEmpty()) {
            log.debug("^ transmitted TournamentDto: {} have constraint violations: {}", tournamentDto, violations);
            throw new ConstraintViolationException(violations);
        }
        //Verify Tournament settings
        TournamentSettings newTournamentSettings = this.getVerifyTournamentSettingsByDto(tournamentDto.getTournamentSettings());

        //Verify Tournament organizers
        List<TournamentOrganizerDto> tournamentOrganizerDtoList = tournamentDto.getTournamentOrganizerList();
        List<TournamentOrganizer> tournamentOrganizers = null;
        if (isNotEmpty(tournamentOrganizerDtoList)) {
            Set<String> organizerUserLeagueId = tournamentOrganizerDtoList.stream().map(TournamentOrganizerDto::getUserLeagueId).collect(Collectors.toSet());
            if (organizerUserLeagueId.size() != tournamentOrganizerDtoList.size()) {
                log.warn("~ parameter 'tournament organizers' is not correctly set for getVerifiedTournamentByDto. " +
                        "There are duplicates by LeagueId in organizers list");
                throw new ValidationException(ExceptionMessages.TOURNAMENT_SETTINGS_VALIDATION_ERROR, "tournament organizers",
                        "parameter 'tournament organizers' is not correctly set for getVerifiedTournamentByDto. There are duplicates by LeagueId in organizers list.");
            }
            tournamentOrganizers = tournamentOrganizerDtoList.parallelStream()
                    .map(this::getVerifiedTournamentOrganizerByDto).filter(Objects::nonNull).collect(Collectors.toList());
        }

        //Verify Tournament winner list
        List<TournamentWinnerDto> tournamentWinnerDtoList = tournamentDto.getTournamentWinnerList();
        List<TournamentWinner> tournamentWinnerList = null;
        if (isNotEmpty(tournamentWinnerDtoList)) {
            if (isNull(tournamentDto.getId())) {
                log.warn("~ parameter 'tournament id' is not set to verify tournament winner list in getVerifiedTournamentByDto. " +
                        "Can't set winner to undefined tournament");
                throw new ValidationException(ExceptionMessages.TOURNAMENT_SETTINGS_VALIDATION_ERROR, "tournament id",
                        "parameter 'tournament id' is not set to verify tournament winner list in getVerifiedTournamentByDto." +
                                " Can't set winner to undefined tournament.");
            }

            Set<Long> winnerTeamProposalIdList = tournamentWinnerDtoList.parallelStream()
                    .map(TournamentWinnerDto::getTeamProposalId).collect(Collectors.toSet());
            if (winnerTeamProposalIdList.size() != tournamentWinnerDtoList.size()) {
                log.warn("~ parameter 'tournament winner list' is not correctly set for getVerifiedTournamentByDto. " +
                        "There are duplicates by Team Proposal Id in winner list");
                throw new ValidationException(ExceptionMessages.TOURNAMENT_SETTINGS_VALIDATION_ERROR, "tournament winners",
                        "parameter 'tournament winner' is not correctly set for getVerifiedTournamentByDto. There are duplicates by teamProposal.id in winner list.");
            }

            tournamentWinnerList = tournamentWinnerDtoList.parallelStream()
                    .map(winner -> this.getVerifiedTournamentWinnerByDto(winner, tournamentDto.getId()))
                    .filter(Objects::nonNull).collect(Collectors.toList());
        }

        //Verify Discipline and its Settings for Tournament
        GameDiscipline gameDiscipline = gameDisciplineFacade.getVerifiedDiscipline(tournamentDto.getGameDisciplineId(), user);
        GameDisciplineSettings gameDisciplineSettings = gameDisciplineFacade.getVerifiedDisciplineSettings(
                tournamentDto.getGameDisciplineSettingsId(), gameDiscipline, user);

        // Collect all data for Tournament and save it
        Tournament newTournament = tournamentMapper.fromDto(tournamentDto);
        newTournament.setTournamentOrganizerList(tournamentOrganizers);
        newTournament.setTournamentWinnerList(tournamentWinnerList);
        if (nonNull(tournamentOrganizers)) {
            tournamentOrganizers.parallelStream().forEach(tOrg -> tOrg.setTournament(newTournament));
        }

        newTournament.setGameDiscipline(gameDiscipline);
        newTournament.setGameDisciplineSettings(gameDisciplineSettings);
        // Connect tournament settings with tournament
        newTournament.setTournamentSettings(newTournamentSettings);
        newTournamentSettings.setTournament(newTournament);

        return newTournament;
    }

    /**
     * Getting tournament settings by DTO with privacy check
     */
    private TournamentSettings getVerifyTournamentSettingsByDto(TournamentSettingsDto tournamentSettingsDto) {
        if (isNull(tournamentSettingsDto)) {
            log.warn("~ parameter 'tournament settings' is not set for getVerifyTournamentSettingsByDto");
            throw new ValidationException(ExceptionMessages.TOURNAMENT_SETTINGS_VALIDATION_ERROR, "tournament settings",
                    "parameter 'tournament settings' is not set for getVerifyTournamentSettingsByDto");
        }
        Set<ConstraintViolation<TournamentSettingsDto>> settingsViolations = validator.validate(tournamentSettingsDto);
        if (!settingsViolations.isEmpty()) {
            log.debug("^ transmitted tournament with embedded TournamentSettingsDto: {} have constraint violations: {}",
                    tournamentSettingsDto, settingsViolations);
            throw new ConstraintViolationException(settingsViolations);
        }
        tournamentSettingsDto.getPrizePoolDistribution().parallelStream()
                .forEach(this::verifyTournamentPrizePoolDistributionDto);
        tournamentSettingsDto.getQuitPenaltyDistribution().parallelStream()
                .forEach(this::verifyTournamentQuitPenaltyDistributionDto);

        return tournamentMapper.fromDto(tournamentSettingsDto);
    }

    /**
     * Verify TournamentPrizePoolDistributionDto with validation and privacy check
     */
    private void verifyTournamentPrizePoolDistributionDto(TournamentPrizePoolDistributionDto prizePoolDistribution) {
        if (isNull(prizePoolDistribution)) {
            log.warn("~ parameter 'prizePoolDistribution' is not correctly set for verifyTournamentPrizePoolDistributionDto");
            throw new ValidationException(ExceptionMessages.TOURNAMENT_SETTINGS_VALIDATION_ERROR, "prizePoolDistribution",
                    "parameter prizePoolDistribution is not correctly set for verifyTournamentPrizePoolDistributionDto");
        }
        Set<ConstraintViolation<TournamentPrizePoolDistributionDto>> settingsViolations = validator.validate(prizePoolDistribution);
        if (!settingsViolations.isEmpty()) {
            log.debug("^ transmitted tournament with embedded TournamentPrizePoolDistributionDto: {} have constraint violations: {}",
                    prizePoolDistribution, settingsViolations);
            throw new ConstraintViolationException(settingsViolations);
        }
    }

    /**
     * Verify TournamentQuitPenaltyDistributionDto with validation and privacy check
     */
    private void verifyTournamentQuitPenaltyDistributionDto(TournamentQuitPenaltyDistributionDto quitPenaltyDistributionDto) {
        if (isNull(quitPenaltyDistributionDto)) {
            log.warn("~ parameter 'quitPenaltyDistributionDto' is not correctly set for verifyTournamentQuitPenaltyDistributionDto");
            throw new ValidationException(ExceptionMessages.TOURNAMENT_SETTINGS_VALIDATION_ERROR, "quitPenaltyDistributionDto",
                    "parameter quitPenaltyDistributionDto is not correctly set for verifyTournamentQuitPenaltyDistributionDto");
        }
        Set<ConstraintViolation<TournamentQuitPenaltyDistributionDto>> settingsViolations = validator.validate(quitPenaltyDistributionDto);
        if (!settingsViolations.isEmpty()) {
            log.debug("^ transmitted tournament with embedded TournamentQuitPenaltyDistributionDto: {} have constraint violations: {}",
                    quitPenaltyDistributionDto, settingsViolations);
            throw new ConstraintViolationException(settingsViolations);
        }
    }

    /**
     * Getting tournament organizer by DTO with entity and privacy check
     */
    private TournamentWinner getVerifiedTournamentWinnerByDto(TournamentWinnerDto winnerDto, Long tournamentId) {
        if (isNull(winnerDto)) {
            log.error("^ requested getVerifiedTournamentWinnerByDto for NULL winnerDto. Check evoking clients");
            return null;
        }
        Set<ConstraintViolation<TournamentWinnerDto>> settingsViolations = validator.validate(winnerDto);
        if (!settingsViolations.isEmpty()) {
            log.debug("^ transmitted tournament with embedded TournamentWinnerDto: {} have constraint violations: {}",
                    winnerDto, settingsViolations);
            throw new ConstraintViolationException(settingsViolations);
        }

        TournamentTeamProposal tournamentTeamProposal = restTournamentTeamFacade
                .getVerifiedTeamProposalById(winnerDto.getTeamProposalId(), null, false);

        if (!tournamentTeamProposal.getTournament().getId().equals(tournamentId)) {
            log.warn("~ parameter 'tournamentTeamProposal' {} is not correctly set to verify tournament winner for tournament {}" +
                            "  in getVerifiedTournamentByDto. Can't set winner with teamProposal to other tournament {}",
                    tournamentTeamProposal.getId(), tournamentId, tournamentTeamProposal.getTournament().getId());
            throw new ValidationException(ExceptionMessages.TOURNAMENT_SETTINGS_VALIDATION_ERROR, "tournamentTeamProposal",
                    "parameter 'tournamentTeamProposal' is not correctly set to verify tournament winner for tournament." +
                            " Can't set winner with teamProposal to other tournament.");
        }

        //convert data from dto to entity
        winnerDto.setTournamentId(null);
        TournamentWinner tournamentWinner = tournamentMapper.fromDto(winnerDto);
        tournamentWinner.setTeamProposal(tournamentTeamProposal);
        tournamentWinner.setTournament(tournamentTeamProposal.getTournament());
        return tournamentWinner;
    }

    /**
     * Getting tournament organizer by DTO with entity and privacy check
     */
    private TournamentOrganizer getVerifiedTournamentOrganizerByDto(TournamentOrganizerDto organizerDto) {
        if (isNull(organizerDto)) {
            log.error("^ requested getVerifiedTournamentOrganizerByDto for NULL organizerDto. Check evoking clients");
            return null;
        }
        Set<ConstraintViolation<TournamentOrganizerDto>> settingsViolations = validator.validate(organizerDto);
        if (!settingsViolations.isEmpty()) {
            log.debug("^ transmitted tournament with embedded TournamentOrganizerDto: {} have constraint violations: {}",
                    organizerDto, settingsViolations);
            throw new ConstraintViolationException(settingsViolations);
        }

        User userOrg = restUserFacade.getVerifiedUserByLeagueId(organizerDto.getUserLeagueId());
        TournamentOrganizer tournamentOrganizer;
        //Check if organizer entity id is set
        if (nonNull(organizerDto.getId())) {
            tournamentOrganizer = tournamentOrganizerService.get(organizerDto.getId());
            if (isNull(tournamentOrganizer)) {
                log.debug("^ Tournament organizer with requested id {} was not found. 'getVerifiedTournamentOrganizerByDto' in RestTournamentFacade request denied", organizerDto.getId());
                throw new TournamentManageException(ExceptionMessages.TOURNAMENT_ORGANIZER_NOT_FOUND_ERROR, "Tournament organizer with requested id " + organizerDto.getId() + " was not found");
            }
            if (!tournamentOrganizer.getTournament().getId().equals(organizerDto.getTournamentId())) {
                log.warn("~ parameter 'tournament organizer' is not match by id to tournament for getVerifiedTournamentOrganizerByDto");
                throw new ValidationException(ExceptionMessages.TOURNAMENT_ORGANIZER_VALIDATION_ERROR, "tournament organizer",
                        "parameter 'tournament organizer' is not match by id to tournament for getVerifiedTournamentOrganizerByDto");
            }
            //set important properties
            tournamentOrganizer.setPrivilegeList(organizerDto.getPrivilegeList());
            tournamentOrganizer.setUser(userOrg);
            tournamentOrganizer.setStatus(organizerDto.getStatus());
        } else {
            //convert data from dto to entity, because we have new organizer
            organizerDto.setTournamentId(null);
            tournamentOrganizer = tournamentMapper.fromDto(organizerDto);
            tournamentOrganizer.setUser(userOrg);
        }
        return tournamentOrganizer;
    }

    /**
     * Returns sign of badly set finished status od Tournament
     */
    private boolean verifyTournamentStatusBadlyFinished(TournamentStatusType status,
                                                        List<TournamentWinnerDto> tournamentWinnerList,
                                                        Boolean isForcesFinished) {
        boolean isFinishedButNotWithWinnerList = status.isFinished()
                && (isEmpty(tournamentWinnerList) || isNull(isForcesFinished) || !isForcesFinished);
        boolean isWinnerListSetButStatusNotFinished = isNotEmpty(tournamentWinnerList)
                && (!status.isFinished() || isNull(isForcesFinished) || !isForcesFinished);
        return isFinishedButNotWithWinnerList || isWinnerListSetButStatusNotFinished;
    }


}

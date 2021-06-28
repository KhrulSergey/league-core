package com.freetonleague.core.service.implementations;


import com.freetonleague.core.domain.dto.GameDisciplineIndicatorDto;
import com.freetonleague.core.domain.dto.TournamentMatchRivalDto;
import com.freetonleague.core.domain.dto.TournamentMatchRivalParticipantDto;
import com.freetonleague.core.domain.dto.TournamentTeamParticipantDto;
import com.freetonleague.core.domain.enums.GameIndicatorType;
import com.freetonleague.core.domain.enums.TournamentMatchRivalParticipantStatusType;
import com.freetonleague.core.domain.model.*;
import com.freetonleague.core.exception.TeamManageException;
import com.freetonleague.core.exception.TournamentManageException;
import com.freetonleague.core.exception.UnauthorizedException;
import com.freetonleague.core.exception.ValidationException;
import com.freetonleague.core.exception.config.ExceptionMessages;
import com.freetonleague.core.mapper.TournamentMatchRivalMapper;
import com.freetonleague.core.security.permissions.CanManageTournament;
import com.freetonleague.core.service.*;
import com.freetonleague.core.util.GameIndicatorConverter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import javax.validation.Validator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;
import static org.apache.commons.lang3.ObjectUtils.isNotEmpty;

/**
 * Service-facade for managing tournament match rival and rival participant
 */
@Slf4j
@RequiredArgsConstructor
@Service
public class RestTournamentMatchRivalFacadeImpl implements RestTournamentMatchRivalFacade {

    private final TournamentMatchRivalService tournamentMatchRivalService;
    private final TournamentService tournamentService;
    private final TournamentMatchRivalMapper tournamentMatchRivalMapper;
    private final Validator validator;

    @Lazy
    @Autowired
    private RestTournamentMatchFacade restTournamentMatchFacade;

    @Lazy
    @Autowired
    private RestTournamentProposalFacade restTournamentProposalFacade;

    /**
     * Returns founded tournament match by id
     */
    @Override
    public TournamentMatchRivalDto getMatchRival(long id) {
        return null;
    }

    /**
     * Add new tournament series to DB.
     */
    @CanManageTournament
    @Override
    public TournamentMatchRivalDto addMatchRival(TournamentMatchRivalDto tournamentMatchRivalDto) {
        return null;
    }

    /**
     * Edit tournament series in DB.
     */
    @CanManageTournament
    @Override
    public TournamentMatchRivalDto editMatchRival(TournamentMatchRivalDto tournamentMatchRivalDto) {
        return null;
    }

    /**
     * Change match rival participant for specified match.
     */
    @Override
    public TournamentMatchRivalDto changeActiveMatchRivalParticipants(long matchId, long rivalId, Set<TournamentTeamParticipantDto> rivalParticipantList, User user) {
        if (isNull(rivalParticipantList) || rivalParticipantList.isEmpty()) {
            log.warn("~ parameter 'rivalParticipantList' set empty in request for editMatchRivalParticipant");
            throw new ValidationException(ExceptionMessages.TOURNAMENT_MATCH_RIVAL_VALIDATION_ERROR, "rivalParticipantList",
                    "parameter 'rivalParticipantList' set empty in request for editMatchRivalParticipant");
        }
        if (isNull(user)) {
            log.debug("^ user is not authenticate. 'addTournament' request denied");
            throw new UnauthorizedException(ExceptionMessages.AUTHENTICATION_ERROR, "'addTournament' request denied");
        }

        // find Match and Rival entities
        TournamentMatch tournamentMatch = restTournamentMatchFacade.getVerifiedMatchById(matchId);
        TournamentMatchRival tournamentMatchRival = this.getVerifiedMatchRivalById(rivalId);
        TournamentTeamProposal tournamentTeamProposal = tournamentMatchRival.getTeamProposal();

        // Check if specified MatchRival corresponds to specified Match
        if (!tournamentMatchRival.getTournamentMatch().equals(tournamentMatch)) {
            log.warn("~ parameter 'rivalId' '{}' is not match by id to Tournament Match with 'matchId' '{}' for editMatchRivalParticipant",
                    rivalId, matchId);
            throw new ValidationException(ExceptionMessages.TOURNAMENT_MATCH_RIVAL_VALIDATION_ERROR, "rivalId",
                    "parameter 'rivalId' is not match by id to Tournament Match with 'matchId' for editMatchRivalParticipant");
        }
        // Check if current user is not Captain of specified MatchRival or is not Organizer of specified Match
        if (!tournamentService.isUserTournamentOrganizer(tournamentMatch.getTournamentSeries().getTournamentRound().getTournament(), user)
                || !tournamentTeamProposal.getTeam().isCaptain(user)) {
            log.warn("~ forbiddenException for manage active match participants from rivalId '{}' for user '{}'.",
                    rivalId, user);
            throw new TeamManageException(ExceptionMessages.TOURNAMENT_MATCH_RIVAL_FORBIDDEN_ERROR,
                    "Only captain or organizers can manage active tournament match participants from team.");
        }

        // Check if all specified rivalParticipantList corresponds to specified tournamentMatchRival
        List<TournamentTeamParticipant> tournamentTeamParticipants = rivalParticipantList.parallelStream()
                .map(p -> restTournamentProposalFacade.getVerifiedTournamentTeamParticipantByDto(p, tournamentTeamProposal))
                .filter(Objects::nonNull).collect(Collectors.toList());

        Set<TournamentMatchRivalParticipant> currentTournamentMatchRivalParticipants = tournamentMatchRival.getRivalParticipantList();
        // Collect new tournamentMatchRivalParticipants from specified tournamentTeamParticipants
        TournamentMatchRival finalTournamentMatchRival = tournamentMatchRival;
        Set<TournamentMatchRivalParticipant> newTournamentMatchRivalParticipants = tournamentTeamParticipants.parallelStream()
                .map(p -> this.createAndValidateMatchRivalParticipant(p, currentTournamentMatchRivalParticipants, finalTournamentMatchRival))
                .collect(Collectors.toSet());

        // Select distinct rivalParticipants from currentTournamentMatchRivalParticipants
        currentTournamentMatchRivalParticipants.retainAll(newTournamentMatchRivalParticipants);
        // change distinct currentTournamentMatchRivalParticipant status to Disabled and
        // to collection of newTournamentMatchRivalParticipants
        for (TournamentMatchRivalParticipant rivalParticipant : currentTournamentMatchRivalParticipants) {
            rivalParticipant.setStatus(TournamentMatchRivalParticipantStatusType.DISABLED);
            newTournamentMatchRivalParticipants.add(rivalParticipant);
        }

        // Save new match rival Participant composition
        tournamentMatchRival.setRivalParticipantList(newTournamentMatchRivalParticipants);
        tournamentMatchRival = tournamentMatchRivalService.editMatchRival(tournamentMatchRival);
        if (isNull(tournamentMatchRival)) {
            log.error("!> error while editing ActiveMatchRivalParticipants for matchId '{}', rivalId '{}', size of new rival participant '{}', for user '{}'.",
                    matchId, rivalId, rivalParticipantList.size(), user);
            throw new TournamentManageException(ExceptionMessages.TOURNAMENT_MATCH_RIVAL_PARTICIPANT_MODIFY_ERROR,
                    "Tournament match rival participant list was not saved on Portal. Check requested params.");
        }
        return tournamentMatchRivalMapper.toDto(tournamentMatchRival);
    }

    /**
     * Delete tournament match rival in DB.
     */
    @CanManageTournament
    @Override
    public void deleteMatchRival(long id, User user) {
        TournamentMatchRival tournamentMatchRival = this.getVerifiedMatchRivalById(id);
        boolean result = tournamentMatchRivalService.deleteMatchRival(tournamentMatchRival);
        if (!result) {
            log.debug("^ Tournament match rival with requested id '{}' was not deleted. 'deleteMatchRival' in " +
                    "RestTournamentMatchRivalFacade request denied", id);
            throw new TeamManageException(ExceptionMessages.TOURNAMENT_MATCH_RIVAL_MODIFY_ERROR,
                    "Tournament match rival with requested id " + id + " was not deleted");
        }
    }

    /**
     * Delete tournament match rival participant in DB.
     */
    @CanManageTournament
    @Override
    public void deleteMatchRivalParticipant(long id, User user) {
        TournamentMatchRivalParticipant matchRivalParticipant = this.getVerifiedMatchRivalParticipantById(id);
        boolean result = tournamentMatchRivalService.deleteMatchRivalParticipant(matchRivalParticipant);
        if (!result) {
            log.debug("^ Tournament match rival participant with requested id '{}' was not deleted. 'deleteMatchRivalParticipant' in " +
                    "RestTournamentMatchRivalFacade request denied", id);
            throw new TeamManageException(ExceptionMessages.TOURNAMENT_MATCH_RIVAL_PARTICIPANT_MODIFY_ERROR,
                    "Tournament match rival participant with requested id " + id + " was not deleted");
        }
    }

    /**
     * Returns tournament rival by id and user with privacy check
     */
    @Override
    public TournamentMatchRival getVerifiedMatchRivalById(long id) {
        TournamentMatchRival tournamentMatchRival = tournamentMatchRivalService.getMatchRival(id);
        if (isNull(tournamentMatchRival)) {
            log.debug("^ Tournament rival with requested id '{}' was not found. 'getVerifiedMatchRivalById' in RestTournamentMatchRivalService request denied", id);
            throw new TeamManageException(ExceptionMessages.TOURNAMENT_MATCH_RIVAL_NOT_FOUND_ERROR, "Tournament rival with requested id " + id + " was not found");
        }

        return tournamentMatchRival;
    }

    /**
     * Returns tournament rival by dto and user with privacy check
     */
    @Override
    public TournamentMatchRival getVerifiedMatchRivalByDto(TournamentMatchRivalDto matchRivalDto) {
        if (isNull(matchRivalDto)) {
            log.warn("~ parameter 'matchRivalDto' is NULL for getVerifiedMatchRivalByDto");
            throw new ValidationException(ExceptionMessages.TOURNAMENT_MATCH_RIVAL_VALIDATION_ERROR, "matchRivalDto",
                    "parameter 'matchRivalDto' is not set for get or modify tournament match rival");
        }
        Set<ConstraintViolation<TournamentMatchRivalDto>> settingsViolations = validator.validate(matchRivalDto);
        if (!settingsViolations.isEmpty()) {
            log.debug("^ transmitted tournament match rival dto: '{}' have constraint violations: '{}'",
                    matchRivalDto, settingsViolations);
            throw new ConstraintViolationException(settingsViolations);
        }

        boolean isRivalWasAFK = matchRivalDto.getStatus().isAFK();
        boolean isRivalHasNoneWinnerPlace = nonNull(matchRivalDto.getWonPlaceInMatch()) && matchRivalDto.getWonPlaceInMatch().isNone();
        if (isRivalWasAFK && !isRivalHasNoneWinnerPlace) {
            log.warn("~ tournament match rival.id '{}' can be set AFK status only with setting WonPlaceInMatch = NONE'. " +
                    "Request for verify rival was rejected.", matchRivalDto.getId());
            throw new TournamentManageException(ExceptionMessages.TOURNAMENT_MATCH_RIVAL_VALIDATION_ERROR,
                    String.format("Tournament match rival.id '%s' can be set AFK status only with setting " +
                            "WonPlaceInMatch = NONE. Check requested params and method.", matchRivalDto.getId()));
        }

        TournamentMatchRival tournamentMatchRival;
        //check if match rival already existed
        if (nonNull(matchRivalDto.getId())) {
            tournamentMatchRival = this.getVerifiedMatchRivalById(matchRivalDto.getId());
            if (!matchRivalDto.getTournamentMatchId().equals(tournamentMatchRival.getTournamentMatch().getId())) {
                log.warn("~ parameter 'matchRivalDto.tournamentMatchId' isn't fit existed ref from matchRival to match. " +
                        "Request to change reference from matchRival to other match is prohibited in getVerifiedMatchRivalByDto");
                throw new ValidationException(ExceptionMessages.TOURNAMENT_MATCH_VALIDATION_ERROR, "matchRivalDto.tournamentMatchId",
                        "parameter 'tournament organizer' is not match by id to tournament for getVerifiedMatchRivalByDto");
            }
            tournamentMatchRival.setStatus(matchRivalDto.getStatus());
            tournamentMatchRival.setWonPlaceInMatch(matchRivalDto.getWonPlaceInMatch());
            tournamentMatchRival.setMatchIndicator(GameIndicatorConverter.convertAndValidate(matchRivalDto.getMatchIndicator()));
            // check if matchRivalParticipants is set and verify them
            if (isNotEmpty(matchRivalDto.getRivalParticipantList())) {
                Set<TournamentMatchRivalParticipant> matchRivalParticipantList = matchRivalDto
                        .getRivalParticipantList().parallelStream()
                        .map(p -> this.getVerifiedTournamentMatchRivalParticipantByDto(p, tournamentMatchRival))
                        .collect(Collectors.toSet());
                tournamentMatchRival.setRivalParticipantList(matchRivalParticipantList);
            }
        } else {
            //compose new match rival
            tournamentMatchRival = tournamentMatchRivalMapper.fromDto(matchRivalDto);
            TournamentTeamProposal teamProposal = restTournamentProposalFacade.getVerifiedTeamProposalById(matchRivalDto.getTeamProposalId());
            tournamentMatchRival.setTeamProposal(teamProposal);
            //set match indicators
            tournamentMatchRival.setMatchIndicator(GameIndicatorConverter.convertAndValidate(matchRivalDto.getMatchIndicator()));

            // check and set match to matchRival
            if (nonNull(matchRivalDto.getTournamentMatchId())) {
                TournamentMatch tournamentMatch = restTournamentMatchFacade.getVerifiedMatchById(matchRivalDto.getTournamentMatchId());
                tournamentMatchRival.setTournamentMatch(tournamentMatch);
            }
            // define, check and set matchRivalParticipants
            if (isNotEmpty(matchRivalDto.getRivalParticipantList())) {
                TournamentMatchRival finalTournamentMatchRival = tournamentMatchRival;
                Set<TournamentMatchRivalParticipant> matchRivalParticipantList = matchRivalDto
                        .getRivalParticipantList().parallelStream()
                        .map(p -> this.getVerifiedTournamentMatchRivalParticipantByDto(p, finalTournamentMatchRival))
                        .collect(Collectors.toSet());
                tournamentMatchRival.setRivalParticipantList(matchRivalParticipantList);
            } else {
                //set default matchRivalParticipants for new matchRival
                tournamentMatchRival.setRivalParticipantsFromTournamentTeamParticipant(
                        teamProposal.getMainTournamentTeamParticipantList());
            }
        }
        return tournamentMatchRival;
    }

    /**
     * Returns tournament match rival by dto with privacy check for modifying by rivals
     */
    @Override
    public TournamentMatchRival getVerifiedMatchRivalByDtoForRival(TournamentMatchRivalDto matchRivalDto) {
        if (isNull(matchRivalDto)) {
            log.warn("~ parameter 'matchRivalDto' is NULL for getVerifiedMatchRivalByDto");
            throw new ValidationException(ExceptionMessages.TOURNAMENT_MATCH_RIVAL_VALIDATION_ERROR, "matchRivalDto",
                    "parameter 'matchRivalDto' is not set for get or modify tournament match rival");
        }
        if (isNull(matchRivalDto.getId())) {
            log.warn("~ parameter 'matchRivalDto.id' is NULL for getVerifiedMatchRivalByDto");
            throw new ValidationException(ExceptionMessages.TOURNAMENT_MATCH_RIVAL_VALIDATION_ERROR, "matchRivalDto.id",
                    "parameter 'matchRivalDto.id' is not set for modify tournament match rival");
        }
        Set<ConstraintViolation<TournamentMatchRivalDto>> settingsViolations = validator.validate(matchRivalDto);
        if (!settingsViolations.isEmpty()) {
            log.debug("^ transmitted tournament match rival dto: '{}' have constraint violations: '{}'",
                    matchRivalDto, settingsViolations);
            throw new ConstraintViolationException(settingsViolations);
        }
        //check if match rival existed
        TournamentMatchRival tournamentMatchRival = this.getVerifiedMatchRivalById(matchRivalDto.getId());
        if (!matchRivalDto.getTournamentMatchId().equals(tournamentMatchRival.getTournamentMatch().getId())) {
            log.warn("~ parameter 'matchRivalDto.tournamentMatchId' isn't fit existed ref from matchRival to match. " +
                    "Request to change reference from matchRival to other match is prohibited in getVerifiedMatchRivalByDto");
            throw new ValidationException(ExceptionMessages.TOURNAMENT_MATCH_VALIDATION_ERROR, "matchRivalDto.tournamentMatchId",
                    "parameter 'tournament organizer' is not match by id to tournament for getVerifiedMatchRivalByDto");
        }

        tournamentMatchRival.setWonPlaceInMatch(matchRivalDto.getWonPlaceInMatch());
        return tournamentMatchRival;
    }


    @Override
    public TournamentMatchRival setGameIndicatorMultipliersToMatchRival(TournamentMatchRival rival, TournamentSeries tournamentSeries) {
        if (rival.getMatchIndicator() == null) {
            return rival;
        }

        TournamentRound tournamentRound = tournamentSeries.getTournamentRound();
        Map<GameIndicatorType, Double> multiplierMap = tournamentRound.getGameIndicatorMultipliersMap();

        if (multiplierMap == null) {
            return rival;
        }

        for (GameDisciplineIndicatorDto indicator : rival.getMatchIndicator()) {
            double multiplier = multiplierMap.getOrDefault(indicator.getGameIndicatorType(), 1D);

            if (indicator.getGameIndicatorValue() != null && indicator.getGameIndicatorValue() instanceof Number) {
                double doubleValue = Double.parseDouble(indicator.getGameIndicatorValue().toString());

                indicator.setMultipliedValue(doubleValue * multiplier);
            } else {
                indicator.setMultipliedValue(null);
            }

        }

        return rival;
    }

    /**
     * Returns match rival participant corresponds to specified teamParticipant
     */
    private TournamentMatchRivalParticipant createAndValidateMatchRivalParticipant(
            TournamentTeamParticipant teamParticipant,
            Set<TournamentMatchRivalParticipant> currentTournamentMatchRivalParticipants,
            TournamentMatchRival tournamentMatchRival) {
        TournamentMatchRivalParticipant existedRivalParticipant = currentTournamentMatchRivalParticipants.parallelStream()
                .filter(p -> p.getTournamentTeamParticipant().equals(teamParticipant)).findFirst().orElse(null);
        // check if rivalParticipant already existed and was banned
        if (nonNull(existedRivalParticipant)
                && existedRivalParticipant.getStatus() == TournamentMatchRivalParticipantStatusType.BANNED) {
            log.warn("~ parameter 'rivalParticipantList' '{}' is not match by id to Tournament Match with 'matchId' '{}' for editMatchRivalParticipant",
                    tournamentMatchRival.getId(), tournamentMatchRival.getTournamentMatch().getId());
            throw new TournamentManageException(ExceptionMessages.TOURNAMENT_MATCH_RIVAL_PARTICIPANT_BANNED_ERROR,
                    "Tournament rival participant id" + existedRivalParticipant.getId() + " was banned. Including participant in match was rejected");
        } else if (isNull(existedRivalParticipant)) {
            // create new rivalParticipant
            return TournamentMatchRivalParticipant.builder()
                    .tournamentTeamParticipant(teamParticipant)
                    .tournamentMatchRival(tournamentMatchRival)
                    .status(TournamentMatchRivalParticipantStatusType.ACTIVE)
                    .build();
        } else {
            // change status of already existed rival participant to active
            existedRivalParticipant.setStatus(TournamentMatchRivalParticipantStatusType.ACTIVE);
            return existedRivalParticipant;
        }
    }

    /**
     * Returns tournament match rival by DTO with tournamentMatchRival and privacy check
     * ONLY for editing entry
     */
    private TournamentMatchRivalParticipant getVerifiedTournamentMatchRivalParticipantByDtoForEditing(
            TournamentMatchRivalParticipantDto rivalParticipantDto, TournamentMatchRival tournamentMatchRival) {
        if (isNull(rivalParticipantDto.getId())) {
            log.debug("^ Tournament rival participant with unset id, rivalParticipantDto: '{}'. " +
                            "'getVerifiedTournamentMatchRivalParticipantByDto' in RestTournamentMatchRivalService request denied",
                    rivalParticipantDto);
            throw new TournamentManageException(ExceptionMessages.TOURNAMENT_MATCH_RIVAL_PARTICIPANT_NOT_FOUND_ERROR,
                    "Tournament rival participant with unset id");
        }
        return this.getVerifiedTournamentMatchRivalParticipantByDto(rivalParticipantDto, tournamentMatchRival);
    }

    /**
     * Returns tournament match rival by DTO with tournamentMatchRival and privacy check
     */
    private TournamentMatchRivalParticipant getVerifiedTournamentMatchRivalParticipantByDto(
            TournamentMatchRivalParticipantDto rivalParticipantDto, TournamentMatchRival tournamentMatchRival) {
        //verify properties of specified TournamentMatchRivalParticipantDto
        if (isNull(rivalParticipantDto)) {
            log.error("^ requested getVerifiedTournamentMatchRivalParticipantByDto for NULL rivalParticipantDto. Check evoking clients");
            return null;
        }
        Set<ConstraintViolation<TournamentMatchRivalParticipantDto>> settingsViolations = validator.validate(rivalParticipantDto);
        if (!settingsViolations.isEmpty()) {
            log.debug("^ transmitted rival participant dto: '{}' have constraint violations: '{}'",
                    rivalParticipantDto, settingsViolations);
            throw new ConstraintViolationException(settingsViolations);
        }
        if (isNull(rivalParticipantDto.getTournamentMatchRivalId())) {
            log.warn("~ parameter 'tournamentMatchRivalId' is no set in rivalParticipantDto");
            throw new ValidationException(ExceptionMessages.TOURNAMENT_MATCH_RIVAL_PARTICIPANT_VALIDATION_ERROR, "tournamentMatchRivalId",
                    "parameter 'tournamentMatchRivalId' is badly set in rivalParticipantDto or not match specified tournamentMatchRival for getVerifiedTournamentMatchRivalParticipantByDto");
        }
        if (!rivalParticipantDto.getTournamentMatchRivalId().equals(tournamentMatchRival.getId())) {
            log.warn("~ parameter 'tournamentMatchRivalId' is not equals tournamentMatchRival that was saved previously in DB. Request denied in getVerifiedTournamentMatchRivalParticipantByDto");
            throw new ValidationException(ExceptionMessages.TOURNAMENT_MATCH_RIVAL_PARTICIPANT_VALIDATION_ERROR, "tournamentMatchRivalId",
                    "parameter 'tournamentMatchRivalId' is not equals tournamentMatchRival that was saved previously in DB. Request denied in getVerifiedTournamentMatchRivalParticipantByDto");
        }

        // Check existence of entity (from TournamentMatchRivalParticipantDto.getId) and matching for tournamentMatchRival
        TournamentMatchRivalParticipant tournamentMatchRivalParticipant = this.getVerifiedMatchRivalParticipantById(rivalParticipantDto.getId());
        if (!tournamentMatchRivalParticipant.getTournamentMatchRival().getId().equals(tournamentMatchRival.getId())) {
            log.warn("~ parameter 'tournamentMatchRivalParticipant.id' {} is not match specified tournamentMatchRival.id '{}' " +
                    "for getVerifiedTournamentMatchRivalParticipantByDto", tournamentMatchRivalParticipant.getId(), tournamentMatchRival.getId());
            throw new ValidationException(ExceptionMessages.TOURNAMENT_MATCH_RIVAL_PARTICIPANT_VALIDATION_ERROR, "tournamentMatchRivalParticipant.ID",
                    "parameter 'tournamentMatchRivalParticipant.id' is not match specified tournamentMatchRival for getVerifiedTournamentMatchRivalParticipantByDto");
        }
        return tournamentMatchRivalParticipant;
    }

    /**
     * Returns tournament rival by id and user with privacy check
     */
    private TournamentMatchRivalParticipant getVerifiedMatchRivalParticipantById(long id) {
        TournamentMatchRivalParticipant matchRivalParticipant = tournamentMatchRivalService.getMatchRivalParticipant(id);
        if (isNull(matchRivalParticipant)) {
            log.debug("^ Tournament rival participant with requested id '{}' was not found. 'getVerifiedMatchRivalParticipantById' in RestTournamentMatchRivalService request denied", id);
            throw new TeamManageException(ExceptionMessages.TOURNAMENT_MATCH_RIVAL_PARTICIPANT_NOT_FOUND_ERROR, "Tournament rival participant with requested id " + id + " was not found");
        }
        return matchRivalParticipant;
    }
}

package com.freetonleague.core.service.tournament.implementations;

import com.freetonleague.core.domain.dto.tournament.TournamentRoundDto;
import com.freetonleague.core.domain.enums.tournament.TournamentStatusType;
import com.freetonleague.core.domain.model.User;
import com.freetonleague.core.domain.model.tournament.Tournament;
import com.freetonleague.core.domain.model.tournament.TournamentRound;
import com.freetonleague.core.exception.TeamManageException;
import com.freetonleague.core.exception.TournamentManageException;
import com.freetonleague.core.exception.ValidationException;
import com.freetonleague.core.exception.config.ExceptionMessages;
import com.freetonleague.core.mapper.tournament.TournamentRoundMapper;
import com.freetonleague.core.security.permissions.CanManageSystem;
import com.freetonleague.core.security.permissions.CanManageTournament;
import com.freetonleague.core.service.tournament.RestTournamentFacade;
import com.freetonleague.core.service.tournament.RestTournamentRoundFacade;
import com.freetonleague.core.service.tournament.TournamentRoundService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import javax.validation.Validator;
import java.util.Set;

import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;

@Slf4j
@RequiredArgsConstructor
@Service
public class RestTournamentRoundFacadeImpl implements RestTournamentRoundFacade {

    private final TournamentRoundService tournamentRoundService;
    private final TournamentRoundMapper tournamentRoundMapper;
    private final Validator validator;
    private final RestTournamentFacade restTournamentFacade;

    /**
     * Returns founded tournament round by id
     */
    @Override
    public TournamentRoundDto getRound(long id, User user) {
        TournamentRound tournamentRound = this.getVerifiedRoundById(id);
        return tournamentRoundMapper.toDto(tournamentRound);
    }

    /**
     * Returns list of all tournament round filtered by requested params
     */
    @Override
    public Page<TournamentRoundDto> getRoundList(Pageable pageable, long tournamentId, User user) {
        Tournament tournament = restTournamentFacade.getVerifiedTournamentById(tournamentId);
        return tournamentRoundService.getRoundList(pageable, tournament).map(tournamentRoundMapper::toDto);
    }

    /**
     * Returns current active round for tournament
     */
    @Override
    public TournamentRoundDto getActiveRoundForTournament(long tournamentId, User user) {
        Tournament tournament = restTournamentFacade.getVerifiedTournamentById(tournamentId);
        return tournamentRoundMapper.toDto(tournamentRoundService.getActiveRoundForTournament(tournament));
    }

    /**
     * Add new tournament round.
     */
    @CanManageTournament
    @Override
    public TournamentRoundDto addRound(TournamentRoundDto tournamentRoundDto, User user) {
        tournamentRoundDto.setId(null);
        tournamentRoundDto.setStatus(TournamentStatusType.CREATED);
        TournamentRound tournamentRound = this.getVerifiedRoundByDto(tournamentRoundDto);

        // allow to add only next round (with number = last + 1).
        int lastNumber = tournamentRoundService.getLastActiveRoundNumberForTournament(tournamentRound.getTournament());
        tournamentRound.setRoundNumber(lastNumber + 1);

        tournamentRound = tournamentRoundService.addRound(tournamentRound);
        if (isNull(tournamentRound)) {
            log.error("!> error while creating tournament round from dto '{}' for user '{}'.", tournamentRoundDto, user);
            throw new TournamentManageException(ExceptionMessages.TOURNAMENT_ROUND_CREATION_ERROR,
                    "Tournament round was not saved on Portal. Check requested params.");
        }
        return tournamentRoundMapper.toDto(tournamentRound);
    }

    /**
     * Generate all rounds for tournament and prototypes of series/matches.
     */
    @CanManageTournament
    @Override
    public void generateRoundsForTournament(long tournamentId, User user) {
        Tournament tournament = restTournamentFacade.getVerifiedTournamentById(tournamentId);
        if (!tournament.getSystemType().isGenerationRoundEnabled()) {
            log.warn("~ generation tournament round list for tournament id '{}' with systemType '{}' is prohibited.", tournamentId, tournament.getSystemType());
            throw new TournamentManageException(ExceptionMessages.TOURNAMENT_ROUND_GENERATION_ERROR,
                    String.format("Generation tournament round list for tournament id '%s' with systemType '%s' " +
                            "is prohibited. Check requested params.", tournamentId, tournament.getSystemType()));
        }
        boolean result = tournamentRoundService.initiateTournamentBracketsWithRounds(tournament);
        if (!result) {
            log.error("!> error while generated tournament round list for tournament id '{}' with user '{}'.", tournamentId, user);
            throw new TournamentManageException(ExceptionMessages.TOURNAMENT_ROUND_GENERATION_ERROR,
                    "Tournament round was not generated and saved on Portal. Check requested params.");
        }
    }

    /**
     * Edit tournament round.
     */
    @CanManageTournament
    @Override
    public TournamentRoundDto editRound(long id, TournamentRoundDto tournamentRoundDto, User user) {
        if (isNull(tournamentRoundDto) || tournamentRoundDto.getId() != id) {
            log.warn("~ parameter 'tournamentRoundDto.id' is not match specified id in parameters for editRound");
            throw new ValidationException(ExceptionMessages.TOURNAMENT_ROUND_VALIDATION_ERROR, "tournamentRoundDto.id",
                    "parameter 'tournamentRoundDto.id' is not match specified id in parameters for editRound");
        }
        TournamentRound tournamentRound = this.getVerifiedRoundByDto(tournamentRoundDto);

        if (tournamentRound.getStatus().isDeleted()) {
            log.warn("~ tournament round deleting was declined in editRound. This operation should be done with specific method.");
            throw new TournamentManageException(ExceptionMessages.TOURNAMENT_ROUND_STATUS_DELETE_ERROR,
                    "Modifying tournament round was rejected. Check requested params and method.");
        }
        tournamentRound = tournamentRoundService.editRound(tournamentRound);
        if (isNull(tournamentRound)) {
            log.error("!> error while editing tournament round from dto '{}' for user '{}'.", tournamentRoundDto, user);
            throw new TournamentManageException(ExceptionMessages.TOURNAMENT_ROUND_MODIFICATION_ERROR,
                    "Tournament round was not updated on Portal. Check requested params.");
        }
        return tournamentRoundMapper.toDto(tournamentRound);
    }

    /**
     * Mark 'deleted' tournament round.
     */
    @CanManageTournament
    @Override
    public TournamentRoundDto archiveRound(long id, User user) {
        TournamentRound tournamentRound = this.getVerifiedRoundById(id);
        tournamentRound = tournamentRoundService.archiveRound(tournamentRound);

        if (isNull(tournamentRound)) {
            log.error("!> error while deleting tournament round with id '{}' for user '{}'.", id, user);
            throw new TournamentManageException(ExceptionMessages.TOURNAMENT_ROUND_MODIFICATION_ERROR,
                    "Tournament round was not deleted on Portal. Check requested params.");
        }
        return tournamentRoundMapper.toDto(tournamentRound);
    }

    /**
     * Remove round (force delete with cascade entries)
     */
    @CanManageSystem
    @Override
    public void removeRound(long id, User user) {
        TournamentRound tournamentRound = tournamentRoundService.getRound(id);
        if (isNull(tournamentRound)) {
            log.debug("^ Tournament round with requested id '{}' was not found. 'removeRound' in RestTournamentRoundService request denied", id);
            throw new TeamManageException(ExceptionMessages.TOURNAMENT_ROUND_NOT_FOUND_ERROR, "Tournament round  with requested id " + id + " was not found");
        }
        boolean result = tournamentRoundService.removeRound(tournamentRound);
        if (!result) {
            log.error("!> error while forced removing tournament round with id '{}' for user '{}'.", id, user);
            throw new TournamentManageException(ExceptionMessages.TOURNAMENT_ROUND_MODIFICATION_ERROR,
                    "Tournament round was not forced removed on Portal. Check requested params.");
        }
    }

    /**
     * Returns tournament round by id and user with privacy check
     */
    @Override
    public TournamentRound getVerifiedRoundById(long id) {
        TournamentRound tournamentRound = tournamentRoundService.getRound(id);
        if (isNull(tournamentRound)) {
            log.debug("^ Tournament round with requested id '{}' was not found. 'getVerifiedRoundById' in RestTournamentRoundService request denied", id);
            throw new TeamManageException(ExceptionMessages.TOURNAMENT_ROUND_NOT_FOUND_ERROR, "Tournament round  with requested id " + id + " was not found");
        }
        if (tournamentRound.getStatus().isDeleted()) {
            log.debug("^ Tournament round with requested id '{}' was '{}'. 'getVerifiedRoundById' in RestTournamentRoundService request denied", id, tournamentRound.getStatus());
            throw new TeamManageException(ExceptionMessages.TOURNAMENT_ROUND_DISABLE_ERROR, "Active tournament round with requested id " + id + " was not found");
        }
        return tournamentRound;
    }

    /**
     * Getting tournament settings by DTO with privacy check
     */
    @Override
    public TournamentRound getVerifiedRoundByDto(TournamentRoundDto tournamentRoundDto) {
        if (isNull(tournamentRoundDto)) {
            log.warn("~ parameter 'tournamentRoundDto' is NULL for getVerifiedRoundByDto");
            throw new ValidationException(ExceptionMessages.TOURNAMENT_ROUND_VALIDATION_ERROR, "tournamentRoundDto",
                    "parameter 'tournamentRoundDto' is not set for get or modify tournament series");
        }

        Set<ConstraintViolation<TournamentRoundDto>> violations = validator.validate(tournamentRoundDto);
        if (!violations.isEmpty()) {
            log.debug("^ transmitted tournament round dto: '{}' have constraint violations: '{}'",
                    tournamentRoundDto, violations);
            throw new ConstraintViolationException(violations);
        }

        Tournament tournament = restTournamentFacade.getVerifiedTournamentById(tournamentRoundDto.getTournamentId());

        // Check existence of tournament round and it's status
        // Set round number to
        if (nonNull(tournamentRoundDto.getId())) {
            TournamentRound existedTournamentRound = this.getVerifiedRoundById(tournamentRoundDto.getId());
            tournamentRoundDto.setRoundNumber(existedTournamentRound.getRoundNumber());
        }
        TournamentRound tournamentRound = tournamentRoundMapper.fromDto(tournamentRoundDto);
        tournamentRound.setTournament(tournament);
        return tournamentRound;
    }
}

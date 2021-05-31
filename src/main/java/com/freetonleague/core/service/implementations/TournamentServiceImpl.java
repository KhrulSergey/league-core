package com.freetonleague.core.service.implementations;


import com.freetonleague.core.cloudclient.LeagueStorageClientService;
import com.freetonleague.core.domain.enums.TournamentStatusType;
import com.freetonleague.core.domain.enums.TournamentWinnerPlaceType;
import com.freetonleague.core.domain.model.*;
import com.freetonleague.core.repository.TournamentRepository;
import com.freetonleague.core.service.TournamentEventService;
import com.freetonleague.core.service.TournamentService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import javax.validation.ConstraintViolation;
import javax.validation.Validator;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Set;

import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;
import static org.apache.commons.lang3.BooleanUtils.isFalse;
import static org.apache.commons.lang3.ObjectUtils.isEmpty;
import static org.apache.commons.lang3.ObjectUtils.isNotEmpty;
import static org.apache.commons.lang3.StringUtils.isBlank;

@Slf4j
@RequiredArgsConstructor
@Service
public class TournamentServiceImpl implements TournamentService {

    private final LeagueStorageClientService leagueStorageClientService;
    private final TournamentRepository tournamentRepository;
    private final Validator validator;

    @Lazy
    @Autowired
    private TournamentEventService tournamentEventService;


    /**
     * Returns founded tournament by id
     */
    @Override
    public Tournament getTournament(long id) {
        log.debug("^ trying to get tournament by id: '{}'", id);
        return tournamentRepository.findById(id).orElse(null);
    }

    /**
     * Returns list of all teams filtered by requested params
     */
    @Override
    public Page<Tournament> getTournamentList(Pageable pageable, User creatorUser, List<TournamentStatusType> statusList) {
        if (isNull(pageable)) {
            log.error("!> requesting getTournamentList for NULL pageable. Check evoking clients");
            return null;
        }
        log.debug("^ trying to get tournament list with pageable params: '{}' and status list '{}'", pageable, statusList);
        boolean filterByStatusEnabled = isNotEmpty(statusList);
        boolean filterByCreatorEnabled = nonNull(creatorUser);

        if (filterByStatusEnabled && filterByCreatorEnabled) {
            return tournamentRepository.findAllByStatusInAndCreatedBy(pageable, statusList, creatorUser);
        } else if (filterByStatusEnabled) {
            return tournamentRepository.findAllByStatusIn(pageable, statusList);
        } else if (filterByCreatorEnabled) {
            return tournamentRepository.findAllByCreatedBy(pageable, creatorUser);
        }
        return tournamentRepository.findAll(pageable);
    }

    /**
     * Returns list of all teams on portal
     */
    @Override
    public List<Tournament> getAllActiveTournament() {
        return tournamentRepository.findAllActive(TournamentStatusType.activeStatusList);
    }


    /**
     * Add new tournament to DB.
     */
    @Override
    public Tournament addTournament(Tournament tournament) {
        if (!this.verifyTournament(tournament)) {
            return null;
        }
        log.debug("^ trying to add new tournament '{}'", tournament);
        if (!isBlank(tournament.getLogoRawFile())) {
            tournament.setLogoHashKey(leagueStorageClientService.saveTournamentLogo(tournament));
        }
        tournament = tournamentRepository.save(tournament);
        tournamentEventService.processTournamentStatusChange(tournament, tournament.getStatus());
        return tournament;
    }

    /**
     * Edit tournament in DB.
     */
    @Override
    public Tournament editTournament(Tournament tournament) {
        if (!this.verifyTournament(tournament)) {
            return null;
        }
        if (!this.isExistsTournamentById(tournament.getId())) {
            log.error("!> requesting modify tournament '{}' for non-existed tournament. Check evoking clients", tournament.getId());
            return null;
        }
        log.debug("^ trying to modify tournament '{}'", tournament);

        if (tournament.getStatus().isFinished()) {
            tournament.setFinishedDate(LocalDateTime.now());
            // if tournament was automatically finished by EventService (not manually-forced)
            if (isFalse(tournament.getIsForcedFinished())) {
                List<TournamentWinner> tournamentWinnerList = this.getCalculatedTeamProposalWinnerList(tournament);
                if (isEmpty(tournamentWinnerList)) {
                    log.error("!> requesting modify tournament id '{}' was canceled. Tournament winner was not defined or found. Check stack trace.",
                            tournament.getId());
                    tournament.setStatus(TournamentStatusType.PAUSE);
                }
                tournament.setTournamentWinnerList(tournamentWinnerList);
            }
        }
        if (tournament.isStatusChanged()) {
            this.handleTournamentStatusChanged(tournament);
        }
        if (!isBlank(tournament.getLogoRawFile())) {
            tournament.setLogoHashKey(leagueStorageClientService.saveTournamentLogo(tournament));
        }
        return tournamentRepository.save(tournament);
    }

    /**
     * Mark 'deleted' tournament in DB.
     */
    @Override
    public Tournament deleteTournament(Tournament tournament) {
        if (!this.verifyTournament(tournament)) {
            return null;
        }
        if (!this.isExistsTournamentById(tournament.getId())) {
            log.error("!> requesting delete tournament for non-existed tournament. Check evoking clients");
            return null;
        }
        log.debug("^ trying to set 'deleted' mark to tournament '{}'", tournament);
        tournament.setStatus(TournamentStatusType.DELETED);
        tournament = tournamentRepository.save(tournament);
        this.handleTournamentStatusChanged(tournament);
        return tournament;
    }

    /**
     * Returns sign of tournament existence for specified id.
     */
    @Override
    public boolean isExistsTournamentById(long id) {
        return tournamentRepository.existsById(id);
    }

    /**
     * Returns sign of user is tournament organizer, or false if not
     */
    public boolean isUserTournamentOrganizer(Tournament tournament, User user) {
        if (isNull(tournament) || isNull(user)) {
            log.error("!> requesting isUserTournamentOrganizer for NULL tournament '{}' or NULL user '{}'. Check evoking clients",
                    tournament, user);
            return false;
        }
        return tournament.getTournamentOrganizerList().parallelStream().anyMatch(org -> org.getUser().equals(user));
    }

    private List<TournamentWinner> getCalculatedTeamProposalWinnerList(Tournament tournament) {

        List<TournamentWinner> winnerTeamProposal = new ArrayList<>();
        TournamentRound tournamentRound = tournament.getTournamentRoundList().stream()
                .max(Comparator.comparingInt(TournamentRound::getRoundNumber))
                .orElse(null);

        if (isNull(tournamentRound) || isEmpty(tournamentRound.getSeriesList()) || tournamentRound.getSeriesList().size() > 1) {
            log.error("!> requesting getCalculatedTeamProposalWinnerList for tournament '{}' " +
                            "for non-existed rival with advantage score. Check evoking clients",
                    tournament.getId());
            return null;
        }
        TournamentSeriesRival winnerOfLastSeries = tournamentRound.getSeriesList().get(0).getSeriesWinner();
        winnerTeamProposal.add(TournamentWinner.builder()
                .winnerPlaceType(TournamentWinnerPlaceType.FIRST)
                .tournament(tournament)
                .teamProposal(winnerOfLastSeries.getTeamProposal())
                .build());
        return winnerTeamProposal;
    }

    /**
     * Validate tournament parameters and settings to modify
     */
    private boolean verifyTournament(Tournament tournament) {
        if (isNull(tournament)) {
            log.error("!> requesting modify tournament with verifyTournament for NULL tournament. Check evoking clients");
            return false;
        }
        Set<ConstraintViolation<Tournament>> violations = validator.validate(tournament);
        if (!violations.isEmpty()) {
            log.error("!> requesting modify tournament '{}' with verifyTournament for tournament with ConstraintViolations '{}'. Check evoking clients",
                    tournament.getId(), violations);
            return false;
        }
        TournamentSettings tournamentSettings = tournament.getTournamentSettings();
        if (nonNull(tournamentSettings)) {
            Set<ConstraintViolation<TournamentSettings>> settingsViolations = validator.validate(tournamentSettings);
            if (!settingsViolations.isEmpty()) {
                log.error("!> requesting modify tournament '{}' with verifyTournament for tournament settings with ConstraintViolations '{}'. Check evoking clients",
                        tournament.getId(), settingsViolations);
                return false;
            }
        }
        return true;
    }

    /**
     * Prototype for handle tournament status
     */
    private void handleTournamentStatusChanged(Tournament tournament) {
        log.warn("~ status for tournament id '{}' was changed from '{}' to '{}' ",
                tournament.getId(), tournament.getPrevStatus(), tournament.getStatus());
        tournament.setPrevStatus(tournament.getStatus());
    }
}

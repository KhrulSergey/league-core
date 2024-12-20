package com.freetonleague.core.service.tournament.implementations;

import com.freetonleague.core.domain.enums.tournament.TournamentMatchRivalParticipantStatusType;
import com.freetonleague.core.domain.model.User;
import com.freetonleague.core.domain.model.tournament.TournamentMatch;
import com.freetonleague.core.domain.model.tournament.TournamentMatchRival;
import com.freetonleague.core.domain.model.tournament.TournamentMatchRivalParticipant;
import com.freetonleague.core.repository.tournament.TournamentMatchRivalParticipantRepository;
import com.freetonleague.core.repository.tournament.TournamentMatchRivalRepository;
import com.freetonleague.core.service.tournament.TournamentMatchRivalService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.validation.ConstraintViolation;
import javax.validation.Validator;
import java.util.List;
import java.util.Set;

import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;

@Slf4j
@RequiredArgsConstructor
@Service
@Transactional
public class TournamentMatchRivalServiceImpl implements TournamentMatchRivalService {

    private final TournamentMatchRivalRepository tournamentMatchRivalRepository;
    private final TournamentMatchRivalParticipantRepository tournamentMatchRivalParticipantRepository;
    private final Validator validator;

    /**
     * Returns founded tournament match by id
     */
    @Override
    public TournamentMatchRival getMatchRival(long id) {
        log.debug("^ trying to get match rival by id: '{}'", id);
        return tournamentMatchRivalRepository.findById(id).orElse(null);
    }

    /**
     * Returns founded tournament match by id
     *
     * @param match of tournament match to search
     * @return tournament match entity or NULL of not found
     */
    @Override
    public List<TournamentMatchRival> getMatchRivalByMatch(TournamentMatch match) {
        if (isNull(match)) {
            log.error("!> requesting get tournament rival with getMatchRivalByMatch for NULL match. Check evoking clients");
            return null;
        }
        log.debug("^ trying to get match rival by match.id: '{}'", match.getId());
        return tournamentMatchRivalRepository.findAllByTournamentMatch(match);
    }

    /**
     * Add new tournament series to DB.
     */
    @Override
    public TournamentMatchRival addMatchRival(TournamentMatchRival tournamentMatchRival) {
        if (!this.verifyTournamentMatchRival(tournamentMatchRival)) {
            return null;
        }
        log.debug("^ trying to add new tournament match rival '{}'", tournamentMatchRival);
        return tournamentMatchRivalRepository.save(tournamentMatchRival);
    }

    /**
     * Edit tournament series in DB.
     */
    @Override
    public TournamentMatchRival editMatchRival(TournamentMatchRival tournamentMatchRival) {
        if (!this.verifyTournamentMatchRival(tournamentMatchRival)) {
            return null;
        }
        if (!this.isExistsTournamentMatchRivalById(tournamentMatchRival.getId())) {
            log.error("!> requesting modify tournament match rival '{}' for non-existed tournament match. Check evoking clients",
                    tournamentMatchRival.getId());
            return null;
        }
        log.debug("^ trying to modify tournament match rival '{}'", tournamentMatchRival);
        if (tournamentMatchRival.isStatusChanged()) {
            this.handleTournamentMatchRivalStatusChanged(tournamentMatchRival);
        }
        return tournamentMatchRivalRepository.save(tournamentMatchRival);
    }

    /**
     * Mark 'deleted' tournament series in DB.
     */
    @Override
    public TournamentMatchRival archiveMatchRival(TournamentMatchRival tournamentMatchRival) {
        if (!this.verifyTournamentMatchRival(tournamentMatchRival)) {
            return null;
        }
        if (!this.isExistsTournamentMatchRivalById(tournamentMatchRival.getId())) {
            log.error("!> requesting delete tournament match rival for non-existed tournament tournamentMatchRival. Check evoking clients");
            return null;
        }
        log.debug("^ trying to set 'deleted' mark to tournament match rival '{}'", tournamentMatchRival);
        tournamentMatchRival.setStatus(TournamentMatchRivalParticipantStatusType.DISABLED);
        tournamentMatchRival = tournamentMatchRivalRepository.save(tournamentMatchRival);
        this.handleTournamentMatchRivalStatusChanged(tournamentMatchRival);
        return tournamentMatchRival;
    }

    /**
     * Delete tournament match rival by id
     */
    @Override
    public boolean deleteMatchRival(TournamentMatchRival tournamentMatchRival) {
        if (isNull(tournamentMatchRival) || isNull(tournamentMatchRival.getId())) {
            log.error("!> requesting delete match rival for NULL data (id). Check evoking clients");
            return false;
        }
        if (!this.isExistsTournamentMatchRivalById(tournamentMatchRival.getId())) {
            log.error("!> requesting delete match rival for non-existed entry. Check evoking clients");
            return false;
        }
        log.debug("^ trying to delete tournament match rival '{}'", tournamentMatchRival);
        tournamentMatchRivalRepository.delete(tournamentMatchRival);
        return true;
    }

    /**
     * Delete tournament match rival participant by id
     */
    @Override
    public boolean deleteMatchRivalParticipant(TournamentMatchRivalParticipant tournamentMatchRivalParticipant) {
        if (isNull(tournamentMatchRivalParticipant) || isNull(tournamentMatchRivalParticipant.getId())) {
            log.error("!> requesting delete match rival participant for NULL data (id). Check evoking clients");
            return false;
        }
        if (!this.isExistsTournamentMatchRivalParticipantById(tournamentMatchRivalParticipant.getId())) {
            log.error("!> requesting delete match rival participant for non-existed entry. Check evoking clients");
            return false;
        }
        log.debug("^ trying to delete tournament match rival participant '{}'", tournamentMatchRivalParticipant);
        tournamentMatchRivalParticipantRepository.delete(tournamentMatchRivalParticipant);
        return true;
    }

    /**
     * Returns sign of tournament series existence for specified id.
     */
    @Override
    public boolean isExistsTournamentMatchRivalById(long id) {
        return tournamentMatchRivalRepository.existsById(id);
    }

    /**
     * Returns sign of tournament match rival participant existence for specified id.
     */
    @Override
    public boolean isExistsTournamentMatchRivalParticipantById(long id) {
        return tournamentMatchRivalParticipantRepository.existsById(id);
    }

    /**
     * Returns sign if user is tournament match rival participant
     */
    @Override
    public boolean isUserMatchRivalParticipant(TournamentMatch tournamentMatch, User user) {
        return tournamentMatchRivalRepository.isUserParticipateInMatch(tournamentMatch, user.getLeagueId());
    }

    /**
     * Verify tournament match rival info with validation and business check
     */
    @Override
    public boolean verifyTournamentMatchRival(TournamentMatchRival tournamentMatchRival) {

        if (isNull(tournamentMatchRival)) {
            log.error("!> requesting modify tournament rival with verifyTournamentRival for NULL tournamentMatchRival. Check evoking clients");
            return false;
        }
        Set<ConstraintViolation<TournamentMatchRival>> violations = validator.validate(tournamentMatchRival);
        if (!violations.isEmpty()) {
            log.error("!> requesting modify tournament rival id:'{}' with verifyTournamentMatch for tournament match with ConstraintViolations. Check evoking clients",
                    tournamentMatchRival.getId());
            return false;
        }
        Set<TournamentMatchRivalParticipant> tournamentMatchRivalParticipants = tournamentMatchRival.getRivalParticipantList();
        if (nonNull(tournamentMatchRivalParticipants)) {
            for (TournamentMatchRivalParticipant matchRivalParticipant : tournamentMatchRivalParticipants) {
                if (!this.verifyTournamentRivalParticipant(matchRivalParticipant)) {
                    log.error("!> requesting modify tournament rival '{}' with verifyTournamentRival for tournament rival participant with ConstraintViolations. Check evoking clients",
                            tournamentMatchRival.getId());
                    return false;
                }
            }
        }
        return true;
    }

    /**
     * Returns founded tournament rival participant by id
     */
    @Override
    public TournamentMatchRivalParticipant getMatchRivalParticipant(long id) {
        log.debug("^ trying to get match rival participant by id: '{}'", id);
        return tournamentMatchRivalParticipantRepository.findById(id).orElse(null);
    }

    /**
     * Verify tournament match rival participant info with validation and business check
     */
    private boolean verifyTournamentRivalParticipant(TournamentMatchRivalParticipant tournamentMatchRivalParticipant) {
        if (isNull(tournamentMatchRivalParticipant)) {
            log.error("!> requesting modify tournament rival participant with verifyTournamentRivalParticipant for NULL tournamentMatchRivalParticipant. Check evoking clients");
            return false;
        }
        Set<ConstraintViolation<TournamentMatchRivalParticipant>> violations = validator.validate(tournamentMatchRivalParticipant);
        if (!violations.isEmpty()) {
            log.error("!> requesting modify tournament rival participant id:'{}' with verifyTournamentRivalParticipant for tournament match rival with ConstraintViolations. Check evoking clients",
                    tournamentMatchRivalParticipant.getId());
            return false;
        }
        return true;
    }

    /**
     * Prototype for handle tournament match status
     */
    private void handleTournamentMatchRivalStatusChanged(TournamentMatchRival tournamentMatchRival) {
        log.warn("~ status for tournament match rival id '{}' was changed from '{}' to '{}' ",
                tournamentMatchRival.getId(), tournamentMatchRival.getPrevStatus(), tournamentMatchRival.getStatus());
        tournamentMatchRival.setPrevStatus(tournamentMatchRival.getStatus());
    }
}

package com.freetonleague.core.service.implementations;

import com.freetonleague.core.domain.enums.ParticipantStatusType;
import com.freetonleague.core.domain.model.User;
import com.freetonleague.core.domain.model.Participant;
import com.freetonleague.core.domain.model.Team;
import com.freetonleague.core.repository.ParticipantRepository;
import com.freetonleague.core.service.ParticipantService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import javax.validation.ConstraintViolation;
import javax.validation.Validator;
import java.util.Optional;
import java.util.Set;

@Transactional(propagation = Propagation.REQUIRES_NEW)
@Service
@RequiredArgsConstructor
@Slf4j
/**
 * Implementation of the service for accessing User data from the repository.
 */
public class ParticipantServiceImpl implements ParticipantService {
    private final ParticipantRepository participantRepository;
    private final Validator validator;

    private final static String ADD_FAIL = "Something went wrong while add participant to team";
    private final static String DELETE_FAIL = "Something went wrong while delete participant to team";

    /**
     * Add participant to team by his id and team id.
     *
     * @param user participant what will be added
     * @param team team where participant will be added
     * @return added participant
     */
    @Override
    public Participant addToTeam(User user, Team team) {
        Set<ConstraintViolation<User>> userViolations = validator.validate(user);
        Set<ConstraintViolation<Team>> teamViolations = validator.validate(team);
        if (userViolations.isEmpty() && teamViolations.isEmpty()) {
            Participant participant = Participant.builder()
                    .team(team)
                    .user(user)
                    .status(ParticipantStatusType.NEWBIE)
                    .build();
            Participant addedParticipant = participantRepository.saveAndFlush(participant);
            log.debug("User: {} is added to team: {}", user, team);
            return addedParticipant;
        } else {
            log.warn("Something went wrong, check user: {} or team: {}", user, team);
            throw new RuntimeException(ADD_FAIL);
        }
    }

    /**
     * Delete participant from team.
     *
     * @param participant participant what will be deleted
     * @return deleted participant
     */
    @Override
    public Participant deleteFromTeam(Participant participant) {
        Set<ConstraintViolation<Participant>> participantViolations = validator.validate(participant);
        if (participantViolations.isEmpty()) {
            Optional<Participant> deletedParticipant = participantRepository.delete(participant.getId());
            if (deletedParticipant.isPresent()) {
                return deletedParticipant.get();
            } else {
                throw new RuntimeException(DELETE_FAIL);
            }
        } else {
            throw new RuntimeException(DELETE_FAIL);
        }
    }
}

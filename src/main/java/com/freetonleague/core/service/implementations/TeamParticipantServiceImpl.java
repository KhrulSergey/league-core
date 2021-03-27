package com.freetonleague.core.service.implementations;

import com.freetonleague.core.domain.enums.TeamParticipantStatusType;
import com.freetonleague.core.domain.model.Team;
import com.freetonleague.core.domain.model.TeamParticipant;
import com.freetonleague.core.domain.model.User;
import com.freetonleague.core.repository.TeamParticipantRepository;
import com.freetonleague.core.service.TeamParticipantService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import javax.validation.ConstraintViolation;
import javax.validation.Validator;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static java.util.Objects.isNull;

@Transactional(propagation = Propagation.REQUIRES_NEW)
@Service
@RequiredArgsConstructor
@Slf4j
/**
 * Implementation of the service for accessing User data from the repository.
 */
public class TeamParticipantServiceImpl implements TeamParticipantService {
    private final TeamParticipantRepository teamParticipantRepository;
    private final Validator validator;

    private final static String ADD_FAIL = "Something went wrong while add participant to team";
    private final static String DELETE_FAIL = "Something went wrong while delete participant to team";


    public TeamParticipant save(TeamParticipant teamParticipant) {
        return teamParticipantRepository.saveAndFlush(teamParticipant);
    }

    /**
     * Add participant to team by his id and team id.
     *
     * @param user participant what will be added
     * @param team team where participant will be added
     * @return added participant
     */
    @Override
    public TeamParticipant addToTeam(User user, Team team) {
        Set<ConstraintViolation<User>> userViolations = validator.validate(user);
        Set<ConstraintViolation<Team>> teamViolations = validator.validate(team);
        if (userViolations.isEmpty() && teamViolations.isEmpty()) {
            TeamParticipant teamParticipant = TeamParticipant.builder()
                    .team(team)
                    .user(user)
                    .status(TeamParticipantStatusType.ACTIVE)
                    .build();
            TeamParticipant addedTeamParticipant = teamParticipantRepository.saveAndFlush(teamParticipant);
            log.debug("User: {} is added to team: {}", user, team);
            return addedTeamParticipant;
        } else {
            log.warn("Something went wrong, check user: {} or team: {}", user, team);
            throw new RuntimeException(ADD_FAIL);
        }
    }

    /**
     * Delete participant from team.
     *
     * @param teamParticipant participant what will be deleted
     * @return deleted participant
     */
    @Override
    public TeamParticipant deleteFromTeam(TeamParticipant teamParticipant) {
        Set<ConstraintViolation<TeamParticipant>> participantViolations = validator.validate(teamParticipant);
        if (participantViolations.isEmpty()) {
            Optional<TeamParticipant> deletedParticipant = teamParticipantRepository.expel(teamParticipant);
            if (deletedParticipant.isPresent()) {
                return deletedParticipant.get();
            } else {
                throw new RuntimeException(DELETE_FAIL);
            }
        } else {
            throw new RuntimeException(DELETE_FAIL);
        }
    }


    /**
     * Get all participation info for requested user
     */
    @Override
    public List<TeamParticipant> getAllParticipation(User user) {
        if (isNull(user)) {
            //TODO change Exc
            throw new UsernameNotFoundException("to token in request");
        }
        return teamParticipantRepository.findAllByUser(user);
    }

    /**
     * Returns founded participant by id
     *
     * @param id of team to search
     * @return team entity
     */
    @Override
    public TeamParticipant getById(long id) {
        log.debug("^ getting participant by id: {}", id);
        return teamParticipantRepository.getOne(id);
    }


    /**
     * Expel participant from his team.
     * Changing status of participant to DELETED
     */
    public void expel(TeamParticipant teamParticipant) {
        if (isNull(teamParticipant)) {
            log.error("!> requesting expel for null participant. Check evoking clients");
            return;
        }
//        participant.setStatus(ParticipantStatusType.DELETED);
//        participant.setD(ParticipantStatusType.DELETED);
        teamParticipantRepository.expel(teamParticipant);
    }

}

package com.freetonleague.core.service.implementations;

import com.freetonleague.core.domain.dto.TeamBaseDto;
import com.freetonleague.core.domain.dto.TeamDto;
import com.freetonleague.core.domain.dto.TeamExtendedDto;
import com.freetonleague.core.domain.model.User;
import com.freetonleague.core.service.RestTeamFacade;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class RestTeamFacadeImpl implements RestTeamFacade {

    /**
     * Returns founded team by id
     */
    @Override
    public TeamDto getByUd(long id) {
        return null;
    }

    /**
     * Registry new team on platform
     */
    @Override
    public TeamDto add(TeamDto team, User user) {
        return null;
    }

    /**
     * Edit team on Portal.
     * Editable fields only logo, name
     */
    @Override
    public TeamExtendedDto edit(Long id, TeamBaseDto team, User user) {
        return null;
    }

    /**
     * Expel from requested team the specified participant.
     * Accessible only for a capitan of the team
     */
    @Override
    public TeamExtendedDto expel(Long id, Long participantId, User user) {
        return null;
    }

    /**
     * Disband all the band.
     * Accessible only for a capitan of the team
     */
    @Override
    public void disband(Long id, User user) {

    }

    /**
     * Quit current user from specified team
     */
    @Override
    public void quitUserFromTeam(Long id, User user) {

    }

    /**
     * Get the list of teams for current user
     */
    @Override
    public List<TeamExtendedDto> getUserTeamList(User user) {
        return null;
    }
}

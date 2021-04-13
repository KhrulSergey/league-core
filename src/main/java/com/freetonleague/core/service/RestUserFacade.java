package com.freetonleague.core.service;

import com.freetonleague.core.domain.dto.UserDto;
import com.freetonleague.core.domain.model.User;

/**
 * Service-facade for managing user in League-Core module
 */
public interface RestUserFacade {

    /**
     * Returns founded team by id
     *
     * @param id   of team to search
     * @param user current user from Session
     * @return team entity
     */
    UserDto getTeamById(long id, User user);

    /**
     * Getting user by username with privacy check
     */
    User getVerifiedUserByUsername(String username);

    /**
     * Getting user by leagueId with privacy check
     */
    User getVerifiedUserByLeagueId(String leagueId);
}

package com.freetonleague.core.service;

import com.freetonleague.core.domain.model.User;

/**
 * Service-facade for managing user in League-Core module
 */
public interface RestUserFacade {

    /**
     * Getting user by username with privacy check
     */
    User getVerifiedUserByUsername(String username);

    /**
     * Getting user by leagueId with privacy check
     */
    User getVerifiedUserByLeagueId(String leagueId);
}

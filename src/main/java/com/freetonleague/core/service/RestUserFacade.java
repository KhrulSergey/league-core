package com.freetonleague.core.service;

import com.freetonleague.core.domain.dto.UserDto;
import com.freetonleague.core.domain.dto.UserPublicDto;
import com.freetonleague.core.domain.filter.UserInfoFilter;
import com.freetonleague.core.domain.model.User;

import java.io.OutputStream;

/**
 * Service-facade for managing user in League-Core module
 */
public interface RestUserFacade {

    /**
     * Returns founded user by leagueId
     *
     * @param leagueId of user to search
     * @param user     current user from Session
     * @return team entity
     */
    UserPublicDto getUserByLeagueId(String leagueId, User user);

    /**
     * Getting user by username with privacy check
     */
    User getVerifiedUserByUsername(String username);

    /**
     * Getting user by leagueId with privacy check
     */
    User getVerifiedUserByLeagueId(String leagueId);

    UserDto updateUserInfoByFilter(UserInfoFilter filter, User user);

    /**
     * Import users from file to disk and specified outputStream (only for admin)
     * Data: list of user External Id and Bank Account Address
     */
    void importUsersDataFromFile(OutputStream outputStream);
}

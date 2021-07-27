package com.freetonleague.core.service;


import com.freetonleague.core.domain.dto.UserExternalInfo;
import com.freetonleague.core.domain.dto.finance.AccountInfoDto;
import com.freetonleague.core.domain.model.User;
import org.springframework.security.core.userdetails.UserDetailsService;

import java.util.List;
import java.util.UUID;

public interface UserService extends UserDetailsService {

    /**
     * Adding a new user to DB.
     *
     * @param user data to add
     * @return added User
     */
    User add(User user);

    /**
     * Returns User by LeagueID from DB or imported from League-Core module by leagueId.
     *
     * @param leagueId User's leagueID to search
     * @return User with a specific LeagueID, null - if the user is not found.
     */
    User findByLeagueId(UUID leagueId);

    /**
     * Returns found user from DB or imported from League-Core module by username.
     *
     * @param username User's username to search
     * @return user entity, null - if the user is not found.
     */
    User findByUsername(String username);

    /**
     * Loading user from DB or import from LeagueId-module.
     */
    User loadWithLeagueId(String leagueId, String sessionToken);

    /**
     * Returns creating user
     *
     * @param userExternalInfo
     * @return
     */
    User importUserToPlatform(UserExternalInfo userExternalInfo);

    /**
     * Edit an existing user in DB.
     *
     * @param user Updated User's data to be added to the database
     * @return Edited user
     */
    User edit(User user);

    /**
     * Update user bank account info
     *
     * @param leagueId       identifier of user to update data
     * @param accountInfoDto bank account data
     * @return updated user
     */
    User updateUserAccountInfo(UUID leagueId, AccountInfoDto accountInfoDto);

    /**
     * Returns list of all initiated users on portal
     *
     * @return list of user entities
     */
    List<User> findInitiatedUserList();

    /**
     * Returns list of all active users on portal
     *
     * @return list of user entities
     */
    List<User> findActiveUserList();
}

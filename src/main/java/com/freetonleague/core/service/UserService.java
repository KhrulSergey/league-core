package com.freetonleague.core.service;


import com.freetonleague.core.domain.model.User;
import org.springframework.security.core.userdetails.UserDetailsService;

import java.util.UUID;

public interface UserService extends UserDetailsService {
    /**
     * Adding a new user to DB.
     *
     * @param user User to add
     * @return Added User
     */
    User add(User user);

    /**
     * Getting a specific user from DB.
     *
     * @param leagueId User's leagueID to search
     * @return User with a specific LeagueID, null - if the user is not found.
     */
    User findByLeagueId(UUID leagueId);

    /**
     * Returns found user from DB by username.
     * Searching ONLY in League-Core module
     *
     * @param username User's username to search
     * @return user entity, null - if the user is not found.
     */
    User findByUsername(String username);

    /**
     * Loading user from DB or import from LeagueId-module.
     *
     * @param leagueId     User's leagueID to search
     * @param sessionToken Session token to request info from leagueID-module
     * @return user entity, null - if the user is not found.
     */
    User loadWithLeagueId(String leagueId, String sessionToken);

    /**
     * Edit an existing user in DB.
     *
     * @param user Updated User's data to be added to the database
     * @return Edited user
     */
    User edit(User user);

}

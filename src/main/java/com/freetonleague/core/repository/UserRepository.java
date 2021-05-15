package com.freetonleague.core.repository;

import com.freetonleague.core.domain.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.UUID;

/**
 * Access interface for the data of the "Users" entity in the database
 */
public interface UserRepository extends JpaRepository<User, Long> {
    /**
     * Getting user by leagueId
     */
    User findByLeagueId(UUID leagueId);

    /**
     * Checking the presence of a user in the DB by leagueId
     */
    boolean existsByLeagueId(UUID leagueId);

    /**
     * Return user found by username
     */
    User findByUsername(String username);

    /**
     * Check if user already existed on platform
     */
    boolean existsByUsername(String username);

    /**
     * Find all users with status INITIATED
     */
    @Query(value = "select u from User u where u.status = com.freetonleague.core.domain.enums.UserStatusType.INITIATED")
    List<User> findAllInitiatedUsers();
}

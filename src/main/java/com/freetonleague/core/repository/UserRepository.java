package com.freetonleague.core.repository;

import com.freetonleague.core.domain.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
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

    /** Return user found by username */
    User findByUsername(String username);
}

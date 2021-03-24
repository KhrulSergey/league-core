package com.freetonleague.core.service.implementations;

import com.freetonleague.core.domain.model.User;
import com.freetonleague.core.repository.UserRepository;
import com.freetonleague.core.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import javax.validation.Validator;
import java.util.Set;
import java.util.UUID;

@Transactional(propagation = Propagation.REQUIRES_NEW)
@Service
@RequiredArgsConstructor
@Slf4j
/**
 * Implementation of the service for accessing User data from the repository.
 */
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private final Validator validator;

    /**
     * Adding a new user to DB.
     *
     * @param user User to add
     * @return Added User
     */
    @Override
    public User add(User user) {
        Set<ConstraintViolation<User>> violations = validator.validate(user);
        if (violations.isEmpty()) {
            log.debug("^ User [ {} ] binding is successful", user);
            return userRepository.saveAndFlush(user);
        } else {
            log.warn("~ User [ {} ] have constraint violations: {}", user, violations);
            throw new ConstraintViolationException(violations);
        }
    }

    /**
     * Getting User by LeagueID from DB.
     *
     * @param leagueId User's leagueID to search
     * @return User with a specific LeagueID, null - if the user is not found.
     */
    @Override
    public User get(UUID leagueId) {
        User user = userRepository.findByLeagueId(leagueId);
        log.debug("^ getting user: {}", user);
        return user;
    }

    /**
     * Edit an existing user in DB.
     *
     * @param user Updated User's data to be added to the database
     * @return Edited user
     */
    @Override
    public User edit(User user) {
        User updatedUser;
        Set<ConstraintViolation<User>> violations = validator.validate(user);
        if (userRepository.existsByLeagueId(user.getLeagueId())) {
            if (violations.isEmpty()) {
                log.debug("user: {} is edited", user);
                updatedUser = userRepository.saveAndFlush(user);
            } else {
                log.warn("edited user: {} have constraint violations: {}", user, violations);
                throw new ConstraintViolationException(violations);
            }
        } else {
            log.warn("user: {} is not exist", user);
            throw new ConstraintViolationException(violations);
        }
        return updatedUser;
    }

    /**
     * Locates the user based on the username.
     *
     * @param username the username identifying the user whose data is required.
     * @return a fully populated user record (never <code>null</code>)
     * @throws UsernameNotFoundException if the user could not be found or the user has no
     *                                   GrantedAuthority
     */
    @Override
    public User loadUserByUsername(String username) throws UsernameNotFoundException {
        log.debug("!> try to find user by login: {}", username);
        User user = userRepository.findByUsername(username);
        if (user == null) {
            throw new UsernameNotFoundException("User not found");
        }

        return user;
    }
}

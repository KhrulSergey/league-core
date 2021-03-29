package com.freetonleague.core.service.implementations;

import com.freetonleague.core.cloudclient.LeagueIdClientService;
import com.freetonleague.core.domain.dto.UserDto;
import com.freetonleague.core.domain.enums.UserStatusType;
import com.freetonleague.core.domain.model.User;
import com.freetonleague.core.exception.ExceptionMessages;
import com.freetonleague.core.exception.UserManageException;
import com.freetonleague.core.mapper.UserMapper;
import com.freetonleague.core.repository.UserRepository;
import com.freetonleague.core.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import javax.validation.Validator;
import java.util.Set;
import java.util.UUID;

import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;
import static org.apache.commons.lang3.StringUtils.isBlank;

/**
 * Implementation of the service for accessing User data from the repository.
 */
@Transactional(propagation = Propagation.REQUIRES_NEW)
@Service
@RequiredArgsConstructor
@Slf4j
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final UserMapper mapper;
    private final LeagueIdClientService leagueIdClientService;
    private final Validator validator;

    /**
     * Adding a new user to DB.
     */
    @Override
    public User add(User user) {
        Set<ConstraintViolation<User>> violations = validator.validate(user);
        if (violations.isEmpty()) {
            log.debug("^ User [ {} ] binding is successful", user);
            user.setStatus(UserStatusType.ACTIVE);
            return userRepository.saveAndFlush(user);
        } else {
            log.warn("~ User [ {} ] have constraint violations: {}", user, violations);
            throw new ConstraintViolationException(violations);
        }
    }

    /**
     * Getting User by LeagueID from DB.
     */
    @Override
    public User findByLeagueId(UUID leagueId) {
        if (isNull(leagueId)) {
            log.error("!> requesting findByLeagueId for Blank leagueId. Check evoking clients");
            return null;
        }
        User user = userRepository.findByLeagueId(leagueId);
        log.debug("^ getting user: {}", user);
        return user;
    }

    /**
     * Returns found user from DB by username.
     * Searching ONLY in League-Core module
     *
     * @param username User's username to search
     * @return user entity, null - if the user is not found.
     */
    @Override
    public User findByUsername(String username) {
        if (isBlank(username)) {
            log.error("!> requesting findByUsername for Blank username. Check evoking clients");
            return null;
        }
        return userRepository.findByUsername(username);
    }

    /**
     * Loading user from DB or import from LeagueId-module.
     */
    @Override
    public User loadWithLeagueId(String leagueId, String sessionToken) {
        log.debug("^ trying to find user on BD with leagueId {}", leagueId);
        User user = this.findByLeagueId(UUID.fromString(leagueId));
        if (isNull(user)) {
            log.debug("^ trying to load user from LeagueId {}", leagueId);
            UserDto userDto = leagueIdClientService.getUser(sessionToken);
            if (nonNull(userDto)) {
                //create new user
                user = this.add(userDto);
            } else {
                log.warn("~ No user with leagueId {} found in LeagueId-module", leagueId);
            }
        }
        return user;
    }

    /**
     * Edit an existing user in DB.
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

    /** Add user to DB from LeagueId source data*/
    private User add(UserDto userDto){
        Set<ConstraintViolation<UserDto>> violations = validator.validate(userDto);
        if (violations.isEmpty()) {
            if (!isBlank(userDto.getUsername()) && this.isUserExistedByUserName(userDto.getUsername())) {
                log.error("^ user with username already exists on core module: {} but with DIFFERENT guid. Check data!", userDto.getUsername());
                throw new UserManageException(ExceptionMessages.USER_DUPLICATE_FOUND_ERROR,
                        String.format("Found duplicates by username '%s' on auth and data modules", userDto.getUsername()));
            }
            return this.add(mapper.fromDto(userDto));
        } else {
            log.warn("~ user: {} have constraint violations: {}", userDto, violations);
            throw new ConstraintViolationException(violations);
        }
    }

    /** Check if user already existed on platform*/
    public boolean isUserExistedByUserName(String username) {
        return userRepository.existsByUsername(username);
    }
}

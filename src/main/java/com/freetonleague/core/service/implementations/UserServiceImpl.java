package com.freetonleague.core.service.implementations;

import com.freetonleague.core.cloudclient.LeagueIdClientService;
import com.freetonleague.core.domain.dto.AccountInfoDto;
import com.freetonleague.core.domain.dto.UserDto;
import com.freetonleague.core.domain.dto.UserExternalInfo;
import com.freetonleague.core.domain.enums.AccountHolderType;
import com.freetonleague.core.domain.enums.UserRoleType;
import com.freetonleague.core.domain.enums.UserStatusType;
import com.freetonleague.core.domain.model.Role;
import com.freetonleague.core.domain.model.User;
import com.freetonleague.core.exception.UserManageException;
import com.freetonleague.core.exception.config.ExceptionMessages;
import com.freetonleague.core.mapper.UserMapper;
import com.freetonleague.core.repository.RoleRepository;
import com.freetonleague.core.repository.UserRepository;
import com.freetonleague.core.service.FinancialClientService;
import com.freetonleague.core.service.UserEventService;
import com.freetonleague.core.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import javax.validation.Validator;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;
import static org.apache.commons.lang3.StringUtils.isBlank;

/**
 * Implementation of the service for accessing User data from the repository.
 */
@Transactional
@Service
@RequiredArgsConstructor
@Slf4j
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final UserMapper mapper;
    private final LeagueIdClientService leagueIdClientService;
    private final Validator validator;

    @Lazy
    @Autowired
    private FinancialClientService financialClientService;

    @Lazy
    @Autowired
    private UserEventService userEventService;

    /**
     * Adding a new user to DB.
     */
    @Override
    public User add(User user) {
        Set<ConstraintViolation<User>> violations = validator.validate(user);
        if (!violations.isEmpty()) {
            log.warn("~ User [ '{}' ] have constraint violations: '{}'", user, violations);
            throw new ConstraintViolationException(violations);
        }
        log.debug("^ try to create new user with data: '{}'", user);
        user.setRoles(Collections.singleton(this.getRegularRole()));
        user.setStatus(UserStatusType.ACTIVE);
        AccountInfoDto userAccountDto = userEventService.processUserStatusChange(user, UserStatusType.CREATED);
        if (nonNull(userAccountDto)) {
            user.setBankAccountAddress(userAccountDto.getExternalAddress());
        }
        user = userRepository.saveAndFlush(user);
        return user;
    }

    /**
     * Returns User by LeagueID from DB or imported from League-Core module by leagueId.
     */
    @Override
    public User findByLeagueId(UUID leagueId) {
        log.debug("^ trying to find user in BD with leagueId '{}'", leagueId);
        if (isNull(leagueId)) {
            log.error("!> requesting findByLeagueId for Blank leagueId. Check evoking clients");
            return null;
        }
        User user = userRepository.findByLeagueId(leagueId);
        if (isNull(user)) {
            log.debug("^ trying to load user from LeagueId-module with id '{}'", leagueId);
            UserDto userDto = leagueIdClientService.getUserByLeagueId(leagueId);
            if (nonNull(userDto)) {
                //create new user
                return this.createFromDto(userDto);
            } else {
                log.warn("~ No user with leagueId '{}' found in LeagueId-module", leagueId);
            }
        }
        return user;
    }

    /**
     * Returns found user from DB or imported from League-Core module by username.
     */
    @Override
    public User findByUsername(String username) {
        log.debug("^ trying to find user in BD with username '{}'", username);
        if (isBlank(username)) {
            log.error("!> requesting findByUsername for Blank username. Check evoking clients");
            return null;
        }
        User user = userRepository.findByUsername(username);
        if (isNull(user)) {
            log.debug("^ trying to load user from LeagueId-module with username '{}'", username);
            UserDto userDto = leagueIdClientService.getUserByUserName(username);
            if (nonNull(userDto)) {
                //create new user
                user = this.createFromDto(userDto);
            } else {
                log.warn("~ No user with username '{}' found in LeagueId-module", username);
            }
        }
        return user;
    }

    /**
     * Loading user from DB or import from LeagueId-module.
     */
    @Override
    public User loadWithLeagueId(String leagueId, String sessionToken) {
        log.debug("^ trying to find user on BD with leagueId '{}'", leagueId);
        User user = this.findByLeagueId(UUID.fromString(leagueId));
        if (isNull(user)) {
            log.debug("^ trying to load user from LeagueId '{}'", leagueId);
            UserDto userDto = leagueIdClientService.getUser(sessionToken);
            if (nonNull(userDto)) {
                //create new user
                user = this.createFromDto(userDto);
            } else {
                log.warn("~ No user with leagueId '{}' found in LeagueId-module", leagueId);
            }
        }
        return user;
    }

    @Override
    public User importUserToPlatform(UserExternalInfo userExternalInfo) {
        if (isNull(userExternalInfo)) {
            log.error("!> requesting importUserToPlatform for NULL userExternalInfo. Check evoking clients");
            return null;
        }
        log.debug("^ try to import user with info '{}' to system", userExternalInfo);
        UserDto userDto = leagueIdClientService.createByExternalInfo(userExternalInfo);
        log.debug("^ found or create user with info '{}' in LeagueId module", userDto);
        if (isNull(userDto)) {
            log.warn("~ Error while creating user with userExternalInfo '{}' in LeagueId-module", userExternalInfo);
            return null;
        }
        User user = this.findByLeagueId(userDto.getLeagueId());

        if (isNull(user)) {
            //create new user from dto
            user = this.createFromDto(userDto);
        } else {
            AccountInfoDto accountInfo = financialClientService.getAccountByHolderInfo(user.getLeagueId(), AccountHolderType.USER);
            user.setBankAccountAddress(accountInfo.getExternalAddress());
        }
        if (isNull(user)) {
            log.warn("~ Error while creating user from userExternalInfo '{}' in LeagueCore-module", userExternalInfo);
            return null;
        }
        log.debug("^ found or create user with info '{}' in LeagueId module", userDto);
        return user;
    }

    /**
     * Edit an existing user in DB.
     */
    @Override
    public User edit(User user) {
        User updatedUser;
        Set<ConstraintViolation<User>> violations = validator.validate(user);

        if (!userRepository.existsByLeagueId(user.getLeagueId())) {
            log.warn("user: '{}' is not exist", user);
            throw new UserManageException(ExceptionMessages.USER_NOT_FOUND_ERROR,
                    "user with leagueId " + user.getLeagueId() + " is not exist");
        }
        if (!violations.isEmpty()) {
            log.warn("edited user: '{}' have constraint violations: '{}'", user, violations);
            throw new ConstraintViolationException(violations);
        }
        log.debug("user: '{}' is edited", user);
        updatedUser = userRepository.save(user);
        //todo check changed status
        userEventService.processUserStatusChange(user, user.getStatus());
        return updatedUser;
    }

    /**
     * Returns list of all initiated users on portal
     */
    @Override
    public List<User> findInitiatedUserList() {
        return userRepository.findAllInitiatedUsers();
    }

    /**
     * Returns list of all active users on portal
     */
    @Override
    public List<User> findActiveUserList() {
        return userRepository.findAllActiveUsers();
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
        log.debug("!> try to find user by login: '{}' for Spring Auth", username);
        User user = this.findByUsername(username);
        if (user == null) {
            throw new UsernameNotFoundException("User not found");
        }
        return user;
    }

    /**
     * Check if user already existed on platform
     */
    public boolean isUserExistedByUserName(String username) {
        return userRepository.existsByUsername(username);
    }

    /**
     * Create user to DB from LeagueId source data
     */
    private User createFromDto(UserDto userDto) {
        Set<ConstraintViolation<UserDto>> violations = validator.validate(userDto);
        if (!violations.isEmpty()) {
            log.warn("~ user: '{}' have constraint violations: '{}'", userDto, violations);
            throw new ConstraintViolationException(violations);
        }
        if (!isBlank(userDto.getUsername()) && this.isUserExistedByUserName(userDto.getUsername())) {
            log.error("^ user with username already exists on core module: '{}' but with DIFFERENT GUID. Check data!", userDto.getUsername());
            throw new UserManageException(ExceptionMessages.USER_DUPLICATE_FOUND_ERROR,
                    String.format("Found duplicates by username '%s' on auth and data modules", userDto.getUsername()));
        }
        log.debug("^ try to create new user from dto: '{}'", userDto);
        return this.add(mapper.fromDto(userDto));
    }

    private Role getRegularRole() {
        return roleRepository.findByName(UserRoleType.REGULAR);
    }
}

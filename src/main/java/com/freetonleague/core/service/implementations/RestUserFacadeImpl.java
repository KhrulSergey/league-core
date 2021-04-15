package com.freetonleague.core.service.implementations;

import com.freetonleague.core.domain.dto.UserDto;
import com.freetonleague.core.domain.model.User;
import com.freetonleague.core.exception.ExceptionMessages;
import com.freetonleague.core.exception.UserManageException;
import com.freetonleague.core.exception.ValidationException;
import com.freetonleague.core.mapper.UserMapper;
import com.freetonleague.core.service.RestUserFacade;
import com.freetonleague.core.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.UUID;

import static java.util.Objects.isNull;
import static org.apache.commons.lang3.StringUtils.isBlank;

@Slf4j
@RequiredArgsConstructor
@Service
public class RestUserFacadeImpl implements RestUserFacade {

    private final UserService userService;
    private final UserMapper userMapper;


    /**
     * Returns founded user by leagueId
     */
    @Override
    public UserDto getUserByLeagueId(String leagueId, User user) {
        return userMapper.toDto(this.getVerifiedUserByLeagueId(leagueId));
    }

    /**
     * Getting user by username with privacy check
     */
    @Override
    public User getVerifiedUserByUsername(String username) {
        if (isBlank(username)) {
            log.warn("~ parameter 'username' is not set for getVerifiedUserByUsername");
            throw new ValidationException(ExceptionMessages.USER_REQUIRED_ERROR, "username",
                    "parameter username is required for getVerifiedUserByUsername");
        }
        User user = userService.findByUsername(username);
        if (isNull(user)) {
            log.debug("^ User was not found for request parameter username {}", username);
            throw new UserManageException(ExceptionMessages.USER_NOT_FOUND_ERROR,
                    String.format("Requested parameter username: '%s'", username));
        }
        return user;
    }

    /**
     * Getting user by leagueId with privacy check
     */
    @Override
    public User getVerifiedUserByLeagueId(String leagueId) {
        if (isBlank(leagueId)) {
            log.warn("~ parameter 'leagueId' is not set for getVerifiedUserByLeagueId");
            throw new ValidationException(ExceptionMessages.USER_REQUIRED_ERROR, "leagueId",
                    "parameter leagueId is required for getVerifiedUserByLeagueId");
        }
        User user = userService.findByLeagueId(UUID.fromString(leagueId));
        if (isNull(user)) {
            log.debug("^ User was not found for request parameter leagueId {}", leagueId);
            throw new UserManageException(ExceptionMessages.USER_NOT_FOUND_ERROR,
                    String.format("Requested parameter leagueId: '%s'", leagueId));
        }
        return user;
    }
}

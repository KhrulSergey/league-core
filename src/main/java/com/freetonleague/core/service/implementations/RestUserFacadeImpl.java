package com.freetonleague.core.service.implementations;

import com.freetonleague.core.domain.dto.UserDto;
import com.freetonleague.core.domain.dto.UserPublicDto;
import com.freetonleague.core.domain.filter.UserInfoFilter;
import com.freetonleague.core.domain.model.User;
import com.freetonleague.core.exception.UserManageException;
import com.freetonleague.core.exception.ValidationException;
import com.freetonleague.core.exception.config.ExceptionMessages;
import com.freetonleague.core.mapper.UserMapper;
import com.freetonleague.core.security.permissions.CanManageSystem;
import com.freetonleague.core.service.RestUserFacade;
import com.freetonleague.core.service.UserService;
import com.freetonleague.core.util.CsvFileUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.File;
import java.io.OutputStream;
import java.util.UUID;

import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;
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
    public UserPublicDto getUserByLeagueId(String leagueId, User user) {
        User foundUser = this.getVerifiedUserByLeagueId(leagueId);
        //Hide service (hidden) users
        if (nonNull(foundUser) && foundUser.isHidden()) {
            foundUser = null;
        }
        return userMapper.toPubicDto(foundUser);
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
        //Hide service (hidden) users
        if (nonNull(user) && user.isHidden()) {
            user = null;
        }
        if (isNull(user)) {
            log.debug("^ User was not found for request parameter username '{}'", username);
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
        //Hide service (hidden) users
        if (nonNull(user) && user.isHidden()) {
            user = null;
        }
        if (isNull(user)) {
            log.debug("^ User was not found for request parameter leagueId '{}'", leagueId);
            throw new UserManageException(ExceptionMessages.USER_NOT_FOUND_ERROR,
                    String.format("Requested parameter leagueId: '%s'", leagueId));
        }
        return user;
    }

    @Override
    @Transactional
    public UserDto updateUserInfoByFilter(UserInfoFilter filter, User user) {
        User selectedUser = userService.findByUsername(user.getUsername());

        userMapper.applyChanges(selectedUser, filter);

        return userMapper.toDto(selectedUser);
    }

    /**
     * Import users from file to disk and specified outputStream (only for admin)
     * Data: list of user External Id and Bank Account Address
     */
    @Override
    @CanManageSystem
    public void importUsersDataFromFile(OutputStream outputStream) {
        log.debug("^ try to get import file with user data from disk");
        String exportFilePath = System.getProperty("user.dir") + File.separator + "user_data_export.csv";
        CsvFileUtil.saveFileToOutputStream(exportFilePath, outputStream);
    }
}

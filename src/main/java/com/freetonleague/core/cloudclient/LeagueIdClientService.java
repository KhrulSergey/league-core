package com.freetonleague.core.cloudclient;

import com.freetonleague.core.domain.dto.SessionDto;
import com.freetonleague.core.domain.dto.UserDto;
import com.freetonleague.core.domain.dto.UserExternalInfo;
import feign.FeignException;
import feign.FeignException.FeignClientException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.UUID;

import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;
import static org.apache.commons.lang3.StringUtils.isBlank;

/**
 * Service for importing data from a LeagueId module
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class LeagueIdClientService {

    private final LeagueIdClientCloud leagueIdClientCloud;

    @Value("${freetonleague.service.league-id.service-token}")
    private String leagueIdServiceToken;

    public SessionDto getSession(String token) {
        SessionDto sessionFromLeagueId = null;
        if (!isBlank(token)) {
            try {
                sessionFromLeagueId = leagueIdClientCloud.getSession(token);
            } catch (FeignClientException exc) {
                //TODO habdle exception
                log.error("New FeignClientException exc '{}'", exc, exc);
            } catch (FeignException exc) {
                log.error("New FeignException exc '{}'", exc, exc);
            }
        }
        return sessionFromLeagueId;
    }

    public UserDto getUser(String token) {
        UserDto userInfo = null;
        if (!isBlank(token)) {
            try {
                userInfo = leagueIdClientCloud.account(token);
            } catch (FeignClientException exc) {
                //TODO habdle exception
                log.error("New FeignClientException exc '{}'", exc, exc);
            } catch (FeignException exc) {
                log.error("New FeignException exc '{}'", exc, exc);
            }
        }
        return userInfo;
    }

    public UserDto getUserByLeagueId(UUID leagueId) {
        log.debug("^ try to getUserByLeagueId in LeagueIdClientService by serviceToken '{}' and leagueId '{}'", leagueIdServiceToken, leagueId);
        UserDto userInfo = null;
        if (nonNull(leagueId)) {
            try {
                userInfo = leagueIdClientCloud.getUserByLeagueId(leagueIdServiceToken, leagueId.toString());
            } catch (FeignClientException exc) {
                //TODO habdle exception
                log.error("New FeignClientException exc '{}'", exc, exc);
            } catch (FeignException exc) {
                log.error("New FeignException exc '{}'", exc, exc);
            }
        }
        return userInfo;
    }

    public UserDto getUserByUserName(String username) {
        UserDto userInfo = null;
        if (!isBlank(username)) {
            try {
                userInfo = leagueIdClientCloud.getUserByUsername(leagueIdServiceToken, username);
            } catch (FeignClientException exc) {
                //TODO habdle exception
                log.error("New FeignClientException exc '{}'", exc, exc);
            } catch (FeignException exc) {
                log.error("New FeignException exc '{}'", exc, exc);
            }
        }
        return userInfo;
    }

    public UserDto getByUserExternalId(UserExternalInfo userExternalInfo) {
        UserDto userInfo = null;
        if (!isNull(userExternalInfo)) {
            try {
                log.debug("^ try to get user with info '{}' from LeagueId module", userExternalInfo);
                userInfo = leagueIdClientCloud.getByUserExternalId(leagueIdServiceToken,
                        userExternalInfo.getExternalProvider(), userExternalInfo.getExternalId());
            } catch (FeignClientException exc) {
                //TODO habdle exception
                log.error("New FeignClientException exc '{}'", exc, exc);
            } catch (FeignException exc) {
                log.error("New FeignException exc '{}'", exc, exc);
            }
        }
        return userInfo;
    }

    public UserDto createByExternalInfo(UserExternalInfo userExternalInfo) {
        UserDto userInfo = null;
        if (!isNull(userExternalInfo)) {
            try {
                log.debug("^ try to create user with info '{}' in LeagueId module", userExternalInfo);
                userInfo = leagueIdClientCloud.createByExternalInfo(leagueIdServiceToken, userExternalInfo);
            } catch (FeignClientException exc) {
                //TODO habdle exception
                log.error("New FeignClientException exc '{}'", exc, exc);
            } catch (FeignException exc) {
                log.error("New FeignException exc '{}'", exc, exc);
            }
        }
        return userInfo;
    }

}

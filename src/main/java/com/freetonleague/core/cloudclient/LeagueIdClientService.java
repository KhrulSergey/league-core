package com.freetonleague.core.cloudclient;

import com.freetonleague.core.domain.dto.SessionDto;
import com.freetonleague.core.domain.dto.UserDto;
import feign.FeignException;
import feign.FeignException.FeignClientException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import static org.apache.commons.lang3.StringUtils.isBlank;

/**
 * Service for importing data from a LeagueId module
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class LeagueIdClientService {

    private final LeagueIdClientCloud leagueIdClientCloud;

    @Value("${freetonleague.service.league-id.service-token::Pu6ThMMkF4GFTL5Vn6F45PHSaC193232HGdsQ}")
    private String leagueIdServiceToken;

    public SessionDto getSession(String token) {
        SessionDto sessionFromLeagueId = null;
        if (!isBlank(token)) {
            try {
                sessionFromLeagueId = leagueIdClientCloud.getSession(token);
            } catch (FeignClientException exc) {
                //TODO habdle exception
                log.error("New FeignClientException exc {}", exc, exc);
            } catch (FeignException exc) {
                log.error("New FeignException exc {}", exc, exc);
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
                log.error("New FeignClientException exc {}", exc, exc);
            } catch (FeignException exc) {
                log.error("New FeignException exc {}", exc, exc);
            }
        }
        return userInfo;
    }

    public UserDto getUserByLeagueId(String leagueId) {
        log.debug("^ try to getUserByLeagueId in LeagueIdClientService by serviceToken {} and leagueId {}", leagueIdServiceToken, leagueId);
        UserDto userInfo = null;
        if (!isBlank(leagueId)) {
            try {
                userInfo = leagueIdClientCloud.getUserByLeagueId(leagueIdServiceToken, leagueId);
            } catch (FeignClientException exc) {
                //TODO habdle exception
                log.error("New FeignClientException exc {}", exc, exc);
            } catch (FeignException exc) {
                log.error("New FeignException exc {}", exc, exc);
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
                log.error("New FeignClientException exc {}", exc, exc);
            } catch (FeignException exc) {
                log.error("New FeignException exc {}", exc, exc);
            }
        }
        return userInfo;
    }

}

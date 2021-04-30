package com.freetonleague.core.cloudclient;

import com.freetonleague.core.domain.dto.SessionDto;
import com.freetonleague.core.domain.dto.UserDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;


/**
 * Rest client for importing data from a LeagueId module
 */
@FeignClient(name = "league-id-client", url = "${freetonleague.service.leagueId.url}")
public interface LeagueIdClientCloud {

    String AUTH_TOKEN = "X-Auth-Token";

    String ACCESS_TOKEN = "access_token";

    @GetMapping("/user/get-by-leagueId")
    UserDto getUserByLeagueId(@RequestHeader(ACCESS_TOKEN) String accessToken,
                              @RequestHeader("leagueId") String leagueId);

    @GetMapping("/user/get-by-username")
    UserDto getUserByUsername(@RequestHeader(ACCESS_TOKEN) String accessToken,
                              @RequestHeader("username") String username);

    @PostMapping("/auth/session")
    SessionDto getSession(@RequestHeader(AUTH_TOKEN) String token);
}

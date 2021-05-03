package com.freetonleague.core.cloudclient;

import com.freetonleague.core.domain.dto.SessionDto;
import com.freetonleague.core.domain.dto.UserDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;


/**
 * Rest client for importing data from a LeagueId module
 */
@FeignClient(name = "league-id-client", url = "${freetonleague.service.league-id.url}")
public interface LeagueIdClientCloud {

    String AUTH_TOKEN = "X-Auth-Token";

    String SERVICE_TOKEN = "service_token";

    @GetMapping("/user/get-by-leagueId")
    UserDto getUserByLeagueId(@RequestParam(SERVICE_TOKEN) String serviceToken,
                              @RequestParam("leagueId") String leagueId);

    @GetMapping("/user/get-by-username")
    UserDto getUserByUsername(@RequestParam(SERVICE_TOKEN) String serviceToken,
                              @RequestParam("username") String username);

    @PostMapping("/auth/session")
    SessionDto getSession(@RequestHeader(AUTH_TOKEN) String token);

    @GetMapping("/user/current")
    UserDto account(@RequestHeader(AUTH_TOKEN) String token);
}

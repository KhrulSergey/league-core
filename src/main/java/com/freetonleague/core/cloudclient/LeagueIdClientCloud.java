package com.freetonleague.core.cloudclient;

import com.freetonleague.core.domain.dto.SessionDto;
import com.freetonleague.core.domain.dto.UserDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;


/** Rest client for importing data from a LeagueId module*/
@FeignClient(name = "league-id-client", url = "${freetonleague.service.leagueId.url}")
public interface LeagueIdClientCloud {

    String AUTH_TOKEN = "X-Auth-Token";

    @GetMapping("/user/current")
    UserDto account(@RequestHeader(AUTH_TOKEN) String token);

    @PostMapping("/auth/session")
    SessionDto session(@RequestHeader(AUTH_TOKEN) String token);
}

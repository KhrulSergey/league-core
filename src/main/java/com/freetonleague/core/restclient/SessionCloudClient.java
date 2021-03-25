package com.freetonleague.core.restclient;


import com.freetonleague.core.domain.dto.UserDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;


@FeignClient(name = "league-id-client", url = "${freetonleague.service.leagueId.url}")
public interface SessionCloudClient {

    String AUTH_TOKEN = "X-Auth-Token";

	@GetMapping("/user/current")
	UserDto account(@RequestHeader(AUTH_TOKEN) String token);
}

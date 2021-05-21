package com.freetonleague.core.cloudclient;

import com.freetonleague.core.domain.dto.MediaResourceDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;


/**
 * Rest client for importing data from a LeagueId module
 */
@FeignClient(name = "league-storage-client", url = "${freetonleague.service.league-storage.url}")
public interface LeagueStorageClientCloud {

    /**
     * The same value as from "${freetonleague.session.service-token-name}"
     */
    String staticServiceTokenName = "service_token";

    @PostMapping("/resource/")
    MediaResourceDto saveMediaResource(@RequestParam(staticServiceTokenName) String serviceToken,
                                       @RequestBody MediaResourceDto mediaResourceDto);
}

package com.freetonleague.core.service.financeUnit.cloud;

import com.freetonleague.core.domain.dto.AccountBroxusResponseDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * Rest client for interact with Third-party Bank Accounting Provider "Broxus"
 */
@FeignClient(name = "broxus-client", url = "${config.broxus-client.url}")
public interface BroxusAccountingClientCloud {

    String AUTH_TOKEN = "token";
    String ACCOUNT_IDENTIFIER = "userId";

    @GetMapping("/get-user-balance")
    AccountBroxusResponseDto getAccountBalance(@RequestParam(AUTH_TOKEN) String token,
                                               @RequestParam(ACCOUNT_IDENTIFIER) String accountGUID);

    @GetMapping("/get-deposit-address")
    AccountBroxusResponseDto createBroxusAccount(@RequestParam(AUTH_TOKEN) String token,
                                                 @RequestParam(ACCOUNT_IDENTIFIER) String accountGUID);
}
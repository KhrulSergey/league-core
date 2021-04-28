package com.freetonleague.core.service.financeUnit.cloud;

import com.freetonleague.core.domain.dto.AccountBroxusDataDto;
import com.freetonleague.core.domain.dto.AccountBroxusResponseDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.UUID;

/**
 * Rest client for interact with Third-party Bank Accounting Provider "Broxus"
 */
@Slf4j
@RequiredArgsConstructor
@Component("broxusMock")
public class BroxusAccountingClientCloudMockImpl implements BroxusAccountingClientCloud {

    @Override
    public AccountBroxusResponseDto getAccountBalance(String token, String accountGUID) {
        AccountBroxusDataDto data = AccountBroxusDataDto.builder()
                .balance(0.0)
                .build();
        return AccountBroxusResponseDto.builder()
                .success(true)
                .data(data)
                .build();
    }

    @Override
    public AccountBroxusResponseDto createBroxusAccount(String token, String accountGUID) {
        AccountBroxusDataDto data = AccountBroxusDataDto.builder()
                .address(UUID.randomUUID().toString())
                .balance(0.0)
                .build();
        return AccountBroxusResponseDto.builder()
                .success(true)
                .data(data)
                .build();
    }
}

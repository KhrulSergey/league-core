package com.freetonleague.core.service.financeUnit.cloud;

import com.freetonleague.core.domain.dto.finance.AccountBroxusDataDto;
import com.freetonleague.core.domain.dto.finance.AccountBroxusResponseDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.UUID;

/**
 * Rest client for interact with Third-party Bank Accounting Provider "Broxus"
 */
@Slf4j
@RequiredArgsConstructor
@Component("broxusInterlayerClientMock")
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

    @Override
    public AccountBroxusResponseDto registerWithdrawTransaction(String token, String accountGUID, String targetAccountExternalAddress, Double amount) {
        return AccountBroxusResponseDto.builder()
                .success(true)
                .data(null)
                .build();
    }
}

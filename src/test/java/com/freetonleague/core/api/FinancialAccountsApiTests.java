package com.freetonleague.core.api;

import com.freetonleague.core.common.IntegrationTest;
import com.freetonleague.core.controller.FinancialAccountsController;
import com.freetonleague.core.domain.dto.MPubgTonExchangeAmountDto;
import com.freetonleague.core.domain.enums.AccountHolderType;
import com.freetonleague.core.domain.enums.AccountTransactionStatusType;
import com.freetonleague.core.domain.enums.TransactionTemplateType;
import com.freetonleague.core.domain.enums.TransactionType;
import com.freetonleague.core.domain.filter.MPubgTonWithdrawalCreationFilter;
import com.freetonleague.core.domain.model.Account;
import com.freetonleague.core.domain.model.AccountTransaction;
import com.freetonleague.core.domain.model.SettingsEntity;
import com.freetonleague.core.domain.model.User;
import com.freetonleague.core.repository.SettingsRepository;
import com.freetonleague.core.service.FinancialClientService;
import com.freetonleague.core.service.SettingsService;
import com.freetonleague.core.service.financeUnit.FinancialUnitService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.Map;

class FinancialAccountsApiTests extends IntegrationTest {

    @Autowired
    private TestRestTemplate testRestTemplate;

    @Autowired
    private FinancialUnitService financialUnitService;

    @Autowired
    private FinancialClientService financialClientService;

    @Autowired
    private SettingsRepository settingsRepository;

    @Autowired
    private SettingsService settingsService;

    @Test
    public void gettingTonToUcExchangeAmountShouldReturnCorrectValues() {
        SettingsEntity settingsEntity = settingsRepository.findByKey(SettingsService.TON_TO_UC_EXCHANGE_RATE_KEY).get();
        settingsEntity.setValue("10.0");
        settingsRepository.save(settingsEntity);
        settingsService.forceUpdate();

        double tonAmount = 10.0;
        double expectedUcAmount = 100.0;

        ResponseEntity<MPubgTonExchangeAmountDto> responseEntity = testRestTemplate.getForEntity(
                FinancialAccountsController.BASE_PATH + FinancialAccountsController.PATH_WITHDRAW_TO_MPUBG + "?tonAmount={tonAmount}",
                MPubgTonExchangeAmountDto.class,
                Map.of("tonAmount", tonAmount)
        );

        Assertions.assertNotNull(responseEntity.getBody());
        Assertions.assertEquals(expectedUcAmount, responseEntity.getBody().getUcAmount());
        Assertions.assertEquals(tonAmount, responseEntity.getBody().getTonAmount());

    }

    @Test
    public void gettingTonToUcExchangeAmountShouldReturnBadRequestWithoutParameters() {
        ResponseEntity<MPubgTonExchangeAmountDto> responseEntity = testRestTemplate.getForEntity(
                FinancialAccountsController.BASE_PATH + FinancialAccountsController.PATH_WITHDRAW_TO_MPUBG,
                MPubgTonExchangeAmountDto.class
        );

        Assertions.assertEquals(HttpStatus.BAD_REQUEST, responseEntity.getStatusCode());

    }

    @Test
    public void balanceShouldChangedAfterMPubgWithdrawalRequest(User user, HttpHeaders httpHeaders) {
        financialClientService.createAccountByHolderInfo(
                user.getLeagueId(), AccountHolderType.USER, user.getUsername()
        );

        Double balance;

        {
            Account account = financialUnitService.getAccountByHolderExternalGUIDAndType(
                    user.getLeagueId(), AccountHolderType.USER);

            financialUnitService.createTransaction(
                    AccountTransaction.builder()
                            .amount(10.0)
                            .targetAccount(account)
                            .transactionTemplateType(TransactionTemplateType.EXTERNAL_PROVIDER)
                            .status(AccountTransactionStatusType.FINISHED)
                            .transactionType(TransactionType.DEPOSIT)
                            .build()
            );

            balance = account.getAmount();
        }

        HttpEntity<MPubgTonWithdrawalCreationFilter> httpEntity = new HttpEntity<>(MPubgTonWithdrawalCreationFilter.builder()
                .tonAmount(10.0)
                .pubgId("")
                .build(),
                httpHeaders
        );

        ResponseEntity<String> responseEntity = testRestTemplate.exchange(
                FinancialAccountsController.BASE_PATH + FinancialAccountsController.PATH_WITHDRAW_TO_MPUBG,
                HttpMethod.POST,
                httpEntity,
                String.class
        );

        Assertions.assertEquals(HttpStatus.NO_CONTENT, responseEntity.getStatusCode());

        {
            Account account = financialUnitService.getAccountByHolderExternalGUIDAndType(
                    user.getLeagueId(), AccountHolderType.USER);

            Assertions.assertNotEquals(balance, account.getAmount());
        }

    }

}

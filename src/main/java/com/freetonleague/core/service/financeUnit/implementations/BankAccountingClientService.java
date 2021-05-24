package com.freetonleague.core.service.financeUnit.implementations;

import com.freetonleague.core.domain.dto.AccountBroxusResponseDto;
import com.freetonleague.core.domain.dto.AccountExternalInfoDto;
import com.freetonleague.core.domain.dto.AccountTransactionExternalInfoDto;
import com.freetonleague.core.domain.enums.BankProviderType;
import com.freetonleague.core.domain.model.Account;
import com.freetonleague.core.domain.model.AccountTransaction;
import com.freetonleague.core.service.financeUnit.cloud.BroxusAccountingClientCloud;
import feign.FeignException;
import feign.FeignException.FeignClientException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;

import static java.util.Objects.isNull;
import static org.apache.commons.lang3.StringUtils.isBlank;

/**
 * Service-client for interact with Third-party Bank Accounting Providers
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class BankAccountingClientService {

    private final BroxusAccountingClientCloud broxusAccountingClientCloud;

    @Autowired
    @Qualifier("broxusMock")
    private BroxusAccountingClientCloud broxusAccountingMockClient;

    @Value("${config.broxus-client.token:}")
    private String broxusClientToken;

    @Value("${config.broxus-client.debug:true}")
    private Boolean isBroxusClientMock;

    @PostConstruct
    private void postConstruct() {
        if (isBlank(broxusClientToken)) {
            log.error("!!> broxusClientToken is not defined in app.properties");
        }
    }

    /**
     * Returns new external bank account for specified account
     */
    public AccountExternalInfoDto createExternalBankAddressForAccount(Account coreAccount) {
        if (isNull(coreAccount) || isNull(coreAccount.getGUID())) {
            log.error("!> requesting createExternalBankAddress for NULL coreAccount {} or NULL account GUID {}. Check evoking clients",
                    coreAccount, null);
            return null;
        }
        AccountBroxusResponseDto externalAccountInfo;
        log.debug("^ try to create external bank account (address) for specified core-account with guid {} for holder {}",
                coreAccount.getGUID(), coreAccount.getHolder());

        BroxusAccountingClientCloud currentBroxusClient = isBroxusClientMock ? broxusAccountingMockClient
                : broxusAccountingClientCloud;
        try {
            externalAccountInfo = currentBroxusClient.createBroxusAccount(this.broxusClientToken,
                    coreAccount.getGUID().toString());
            log.debug("^ response from external bank account for specified core-account with guid {} was {}",
                    coreAccount.getGUID(), externalAccountInfo);
        } catch (FeignClientException exc) {
            //TODO handle exception
            log.error("!!> Error while createExternalBankAddressForAccount in BankAccountingClientService. Transmitted data: {}. New FeignClientException exc {}",
                    coreAccount, exc, exc);
            return null;
        } catch (FeignException exc) {
            log.error("!!> Error while createExternalBankAddressForAccount in BankAccountingClientService. Transmitted data: {}. New FeignException exc {}",
                    coreAccount, exc, exc);
            return null;
        }

        if (isNull(externalAccountInfo) || !externalAccountInfo.isSuccess()) {
            //TODO handle errors with microservice
            log.error("!!> Error in response from Broxus-Client while createExternalBankAddressForAccount in " +
                            "BankAccountingClientService. Transmitted data: {}. Received data: {} ",
                    coreAccount, externalAccountInfo);
            return null;
        }
        if (isNull(externalAccountInfo.getData()) || isBlank(externalAccountInfo.getData().getAddress())) {
            //TODO handle errors with data from microservice
            log.error("!!> Error in data response while Broxus-Client while createExternalBankAddressForAccount in " +
                            "BankAccountingClientService. Transmitted data: {}. Received data: {} ",
                    coreAccount, externalAccountInfo);
        }
        return AccountExternalInfoDto.builder()
                .externalBankAddress(externalAccountInfo.getData().getAddress())
                .balance(externalAccountInfo.getData().getBalance())
                .bankType(BankProviderType.BROXUS)
                .build();
    }

    /**
     * Returns balance for specified account
     */
    public AccountExternalInfoDto getAccountBalance(Account coreAccount) {
        if (isNull(coreAccount) || isNull(coreAccount.getGUID())) {
            log.error("!> requesting getAccountBalance for NULL coreAccount {} or NULL account GUID {}. Check evoking clients",
                    coreAccount, null);
            return null;
        }
        AccountBroxusResponseDto externalAccountInfo;
        log.debug("^ try to get balance from external bank provider for specified core-account guid {} for holder {}",
                coreAccount.getGUID(), coreAccount.getHolder());

        BroxusAccountingClientCloud currentBroxusClient = isBroxusClientMock ? broxusAccountingMockClient
                : broxusAccountingClientCloud;
        try {
            externalAccountInfo = currentBroxusClient.getAccountBalance(this.broxusClientToken,
                    coreAccount.getGUID().toString());
        } catch (FeignClientException exc) {
            //TODO handle exception
            log.error("!!> Error while getAccountBalance in BankAccountingClientService. Transmitted data: {}. New FeignClientException exc {}",
                    coreAccount, exc, exc);
            return null;
        } catch (FeignException exc) {
            log.error("!!> Error while getAccountBalance in BankAccountingClientService. Transmitted data: {}. New FeignException exc {}",
                    coreAccount, exc, exc);
            return null;
        }

        if (isNull(externalAccountInfo) || !externalAccountInfo.isSuccess()) {
            //TODO handle errors with microservice
            log.error("!!> Error in response from Broxus-Client while getAccountBalance in " +
                            "BankAccountingClientService. Transmitted data: {}. Received data: {} ",
                    coreAccount, externalAccountInfo);
            return null;
        }
        if (isNull(externalAccountInfo.getData()) || isNull(externalAccountInfo.getData().getBalance())) {
            //TODO handle errors with data from microservice
            log.error("!!> Error in data response while Broxus-Client while getAccountBalance in " +
                            "BankAccountingClientService. Transmitted data: {}. Received data: {} ",
                    coreAccount, externalAccountInfo);
        }
        return AccountExternalInfoDto.builder()
                .externalBankAddress(externalAccountInfo.getData().getAddress())
                .balance(externalAccountInfo.getData().getBalance())
                .bankType(BankProviderType.BROXUS)
                .build();
    }

    /**
     * Returns information for conducted transaction
     */
    public AccountTransactionExternalInfoDto requestTransaction(AccountTransaction accountTransaction) {
        if (isNull(accountTransaction) || isNull(accountTransaction.getGUID())) {
            log.error("!> requesting requestTransaction for NULL accountTransaction. Check evoking clients");
            return null;
        }
        log.debug("^ trying to send request transaction to Bank Client {}", accountTransaction.getGUID());
        AccountTransactionExternalInfoDto externalTransactionInfoDto = AccountTransactionExternalInfoDto.builder()
                .build();
        log.debug("^ request transaction was successfully saved to Bank Client {}", accountTransaction.getGUID());
        return externalTransactionInfoDto;
    }
}

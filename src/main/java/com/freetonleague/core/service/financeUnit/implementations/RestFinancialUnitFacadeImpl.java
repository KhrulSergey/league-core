package com.freetonleague.core.service.financeUnit.implementations;

import com.freetonleague.core.domain.dto.AccountDepositFinUnitDto;
import com.freetonleague.core.domain.dto.AccountInfoDto;
import com.freetonleague.core.domain.dto.AccountTransactionInfoDto;
import com.freetonleague.core.domain.enums.*;
import com.freetonleague.core.domain.model.Account;
import com.freetonleague.core.domain.model.AccountHolder;
import com.freetonleague.core.domain.model.AccountTransaction;
import com.freetonleague.core.exception.CustomUnexpectedException;
import com.freetonleague.core.exception.ExceptionMessages;
import com.freetonleague.core.exception.FinancialUnitManageException;
import com.freetonleague.core.exception.ValidationException;
import com.freetonleague.core.mapper.AccountFinUnitMapper;
import com.freetonleague.core.mapper.AccountTransactionFinUnitMapper;
import com.freetonleague.core.service.financeUnit.FinancialUnitService;
import com.freetonleague.core.service.financeUnit.RestFinancialUnitFacade;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import javax.validation.Validator;
import java.util.Set;
import java.util.UUID;

import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;
import static org.apache.commons.lang3.StringUtils.isBlank;

/**
 * Service-facade for provide data from inner DB and process requests (incl. callback from bank-providers) to save data
 * Also Validate request and incoming data
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class RestFinancialUnitFacadeImpl implements RestFinancialUnitFacade {

    private final FinancialUnitService financialUnitService;    //call service to get/save data from DB
    private final AccountFinUnitMapper accountMapper;
    private final AccountTransactionFinUnitMapper accountTransactionFinUnitMapper;
    private final Validator validator;

    /**
     * Process deposit transfer request to user account
     */
    @Override
    public void processDeposit(String token, AccountDepositFinUnitDto accountDepositInfo, BankProviderType providerType) {
        log.info("New deposit request with token '{}' from external provider '{}' with data: {}",
                token, providerType, accountDepositInfo);

        if (!financialUnitService.validateFinanceTokenForDeposit(token)) {
            log.error("!!> Specified token {} is not valid for operate with deposit transactions. Request denied", token);
            throw new ValidationException(ExceptionMessages.FINANCE_UNIT_TOKEN_VALIDATION_ERROR, "token",
                    "parameter token is not valid for processDeposit");
        }

        Set<ConstraintViolation<AccountDepositFinUnitDto>> settingsViolations = validator.validate(accountDepositInfo);
        if (!settingsViolations.isEmpty()) {
            log.debug("^ transmitted account deposit info dto: {} have constraint violations: {}",
                    accountDepositInfo, settingsViolations);
            throw new ConstraintViolationException(settingsViolations);
        }

        Account account = this.getVerifiedAccountByAccountDepositDto(accountDepositInfo.getAccountGUID(), accountDepositInfo.getExternalAddress());
        AccountTransaction accountTransaction = this.composeVerifiedDepositTransaction(account, accountDepositInfo.getAmount());

        try {
            AccountTransaction savedAccountTransaction = financialUnitService.saveTransaction(accountTransaction);
            if (isNull(savedAccountTransaction)) {
                throw new CustomUnexpectedException("Saved transaction returned NULL from DB");
            }
        } catch (Exception exc) {
            log.error("!!> saving deposit transaction {} from Dto {} cause error with message {}. Request denied",
                    accountTransaction, accountDepositInfo, exc.getMessage());
            throw new CustomUnexpectedException("Saved deposit transaction returned NULL from DB");
            //TODO process error while deposit operation
        }
    }

    /**
     * Returns account info for specified holder
     */
    @Override
    public AccountInfoDto findAccountByHolder(UUID holderGUID, AccountHolderType holderType) {
        log.debug("^ requested findAccountByHolder with holderGUID:'{}' and holder type: '{}'",
                holderGUID, holderType);
        Account account = this.getVerifiedAccountByHolder(holderGUID, holderType);
        if (isNull(account)) {
            log.warn("~ account for holderGUID:'{}' and holder type: '{}' was not found. Create new one!",
                    holderGUID, holderType);
            return this.createAccountForHolder(holderGUID, holderType, null);
        }
        return accountMapper.toDto(account);
    }

    /**
     * Returns account info for specified account guid
     */
    @Override
    public AccountInfoDto findAccountByGUID(String GUID) {
        log.debug("^ requested findAccountByGUID with token GUID:'{}'", GUID);
        return accountMapper.toDto(this.getVerifiedAccountByAccountDepositDto(GUID, null));
    }

    /**
     * Returns created account info for specified holder
     */
    @Override
    public AccountInfoDto createAccountForHolder(UUID holderExternalGUID, AccountHolderType holderType, String holderName) {
        Account account = this.getVerifiedAccountByHolder(holderExternalGUID, holderType);
        if (nonNull(account)) {
            //TODO decide if it's need to throw error
            log.error("^ request for create new Account for holderGUID {} and holderType {} was rejected. Account already existed:'{}'", holderExternalGUID, holderType, account);
            return accountMapper.toDto(account);
        }

        AccountHolder accountHolder = composeVerifiedAccountHolder(holderExternalGUID, holderType, holderName);
        account = financialUnitService.createAccountHolderWithAccount(accountHolder, AccountType.DEPOSIT);
        if (isNull(account)) {
            log.error("!!> creation of account for holder GUID {} and type {} was interrupted. Check stack trace.", holderExternalGUID, accountHolder);
            throw new FinancialUnitManageException(ExceptionMessages.FINANCE_UNIT_ACCOUNT_CREATION_ERROR,
                    String.format("creation of account for holder GUID '%s' and type '%s' was interrupted", holderExternalGUID, accountHolder));
        }
        return accountMapper.toDto(account);
    }

    /**
     * Returns created transaction info for specified data
     */
    @Override
    public AccountTransactionInfoDto createTransaction(AccountTransactionInfoDto accountTransactionInfoDto) {
        if (isNull(accountTransactionInfoDto)) {
            log.error("!!> requesting createTransactionFromSourceToTargetHolder for NULL accountTransactionInfoDto. Check evoking clients");
            return null;
        }
        Set<ConstraintViolation<AccountTransactionInfoDto>> violations = validator.validate(accountTransactionInfoDto);
        if (!violations.isEmpty()) {
            log.error("!!> requesting createTransactionFromSourceToTargetHolder for accountTransactionInfoDto:{} with ConstraintViolations {}. Check evoking clients",
                    accountTransactionInfoDto, violations);
            return null;
        }
        Account sourceAccount = this.getVerifyAccountByDto(accountTransactionInfoDto.getSourceAccount());
        Account targetAccount = this.getVerifyAccountByDto(accountTransactionInfoDto.getTargetAccount());

        AccountTransaction accountTransaction = this.composeVerifiedTransferTransaction(sourceAccount, targetAccount,
                accountTransactionInfoDto.getAmount(), accountTransactionInfoDto.getTransactionType(),
                accountTransactionInfoDto.getTransactionTemplateType());

        AccountTransaction savedAccountTransaction;
        try {
            savedAccountTransaction = financialUnitService.saveTransaction(accountTransaction);
            if (isNull(savedAccountTransaction)) {
                throw new CustomUnexpectedException("Saved transaction returned NULL from DB");
            }
        } catch (Exception exc) {
            log.error("!!> saving transferring transaction {} from Dto {} cause error with message {}. Request denied",
                    accountTransaction, accountTransactionInfoDto, exc.getMessage());
            throw new CustomUnexpectedException("Saved transferring transaction returned NULL from DB");
            //TODO process error while deposit operation
        }
        return accountTransactionFinUnitMapper.toDto(savedAccountTransaction);
    }

    private AccountHolder composeVerifiedAccountHolder(UUID holderExternalGUID, AccountHolderType holderType, String holderName) {
        if (isNull(holderExternalGUID) || isNull(holderType)) {
            log.error("!!>  requesting getVerifiedAccountByHolder for Blank holderGUID {} or for NULL holderType {} in RestFinancialUnitFacadeImpl. Request denied",
                    holderExternalGUID, holderType);
            return null;
        }
        AccountHolder accountHolder = financialUnitService.getAccountHolderByExternalGUID(holderExternalGUID, holderType);

        if (nonNull(accountHolder)) {
            log.debug("^  requesting createVerifiedAccountHolder with holderGUID {} for already existed holder {}. Returns existed holder", holderExternalGUID, accountHolder);
            return accountHolder;
        }
        return AccountHolder.builder()
                .holderExternalGUID(holderExternalGUID)
                .holderType(holderType)
                .holderName(holderName)
                .build();
    }

    private AccountTransaction composeVerifiedTransferTransaction(Account sourceAccount, Account targetAccount,
                                                                  Double amount, TransactionType type, TransactionTemplateType templateType) {
        if (amount <= 0) {
            log.error("!!> Amount of deposit a account address {} is not match saved in DB external address for user account {}. Request denied",
                    targetAccount.getExternalAddress(), targetAccount.getGUID());
            //TODO process negative deposit operation
        }

        return AccountTransaction.builder()
                .amount(amount)
                .sourceAccount(sourceAccount)
                .targetAccount(targetAccount)
                .transactionType(type)
                .transactionTemplateType(templateType)
                .build();
    }

    private AccountTransaction composeVerifiedDepositTransaction(Account account, Double amount) {
        if (amount <= 0) {
            log.error("!!> Amount of deposit a account address {} is not match saved in DB external address for user account {}. Request denied",
                    account.getExternalAddress(), account.getGUID());
            //TODO process negative deposit operation
        }

        return AccountTransaction.builder()
                .amount(amount)
                .targetAccount(account)
                .transactionType(TransactionType.DEPOSIT)
                .transactionTemplateType(TransactionTemplateType.EXTERNAL_PROVIDER)
                .build();
    }

    private Account getVerifiedAccountByHolder(UUID holderGUID, AccountHolderType holderType) {
        if (isNull(holderGUID) || isNull(holderType)) {
            log.error("!!>  requesting getVerifiedAccountByHolder for Blank holderGUID {} or for NULL holderType {} in RestFinancialUnitFacadeImpl. Request denied",
                    holderGUID, holderType);
            return null;
        }
        Account account = financialUnitService.getAccountByHolderGUIDAndType(holderGUID, holderType);
        //TODO check if it's needed
//        if (isNull(account)) {
//            log.error("!!> Account with requested holderGUID {} was not found. 'getVerifiedAccountByHolder' in RestFinancialUnitFacadeImpl request denied",
//                    holderGUID);
//            throw new FinancialUnitManageException(ExceptionMessages.FINANCE_UNIT_ACCOUNT_NOT_FOUND_ERROR,
//                    "Financial account with requested holder GUID " + holderGUID + " was not found");
//        }
        if (nonNull(account) && !account.getStatus().isActive()) {
            log.error("!!> Specified account with GUID {} for holder GUID {} and Type {} is not active. Request passed, but be aware",
                    account.getGUID(), holderGUID, holderType);
            //TODO process deposit to non-active account
        }
        return account;
    }

    private Account getVerifiedAccountByAccountDepositDto(String accountGUID, String externalAddress) {
        Account account = financialUnitService.getAccountByGUID(UUID.fromString(accountGUID));
        if (isNull(account)) {
            log.error("!!> Account with requested id {} was not found. 'getVerifiedAccount' in RestFinancialUnitFacadeImpl request denied",
                    accountGUID);
            throw new FinancialUnitManageException(ExceptionMessages.FINANCE_UNIT_ACCOUNT_NOT_FOUND_ERROR,
                    "Financial account with requested id " + accountGUID + " was not found");
        }
        if (!isBlank(externalAddress) && !account.getExternalAddress().equals(externalAddress)) {
            log.error("!!> Specified account address {} is not match saved in DB exteral address for user account {}. Request denied",
                    externalAddress, accountGUID);
            throw new ValidationException(ExceptionMessages.FINANCE_UNIT_TOKEN_VALIDATION_ERROR, "account address",
                    "parameter account address is not match saved in DB exteral address for user account in processDeposit");
        }
        if (!account.getStatus().isActive()) {
            log.error("!!> Specified account with address {} and GUID {} is not active. Request passed, but be aware",
                    externalAddress, accountGUID);
            //TODO process deposit to non-active account
        }
        //TODO удалить до 01.06.2021 если не используется
//        if (isNull(account.getAmount())) {
//            log.error("!!> Specified account with address {} and GUID {} has NULL amount. Request denied",
//                    externalAddress, accountGUID);
//            throw new ValidationException(ExceptionMessages.FINANCE_UNIT_TOKEN_VALIDATION_ERROR, "account amount",
//                    "Parameter account amount is NULL. Request denied.");
//        }
        return account;
    }

    private Account getVerifyAccountByDto(AccountInfoDto accountInfo) {
        if (isNull(accountInfo)) {
            log.error("!!> requesting getVerifyAccountByDto for NULL accountInfo. Check evoking clients");
            return null;
        }
        Set<ConstraintViolation<AccountInfoDto>> violations = validator.validate(accountInfo);
        if (!violations.isEmpty()) {
            log.error("!!> requesting verifyAccountInfoDto for accountInfo:{} with ConstraintViolations {}. Check evoking clients",
                    accountInfo, violations);
            return null;
        }
        Account account = null;
        if (nonNull(accountInfo.getGUID())) {
            account = this.getVerifiedAccountByAccountDepositDto(accountInfo.getGUID(), null);
        } else if (nonNull(accountInfo.getOwnerGUID()) && nonNull(accountInfo.getOwnerType())) {
            account = this.getVerifiedAccountByHolder(UUID.fromString(accountInfo.getOwnerGUID()), accountInfo.getOwnerType());
        }
        return account;
    }
}

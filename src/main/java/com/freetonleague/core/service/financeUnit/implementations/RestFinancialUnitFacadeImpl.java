package com.freetonleague.core.service.financeUnit.implementations;

import com.freetonleague.core.domain.dto.AccountDepositFinUnitDto;
import com.freetonleague.core.domain.dto.AccountInfoDto;
import com.freetonleague.core.domain.dto.AccountTransactionInfoDto;
import com.freetonleague.core.domain.enums.*;
import com.freetonleague.core.domain.model.Account;
import com.freetonleague.core.domain.model.AccountHolder;
import com.freetonleague.core.domain.model.AccountTransaction;
import com.freetonleague.core.domain.model.User;
import com.freetonleague.core.exception.CustomUnexpectedException;
import com.freetonleague.core.exception.FinancialUnitManageException;
import com.freetonleague.core.exception.ValidationException;
import com.freetonleague.core.exception.config.ExceptionMessages;
import com.freetonleague.core.mapper.AccountFinUnitMapper;
import com.freetonleague.core.mapper.AccountTransactionFinUnitMapper;
import com.freetonleague.core.service.RestUserFacade;
import com.freetonleague.core.service.financeUnit.FinancialUnitService;
import com.freetonleague.core.service.financeUnit.RestFinancialUnitFacade;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import javax.validation.Validator;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
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

    private final RestUserFacade restUserFacade;

    /**
     * Process deposit transfer request to user account
     */
    @Override
    public void processDeposit(String token, AccountDepositFinUnitDto accountDepositInfo, BankProviderType providerType) {
        log.info("New deposit request with token '{}' from external provider '{}' with data: '{}'",
                token, providerType, accountDepositInfo);

        if (!financialUnitService.validateFinanceTokenForDeposit(token)) {
            log.error("!!> Specified token '{}' is not valid for operate with deposit transactions. Request denied", token);
            throw new ValidationException(ExceptionMessages.FINANCE_UNIT_TOKEN_VALIDATION_ERROR, "token",
                    "parameter token is not valid for processDeposit");
        }
        Set<ConstraintViolation<AccountDepositFinUnitDto>> settingsViolations = validator.validate(accountDepositInfo);
        if (!settingsViolations.isEmpty()) {
            log.debug("^ transmitted account deposit info dto: '{}' have constraint violations: '{}'",
                    accountDepositInfo, settingsViolations);
            throw new ConstraintViolationException(settingsViolations);
        }

        Account account = this.getVerifiedAccountByGUID(accountDepositInfo.getAccountGUID(), accountDepositInfo.getExternalAddress());
        AccountTransaction accountTransaction = this.composeVerifiedDepositTransaction(account, accountDepositInfo.getAmount());

        try {
            AccountTransaction savedAccountTransaction = financialUnitService.createTransaction(accountTransaction);
            if (isNull(savedAccountTransaction)) {
                throw new CustomUnexpectedException("Saved transaction returned NULL from DB");
            }
        } catch (Exception exc) {
            log.error("!!> saving deposit transaction '{}' from Dto '{}' cause error with message '{}'. Request denied",
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
        log.debug("^ requested findAccountByGUID by GUID:'{}'", GUID);
        return accountMapper.toDto(this.getVerifiedAccountByGUID(GUID, null));
    }

    /**
     * Returns account info for specified account guid
     */
    @Override
    public AccountInfoDto findAccountByExternalAddress(String externalAddress) {
        log.debug("^ requested findAccountByExternalAddress with externalAddress:'{}'", externalAddress);
        Account account = this.getVerifiedAccountByExternalAddress(externalAddress);
        if (isNull(account)) {
            log.warn("~ account for externalAddress:'{}' was not found. Create new one!", externalAddress);
            return this.createNotTrackingAccountByExternalAddress(externalAddress);
        }
        return accountMapper.toDto(account);
    }

    /**
     * Returns created account info for specified holder
     */
    @Override
    public AccountInfoDto createAccountForHolder(UUID holderExternalGUID, AccountHolderType holderType, String holderName) {
        Account account = this.getVerifiedAccountByHolder(holderExternalGUID, holderType);
        if (nonNull(account)) {
            //TODO decide if it's need to throw error
            log.error("^ request for create new Account for holderGUID '{}' and holderType '{}' was rejected. Account already existed:'{}'", holderExternalGUID, holderType, account);
            return accountMapper.toDto(account);
        }

        AccountHolder accountHolder = composeVerifiedAccountHolder(holderExternalGUID, holderType, holderName);
        account = financialUnitService.createAccountHolderWithAccount(accountHolder, AccountType.DEPOSIT);
        if (isNull(account)) {
            log.error("!!> creation of account for holder GUID '{}' and type '{}' was interrupted. Check stack trace.", holderExternalGUID, accountHolder);
            throw new FinancialUnitManageException(ExceptionMessages.FINANCE_UNIT_ACCOUNT_CREATION_ERROR,
                    String.format("creation of account for holder GUID '%s' and type '%s' was interrupted", holderExternalGUID, accountHolder));
        }
        return accountMapper.toDto(account);
    }

    /**
     * Returns transaction info for specified guid
     */
    @Override
    public AccountTransactionInfoDto findTransactionByGUID(String transactionGUID) {
        log.debug("^ requested findAccountByGUID by GUID:'{}'", transactionGUID);
        return accountTransactionFinUnitMapper.toDto(financialUnitService.getTransaction(UUID.fromString(transactionGUID)));
    }

    /**
     * Get list of transactions for specified account and filtered by status list. Search in both source and target.
     */
    @Override
    public Page<AccountTransactionInfoDto> findTransactionListByAccountAndStatusList(Pageable pageable, List<AccountTransactionStatusType> statusList, AccountInfoDto accountDto) {
        if (isNull(pageable)) {
            log.error("!!> Can't findTransactionListByAccountAndStatusList for NULL pageable. Request denied");
            return null;
        }
        log.debug("^ requested findTransactionListByAccountAndStatusList for parameters: statusList '{}',  accountDto:'{}'", statusList, accountDto);
        Account filteredAccount = null;
        if (nonNull(accountDto)) {
            filteredAccount = this.getVerifyAccountByDto(accountDto);
        }
        return financialUnitService.findTransactionListByAccountAndStatus(pageable, statusList, filteredAccount)
                .map(accountTransactionFinUnitMapper::toDto);
    }

    /**
     * Returns created transaction info for specified data
     */
    @Override
    public AccountTransactionInfoDto createTransferTransaction(AccountTransactionInfoDto accountTransactionInfoDto) {
        log.debug("^ try to create transfer transaction for data '{}'", accountTransactionInfoDto);
        if (!this.verifyAccountTransactionByDto(accountTransactionInfoDto)) {
            log.error("!!> Can't create transfer transaction for accountTransactionInfoDto with errors. Request denied");
            return null;
        }
        AccountTransaction accountTransaction = this.composeVerifiedTransferTransactionByDto(accountTransactionInfoDto);
        if (isNull(accountTransaction)) {
            log.error("!!> Can't create transfer transaction for accountTransactionInfoDto with errors. Request denied");
            return null;
        }
        AccountTransaction savedAccountTransaction;
        try {
            savedAccountTransaction = financialUnitService.createTransaction(accountTransaction);
            if (isNull(savedAccountTransaction)) {
                log.error("!!> Can't create transfer transaction. Check stack trace. Request denied");
                throw new CustomUnexpectedException("Saved transaction returned NULL from DB");
            }
        } catch (Exception exc) {
            log.error("!!> saving transferring transaction '{}' from Dto '{}' cause error with message '{}'. Request denied",
                    accountTransaction, accountTransactionInfoDto, exc.getMessage());
            throw new CustomUnexpectedException("Saved transferring transaction returned NULL from DB");
        }
        if (isNull(savedAccountTransaction.getCreatedAt())) {
            savedAccountTransaction.setCreatedAt(LocalDateTime.now());
        }
        return accountTransactionFinUnitMapper.toDto(savedAccountTransaction);
    }

    /**
     * Returns modified transaction info for specified data
     */
    @Override
    public AccountTransactionInfoDto editTransferTransaction(AccountTransactionInfoDto accountTransactionInfoDto) {
        log.debug("^ try to modify transfer transaction for data '{}'", accountTransactionInfoDto);
        if (!this.verifyAccountTransactionByDto(accountTransactionInfoDto)) {
            log.error("!!> Can't edit transfer transaction for accountTransactionInfoDto with errors. Request denied");
            return null;
        }
        AccountTransaction savedAccountTransaction = null;
        try {
            AccountTransaction existedTransaction = financialUnitService.getTransaction(
                    UUID.fromString(accountTransactionInfoDto.getGUID()));
            // find suitable method for modifying transaction
            switch (accountTransactionInfoDto.getStatus()) {
                case ABORTED:
                    savedAccountTransaction = financialUnitService.abortTransaction(existedTransaction);
                    break;
                default:
                    // change only status of existed Transaction
                    existedTransaction.setStatus(accountTransactionInfoDto.getStatus());
                    if (nonNull(accountTransactionInfoDto.getApprovedBy())) {
                        User approvedByUser = restUserFacade.getVerifiedUserByLeagueId(accountTransactionInfoDto.getApprovedBy().getLeagueId().toString());
                        existedTransaction.setApprovedBy(approvedByUser);
                    }
                    savedAccountTransaction = financialUnitService.editTransaction(existedTransaction);
            }
            if (isNull(savedAccountTransaction)) {
                log.error("!!> Can't edit transfer transaction. Check stack trace. Request denied");
                throw new CustomUnexpectedException("Modify transaction returned NULL from DB");
            }
        } catch (Exception exc) {
            log.error("!!> modifying transferring transaction '{}' from Dto '{}' cause error with message '{}'. Request denied",
                    savedAccountTransaction, accountTransactionInfoDto, exc.getMessage());
            throw new CustomUnexpectedException("Save transferring transaction process returned NULL from DB");
        }
        return accountTransactionFinUnitMapper.toDto(savedAccountTransaction);
    }

    /**
     * Returns aborted transaction info for specified data
     */
    @Override
    public AccountTransactionInfoDto abortTransaction(String transactionGUID) {
        log.debug("^ try to abort transfer transaction for transactionGUID '{}'", transactionGUID);
        if (isBlank(transactionGUID)) {
            log.error("!!> Can't abort transfer transaction for BLANK transactionGUID. Request denied");
            throw new FinancialUnitManageException(ExceptionMessages.FINANCE_UNIT_TRANSACTION_ABORT_ERROR,
                    "can't abort transfer transaction for BLANK transactionGUID. Request denied");
        }
        AccountTransaction existedTransaction = this.getVerifiedAccountTransactionByGUID(transactionGUID);
        if (isNull(Objects.requireNonNull(existedTransaction).getStatus()) || existedTransaction.getStatus().isAborted()) {
            log.warn("~ Transfer transaction is already aborted. Request denied");
            throw new FinancialUnitManageException(ExceptionMessages.FINANCE_UNIT_TRANSACTION_ABORT_ERROR,
                    "Transfer transaction is already aborted. Request denied");
        }
        AccountTransaction savedTransaction = financialUnitService.abortTransaction(existedTransaction);
        if (isNull(savedTransaction)) {
            log.error("!!> Can't abort transaction. Check stack trace. Request denied");
            throw new CustomUnexpectedException("Aborted transaction process returned NULL from DB");
        }
        return accountTransactionFinUnitMapper.toDto(savedTransaction);
    }

    /**
     * Returns sign if specified holder made deposit transaction at least once
     */
    @Override
    public boolean isHolderMadeDeposit(UUID holderExternalGUID, AccountHolderType holderType) {
        log.debug("^ requested isHolderMadeDeposit with holderGUID:'{}' and holder type: '{}'",
                holderExternalGUID, holderType);
        Account account = this.getVerifiedAccountByHolder(holderExternalGUID, holderType);
        if (isNull(account)) {
            log.warn("~ returning isHolderMadeDeposit as FALSE: account for holderGUID:'{}' and holder type: '{}' was not found.",
                    holderExternalGUID, holderType);
            return false;
        }
        return financialUnitService.isHolderMadeDeposit(account);
    }

    /**
     * Returns created account info for specified holder
     */
    private AccountInfoDto createNotTrackingAccountByExternalAddress(String externalAddress) {
        if (isBlank(externalAddress)) {
            log.error("!!> requesting createNotTrackingAccountByExternalAddress for Blank externalAddress");
            return null;
        }
        Account account = Account.builder()
                .amount(0.0)
                .holder(null)
                .isNotTracking(true)
                .externalAddress(externalAddress)
                .accountType(AccountType.DEPOSIT)
                .status(AccountStatusType.NOT_TRACKING)
                .externalBankType(BankProviderType.UNKNOWN)
                .build();
        account.generateGUID();
        Account savedAccount = financialUnitService.createNotTrackingAccount(account);
        if (isNull(savedAccount)) {
            log.error("!!> creation of account for external address '{}' was interrupted. Check stack trace.", externalAddress);
            throw new FinancialUnitManageException(ExceptionMessages.FINANCE_UNIT_ACCOUNT_CREATION_ERROR,
                    String.format("creation of account for external address '%s' was interrupted", externalAddress));
        }
        return accountMapper.toDto(account);
    }

    private AccountHolder composeVerifiedAccountHolder(UUID holderExternalGUID, AccountHolderType holderType, String holderName) {
        if (isNull(holderExternalGUID) || isNull(holderType)) {
            log.error("!!>  requesting getVerifiedAccountByHolder for Blank holderGUID '{}' or for NULL holderType '{}' in RestFinancialUnitFacadeImpl. Request denied",
                    holderExternalGUID, holderType);
            return null;
        }
        AccountHolder accountHolder = financialUnitService.getAccountHolderByExternalGUID(holderExternalGUID, holderType);

        if (nonNull(accountHolder)) {
            log.debug("^  requesting createVerifiedAccountHolder with holderGUID '{}' for already existed holder '{}'. Returns existed holder", holderExternalGUID, accountHolder);
            return accountHolder;
        } else {
            accountHolder = AccountHolder.builder()
                    .holderExternalGUID(holderExternalGUID)
                    .holderType(holderType)
                    .holderName(holderName)
                    .build();
            accountHolder.generateGUID();
        }
        return accountHolder;
    }

    private AccountTransaction composeVerifiedTransferTransactionByDto(AccountTransactionInfoDto accountTransactionInfoDto) {
        if (!this.verifyAccountTransactionByDto(accountTransactionInfoDto)) {
            log.error("!!> Can't compose verified transfer transaction for accountTransactionInfoDto with errors. Request denied");
            return null;
        }
        if (accountTransactionInfoDto.getAmount() <= 0) {
            log.error("!!> Amount of transfer transaction from account.GUID '{}' to target account.ExtAddress '{}'  is negative. Request denied",
                    accountTransactionInfoDto.getSourceAccount().getGUID(), accountTransactionInfoDto.getTargetAccount().getExternalAddress());
            throw new FinancialUnitManageException(ExceptionMessages.FINANCE_UNIT_TRANSACTION_CREATION_ERROR,
                    "Amount of transfer transaction is negative.");
        }
        // compose account transfer transaction
        AccountTransaction accountTransaction = accountTransactionFinUnitMapper.fromDto(accountTransactionInfoDto);
        accountTransaction.setSourceAccount(this.getVerifyAccountByDto(accountTransactionInfoDto.getSourceAccount()));
        accountTransaction.setTargetAccount(this.getVerifyAccountByDto(accountTransactionInfoDto.getTargetAccount()));
        accountTransaction.generateGUID();
        return accountTransaction;
    }

    private AccountTransaction composeVerifiedDepositTransaction(Account account, Double amount) {
        if (amount <= 0) {
            log.error("!!> Amount of deposit transaction to account.extAddress '{}' , account.GUID '{}' is negative. Request was passed, but be aware",
                    account.getExternalAddress(), account.getGUID());
            //TODO process negative deposit operation
        }
        AccountTransaction accountTransaction = AccountTransaction.builder()
                .amount(amount)
                .targetAccount(account)
                .transactionType(TransactionType.DEPOSIT)
                .transactionTemplateType(TransactionTemplateType.EXTERNAL_PROVIDER)
                .status(AccountTransactionStatusType.FINISHED)
                .build();
        accountTransaction.generateGUID();
        return accountTransaction;
    }

    private AccountTransaction getVerifiedAccountTransactionByGUID(String transactionGUID) {
        if (isBlank(transactionGUID)) {
            log.error("!> requesting getVerifiedAccountByGUID for Blank transactionGUID. Check evoking clients");
            return null;
        }
        log.debug("^ trying to get transaction by GUID '{}'", transactionGUID);
        AccountTransaction accountTransaction = financialUnitService.getTransaction(UUID.fromString(transactionGUID));
        if (isNull(accountTransaction)) {
            log.error("!!> Transaction with requested id '{}' was not found. 'getVerifiedAccountTransactionByGUID' in RestFinancialUnitFacadeImpl request denied",
                    transactionGUID);
            throw new FinancialUnitManageException(ExceptionMessages.TRANSACTION_NOT_FOUND_ERROR,
                    "Financial transaction with requested id " + transactionGUID + " was not found");
        }
        return accountTransaction;
    }

    private boolean verifyAccountTransactionByDto(AccountTransactionInfoDto accountTransactionInfoDto) {
        if (isNull(accountTransactionInfoDto)) {
            log.error("!!> requesting modifying transaction with verifyAccountTransactionByDto for NULL accountTransactionInfoDto. Check evoking clients");
            return false;
        }
        Set<ConstraintViolation<AccountTransactionInfoDto>> violations = validator.validate(accountTransactionInfoDto);
        if (!violations.isEmpty()) {
            log.error("!!> requesting modifying transaction with verifyAccountTransactionByDto for accountTransactionInfoDto:'{}' with ConstraintViolations '{}'. Check evoking clients",
                    accountTransactionInfoDto, violations);
            return false;
        }
        return true;
    }

    private Account getVerifiedAccountByHolder(UUID holderExternalGUID, AccountHolderType holderType) {
        if (isNull(holderExternalGUID) || isNull(holderType)) {
            log.error("!!>  requesting getVerifiedAccountByHolder for Blank holderGUID '{}' or for NULL holderType '{}' in RestFinancialUnitFacadeImpl. Request denied",
                    holderExternalGUID, holderType);
            return null;
        }
        Account account = financialUnitService.getAccountByHolderExternalGUIDAndType(holderExternalGUID, holderType);
        //TODO check if it's needed
//        if (isNull(account)) {
//            log.error("!!> Account with requested holderGUID '{}' was not found. 'getVerifiedAccountByHolder' in RestFinancialUnitFacadeImpl request denied",
//                    holderGUID);
//            throw new FinancialUnitManageException(ExceptionMessages.FINANCE_UNIT_ACCOUNT_NOT_FOUND_ERROR,
//                    "Financial account with requested holder GUID " + holderGUID + " was not found");
//        }
        if (nonNull(account) && !account.getStatus().isActive()) {
            log.error("!!> Specified account with GUID '{}' for holder GUID '{}' and Type '{}' is not active. Request passed, but be aware",
                    account.getGUID(), holderExternalGUID, holderType);
            //TODO process deposit to non-active account
        }
        return account;
    }

    private Account getVerifiedAccountByExternalAddress(String externalAddress) {
        if (isBlank(externalAddress)) {
            log.error("!!> requesting getVerifiedAccountByExternalAddress for Blank externalAddress");
            return null;
        }
        Account account = financialUnitService.getAccountByExternalAddress(externalAddress);
        if (nonNull(account) && !account.getStatus().isActive()) {
            log.error("!!> For specified externalAddress '{}' was found non-active Account with GUID '{}'. Request passed, but be aware",
                    externalAddress, account.getGUID());
            //TODO process deposit to non-active account
        }
        return account;
    }

    private Account getVerifiedAccountByGUID(String accountGUID, String externalAddress) {
        Account account = financialUnitService.getAccountByGUID(UUID.fromString(accountGUID));
        if (isNull(account)) {
            log.error("!!> Account with requested id '{}' was not found. 'getVerifiedAccount' in RestFinancialUnitFacadeImpl request denied",
                    accountGUID);
            throw new FinancialUnitManageException(ExceptionMessages.FINANCE_UNIT_ACCOUNT_NOT_FOUND_ERROR,
                    "Financial account with requested id " + accountGUID + " was not found");
        }
        // TODO temporary off validating of extAddress
        //  Есть формат в HEX который как ты прислал. А есть в base64. То есть один адрес кодироваться в тоне может по разному
//        if (!isBlank(externalAddress) && !account.getExternalAddress().equals(externalAddress)) {
//            log.error("!!> Specified account address '{}' is not match saved in DB exteral address for user account '{}'. Request denied",
//                    externalAddress, accountGUID);
//            throw new ValidationException(ExceptionMessages.FINANCE_UNIT_TOKEN_VALIDATION_ERROR, "account address",
//                    "parameter account address is not match saved in DB exteral address for user account in processDeposit");
//        }
        if (!account.getStatus().isActive()) {
            log.error("!!> Specified account with address '{}' and GUID '{}' is not active. Request passed, but be aware",
                    externalAddress, accountGUID);
            //TODO process deposit to non-active account
        }
        //TODO удалить до 01.06.2021 если не используется
//        if (isNull(account.getAmount())) {
//            log.error("!!> Specified account with address '{}' and GUID '{}' has NULL amount. Request denied",
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
            log.error("!!> requesting verifyAccountInfoDto for accountInfo:'{}' with ConstraintViolations '{}'. Check evoking clients",
                    accountInfo, violations);
            return null;
        }
        Account account = null;
        if (nonNull(accountInfo.getGUID())) {
            account = this.getVerifiedAccountByGUID(accountInfo.getGUID(), null);
        } else if (nonNull(accountInfo.getOwnerGUID()) && nonNull(accountInfo.getOwnerType())) {
            account = this.getVerifiedAccountByHolder(UUID.fromString(accountInfo.getOwnerGUID()), accountInfo.getOwnerType());
        }
        return account;
    }
}

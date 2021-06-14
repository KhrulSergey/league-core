package com.freetonleague.core.service.financeUnit.implementations;

import com.freetonleague.core.domain.dto.AccountExternalInfoDto;
import com.freetonleague.core.domain.dto.AccountTransactionExternalInfoDto;
import com.freetonleague.core.domain.enums.AccountHolderType;
import com.freetonleague.core.domain.enums.AccountTransactionStatusType;
import com.freetonleague.core.domain.enums.AccountType;
import com.freetonleague.core.domain.enums.TransactionType;
import com.freetonleague.core.domain.model.Account;
import com.freetonleague.core.domain.model.AccountHolder;
import com.freetonleague.core.domain.model.AccountTransaction;
import com.freetonleague.core.exception.ExceptionMessages;
import com.freetonleague.core.exception.FinancialUnitManageException;
import com.freetonleague.core.repository.AccountHolderRepository;
import com.freetonleague.core.repository.AccountRepository;
import com.freetonleague.core.repository.AccountTransactionRepository;
import com.freetonleague.core.service.NotificationService;
import com.freetonleague.core.service.financeUnit.FinanceEventService;
import com.freetonleague.core.service.financeUnit.FinancialUnitService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Lazy;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

import javax.validation.ConstraintViolation;
import javax.validation.Validator;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;
import static org.apache.commons.lang3.ObjectUtils.isEmpty;
import static org.apache.commons.lang3.StringUtils.isBlank;

/**
 * Service for save transactions and accounts in DB
 */
@Transactional
@Service
@RequiredArgsConstructor
@Slf4j
public class FinancialUnitServiceImpl implements FinancialUnitService {

    private final BankAccountingClientService bankAccountingClientService;// call broxus service
    private final AccountRepository accountsRepository;
    private final AccountHolderRepository accountHolderRepository;
    private final AccountTransactionRepository accountTransactionRepository;
    private final Validator validator;

    @Lazy
    @Autowired
    private FinanceEventService financeEventService;


    @Value("${freetonleague.service.league-finance.service-token:Pu6ThMMkF4GFTL5Vn6F45PHSaC193232HGdsQ}")
    private String leagueFinanceServiceToken;

    @Value("${freetonleague.service.league-finance.balance-update-timeout-in-sec:600}")
    private Long leagueFinanceBalanceUpdateTimeout;

    /**
     * User login with system bonus account
     */
    @Value("${freetonleague.service.league-finance.system-bonus-user-login:#{null}}")
    private String systemBonusUserLogin;

    @Value("${freetonleague.service.league-finance.auto-abort-transaction:false}")
    private Boolean autoAbortTransaction;

    //region Accounts

    /**
     * Get account by GUID.
     */
    @Override
    public Account getAccountByGUID(UUID GUID) {
        if (isNull(GUID)) {
            log.error("!> requesting getAccountByGUID for null GUID. Check evoking clients");
            return null;
        }
        log.debug("^ trying to get account by GUID: '{}'", GUID);
        return accountsRepository.findByGUID(GUID);
    }

    /**
     * Get account by holder.
     */
    @Override
    public Account getAccountByHolder(AccountHolder holder) {
        if (isNull(holder)) {
            log.error("!!>  requesting getAccountByHolder for NULL holder. Check evoking clients");
            return null;
        }
        //TODO handle update account balance by timeout
//        if(isEmpty(account)){
//            log.debug("^ there is no accounts for holder '{}'. Returns null", holder);
//            return null;
//        }
//        if(account.getExternalBankLastUpdate().plusSeconds(leagueFinanceBalanceUpdateTimeout).isBefore(LocalDateTime.now())){
//            account = this.updateAccountBalanceFromExternalBank(account);
//        }
        return accountsRepository.findByHolder(holder);
    }

    /**
     * Get account by external GUID and Holder type.
     */
    @Override
    public Account getAccountByHolderExternalGUIDAndType(UUID externalHolderGUID, AccountHolderType holderType) {
        if (isNull(externalHolderGUID) || isNull(holderType)) {
            log.error("!!>  requesting getAccountByHolderGUIDAndType for NULL holderGUID '{}' or for NULL holderType '{}'. Check evoking clients",
                    externalHolderGUID, holderType);
            return null;
        }
        AccountHolder accountHolder = this.getAccountHolderByExternalGUID(externalHolderGUID, holderType);
        return this.getAccountByHolder(accountHolder);
    }

    /**
     * Get account by external address.
     */
    @Override
    public Account getAccountByExternalAddress(String externalAddress) {
        if (isBlank(externalAddress)) {
            log.error("!!>  requesting getAccountByExternalAddress for BLANK externalAddress. Check evoking clients");
            return null;
        }
        log.debug("^ trying to get Account by external address:'{}'", externalAddress);
        return accountsRepository.findByExternalAddress(externalAddress);
    }

    /**
     * Get account holder by external GUID and Holder type.
     */
    @Override
    public AccountHolder getAccountHolderByExternalGUID(UUID externalHolderGUID, AccountHolderType holderType) {
        if (isNull(externalHolderGUID) || isNull(holderType)) {
            log.error("!!>  requesting getVerifiedAccountByHolder for NULL holderGUID '{}' or for NULL holderType '{}'. Check evoking clients",
                    externalHolderGUID, holderType);
            return null;
        }
        log.debug("^ trying to get AccountHolder by external GUID:'{}' and holderType: '{}'", externalHolderGUID, holderType);
        return accountHolderRepository.findByHolderExternalGUIDAndHolderType(externalHolderGUID, holderType);
    }

    /**
     * Create new account holder with embedded Account and link to External Bank
     */
    @Override
    public Account createAccountHolderWithAccount(AccountHolder accountHolder, AccountType accountType) {
        if (isNull(accountHolder)) {
            log.error("!!>  requesting createAccountHolderWithAccount for NULL accountHolder. Check evoking clients");
            return null;
        }
        Set<ConstraintViolation<AccountHolder>> violations = validator.validate(accountHolder);
        if (!violations.isEmpty()) {
            log.error("!> requesting createAccountHolderWithAccount for accountHolder '{}' with constraint violations: '{}'. " +
                    "Check evoking clients", accountHolder, violations);
            return null;
        }
        log.debug("^ trying to create account holder external guid '{}' and name '{}', with accountType '{}'",
                accountHolder.getHolderExternalGUID(), accountHolder.getHolderName(), accountType);
        Account account = null;
        try {
            account = this.createAccountForHolder(accountHolder, accountType);
            if (isNull(account)) {
                log.error("!!> requesting createAccountForHolder in createAccountHolderWithAccount " +
                                "returned NULL core-account for holder '{}' and account-type '{}'. Check stack trace",
                        accountHolder, accountType);
                return null;
            }
            accountHolder.setAccount(account);
            log.debug("^ send request to save account holder with embedded Account in DB: '{}'", accountHolder);
            accountHolder = this.saveAccountHolder(accountHolder);
        } catch (Exception exc) {
            log.error("!!> requesting createAccountHolderWithAccount for holder '{}' cause unexpected Error '{}'." +
                    " Check stack trace", accountHolder, exc.getMessage());
        }
        return account;
    }

    /**
     * Save new notTracking account
     */
    @Override
    public Account createNotTrackingAccount(Account account) {
        if (isNull(account)) {
            log.error("!!>  requesting createNotTrackingAccount for NULL account. Check evoking clients");
            return null;
        }
        Set<ConstraintViolation<Account>> violations = validator.validate(account);
        if (!violations.isEmpty()) {
            log.error("!> requesting createNotTrackingAccount for account '{}' with constraint violations: '{}'. " +
                    "Check evoking clients", account, violations);
            return null;
        }
        log.debug("^ trying to create not tracking account '{}'", account);
        return accountsRepository.save(account);
    }

    /**
     * Get transaction by GUID.
     */
    @Override
    public AccountTransaction getTransaction(UUID GUID) {
        if (isNull(GUID)) {
            log.error("!!> trying to get transaction for NULL GUID. Check evoking clients");
            return null;
        }
        log.debug("^ trying to find transaction with guid '{}'", GUID);
        return accountTransactionRepository.findByGUID(GUID);
    }
    //endregion

    //region Transactions

    /**
     * Get list of transactions for specified account and status list. Search in both source and target.
     */
    @Override
    public Page<AccountTransaction> findTransactionListByAccountAndStatus(Pageable pageable, List<AccountTransactionStatusType> statusList, Account account) {
        if (isEmpty(statusList)) {
            statusList = AccountTransactionStatusType.valueList;
        }
        if (nonNull(account)) {
            return accountTransactionRepository.findAllByAccount(pageable, account, statusList);
        }
        return accountTransactionRepository.findAllByStatusIn(pageable, statusList);
    }

    /**
     * Save new transaction and update Accounts: source (if specified) and target
     */
    @Transactional(isolation = Isolation.SERIALIZABLE)
    @Override
    public AccountTransaction createTransaction(AccountTransaction transaction) {
        if (!this.verifyTransaction(transaction)) {
            return null;
        }
        Account sourceAccount = transaction.getSourceAccount();
        // check if source account is defined BUT there is no fund of transaction amount
        if (TransactionType.withdrawTransactionTypeList.contains(transaction.getTransactionType())) {
            if (isNull(sourceAccount)) {
                log.error("!!> requesting saveTransaction for 'withdraw' transaction '{}' with NULL source account. Request denied. Check evoking clients",
                        transaction);
                throw new FinancialUnitManageException(ExceptionMessages.FINANCE_UNIT_TRANSACTION_CREATION_ERROR,
                        "Requesting 'withdraw' transaction for NULL source financial account. Request denied.");
            }
            // check if source account doesn't have fund of transaction amount
            if (!this.isAccountHaveFundAmount(sourceAccount, transaction.getAmount())) {
                log.error("!!> requesting saveTransaction method for 'withdraw' transaction '{}' for source account with not enough fund. " +
                        "Request denied. Check evoking clients", transaction);
                throw new FinancialUnitManageException(ExceptionMessages.FINANCE_UNIT_TRANSACTION_CREATION_ERROR,
                        "Specified source financial account doesn't have enough fund to execute transfer");
            }
        }

        transaction.generateGUID();
        if (nonNull(sourceAccount)) {
            log.debug("^ trying to change (lower) balance of source account with GUID '{}' from transaction GUID: '{}'", sourceAccount.getGUID(), transaction.getGUID());
            double sourceBalance = sourceAccount.getAmount();
            sourceAccount.setAmount(sourceBalance - transaction.getAmount());
            sourceAccount = this.editAccount(sourceAccount);
            if (isNull(sourceAccount)) {
                log.error("!!> requesting edit source Account in saveTransaction '{}' cause error while saving in DB. Request denied. Check evoking clients",
                        transaction);
                throw new FinancialUnitManageException(ExceptionMessages.FINANCE_UNIT_TRANSACTION_CREATION_ERROR,
                        "Specified source financial account can't be modified in DB. ");
            }
        }

        Account targetAccount = transaction.getTargetAccount();
        log.debug("^ trying to change balance of target account with GUID '{}' from transaction GUID: '{}'", targetAccount.getGUID(), transaction.getGUID());
        double targetBalance = targetAccount.getAmount();
        targetAccount.setAmount(targetBalance + transaction.getAmount());
        targetAccount = this.editAccount(targetAccount);
        if (isNull(targetAccount)) {
            log.error("!!> requesting edit target Account in saveTransaction '{}' cause error while saving in DB. Request denied. Check evoking clients",
                    transaction);
            throw new FinancialUnitManageException(ExceptionMessages.FINANCE_UNIT_TRANSACTION_CREATION_ERROR,
                    "Specified target financial account can't be modified in DB. ");
        }

        log.debug("^ trying to save new transaction '{}'  in DB: '{}'", transaction.getGUID(), transaction);
        if (transaction.getStatus().isFinished()) {
            log.debug("^ transaction.GUID was finished '{}'", transaction.getGUID());
            transaction.setFinishedAt(LocalDateTime.now());
        }
        AccountTransaction savedTransaction = accountTransactionRepository.save(transaction);
        log.debug("^ transaction is saved?:'{}' in DB with data: '{}'", true, savedTransaction);
        // send event about saved transaction
        financeEventService.processTransactionStatusChange(savedTransaction, savedTransaction.getStatus());
        return savedTransaction;
    }

    /**
     * Update transaction data
     */
    @Transactional(isolation = Isolation.SERIALIZABLE)
    @Override
    public AccountTransaction editTransaction(AccountTransaction transaction) {
        if (!this.verifyTransaction(transaction)) {
            return null;
        }
        if (!this.isExistsTransactionByGUID(transaction.getGUID())) {
            log.error("!!> requesting modify transaction '{}' for non-existed transaction. Check evoking clients", transaction.getGUID());
            return null;
        }
        if (transaction.getPrevStatus().isAborted()) {
            log.error("!!> requesting modify for already aborted transaction '{}'. Request denied. Check evoking clients",
                    transaction);
            throw new FinancialUnitManageException(ExceptionMessages.FINANCE_UNIT_TRANSACTION_MODIFY_ABORTED_ERROR,
                    "Specified transaction was already aborted and can't be modified in DB. ");
        }
        if (transaction.getPrevStatus().isFinished()) {
            log.error("!!> requesting modify already finished transaction '{}' in saveTransaction. Finished transaction can be only aborted. " +
                    "Request denied. Check evoking clients", transaction);
            throw new FinancialUnitManageException(ExceptionMessages.FINANCE_UNIT_TRANSACTION_MODIFY_FINISHED_ERROR,
                    "Specified financial account transaction can't be modified in DB. ");
        }
        if (transaction.getStatus().isAborted()) {
            log.error("!!> requesting abort transaction with data '{}' in editTransaction. Call specific abortTransaction method to abort. " +
                    "Request denied. Check evoking clients", transaction);
            throw new FinancialUnitManageException(ExceptionMessages.FINANCE_UNIT_TRANSACTION_MODIFY_ABORT_ERROR,
                    "Specified financial account transaction can't be aborted with modifying method. ");
        }
        log.debug("^ trying to modify transaction '{}'", transaction);
        if (transaction.getStatus().isFinished()) {
            log.debug("^ transaction.GUID was finished '{}'", transaction.getGUID());
            transaction.setFinishedAt(LocalDateTime.now());
            if (transaction.getTransactionType().isWithdraw()) {
                //save data to Broxus-cloud-client
                AccountTransactionExternalInfoDto transactionExternalInfoDto =
                        bankAccountingClientService.registerBankTransferTransaction(transaction);
                if (isNull(transactionExternalInfoDto) && this.autoAbortTransaction) {
                    log.error("!!> transaction.GUID '{}' was not registered in Broxus-client. Transaction will be aborted.", transaction.getGUID());
                    return this.abortTransaction(transaction);
                }
            }
        }

        if (transaction.isStatusChanged()) {
            this.handleTransactionStatusChanged(transaction);
        }
        transaction = accountTransactionRepository.save(transaction);
        log.debug("^ successfully modified transaction '{}'", transaction.getGUID());
        return transaction;
    }

    /**
     * Abort transaction and update Accounts: source (if specified) and target
     */
    @Transactional(isolation = Isolation.SERIALIZABLE)
    @Override
    public AccountTransaction abortTransaction(AccountTransaction transaction) {
        if (!this.verifyTransaction(transaction)) {
            return null;
        }
        if (!this.isExistsTransactionByGUID(transaction.getGUID())) {
            log.error("!> requesting abort transaction '{}' for non-existed transaction. Check evoking clients", transaction.getGUID());
            return null;
        }
        if (this.isAbortedTransactionByGUID(transaction.getGUID())) {
            log.error("!> requesting abort transaction '{}' for already aborted transaction. Check evoking clients", transaction.getGUID());
            return null;
        }
        log.debug("^ trying to abort transaction '{}'", transaction);

        Account targetAccount = transaction.getTargetAccount();
        log.debug("^ trying to abort (lower) balance of target account with GUID '{}' from transaction GUID: '{}'",
                targetAccount.getGUID(), transaction.getGUID());
        double targetBalance = targetAccount.getAmount();
        targetAccount.setAmount(targetBalance - transaction.getAmount());
        targetAccount = this.editAccount(targetAccount);
        if (isNull(targetAccount)) {
            log.error("!!> requesting abort target Account in abortTransaction '{}' cause error while saving in DB. Request denied. Check evoking clients",
                    transaction);
            throw new FinancialUnitManageException(ExceptionMessages.FINANCE_UNIT_TRANSACTION_CREATION_ERROR,
                    "Specified target financial account can't be modified in DB. ");
        }

        Account sourceAccount = transaction.getSourceAccount();
        if (nonNull(sourceAccount)) {
            log.debug("^ trying to abort (return) balance of source account with GUID '{}' from transaction GUID: '{}'",
                    sourceAccount.getGUID(), transaction.getGUID());
            double sourceBalance = sourceAccount.getAmount();
            sourceAccount.setAmount(sourceBalance + transaction.getAmount());
            sourceAccount = this.editAccount(sourceAccount);
            if (isNull(sourceAccount)) {
                log.error("!!> requesting edit source Account in saveTransaction '{}' cause error while saving in DB. Request denied. Check evoking clients",
                        transaction);
                throw new FinancialUnitManageException(ExceptionMessages.FINANCE_UNIT_TRANSACTION_CREATION_ERROR,
                        "Specified source financial account can't be modified in DB. ");
            }
        }
        transaction.setAbortedAt(LocalDateTime.now());
        transaction.setStatus(AccountTransactionStatusType.ABORTED);

        this.handleTransactionStatusChanged(transaction);
        transaction = accountTransactionRepository.save(transaction);
        log.debug("^ successfully abort transaction '{}'", transaction.getGUID());
        return transaction;
    }

    /**
     * Validate token for operate with finance unit
     */
    @Override
    public boolean validateFinanceTokenForDeposit(String token) {
        return token.equals(this.leagueFinanceServiceToken);
    }

    /**
     * Returns sign of transaction existence for specified GUID.
     */
    @Transactional(isolation = Isolation.SERIALIZABLE)
    @Override
    public boolean isExistsTransactionByGUID(UUID GUID) {
        return accountTransactionRepository.existsByGUID(GUID);
    }

    /**
     * Returns sign of transaction aborted for specified GUID.
     */
    @Transactional(isolation = Isolation.SERIALIZABLE)
    @Override
    public boolean isAbortedTransactionByGUID(UUID GUID) {
        return accountTransactionRepository.isAbortedByGUID(GUID);
    }

    /**
     * Returns sign if with specified account was made deposit transaction at least once
     */
    @Override
    public boolean isHolderMadeDeposit(Account account) {
        if (isNull(account)) {
            log.error("!> requesting isHolderMadeDeposit for NULL account. Check evoking clients");
            return false;
        }
        return accountTransactionRepository.isExistFinishedDepositTransaction(account);
    }

    @Transactional(isolation = Isolation.SERIALIZABLE)
    public boolean isAccountHaveFundAmount(Account account, Double amount) {
        if (isNull(account) || isNull(amount) || amount < 0) {
            log.error("!> requesting isAccountHaveFundAmount for NULL account '{}' or NULL / NEGATIVE amount '{}'. Check evoking clients",
                    account, amount);
            return false;
        }
        log.debug("^ trying to define is account.GUID '{}' have specified fund amount", account.getGUID());
        boolean result = accountsRepository.getAmountForAccount(account) >= amount;
        log.debug("^ account with GUID '{}' have specified fund amount: ", result);
        return result;
    }
    //endregion

    /**
     * Create new core-account with external address from trusted bank-provider
     */
    private Account createAccountForHolder(AccountHolder accountHolder, AccountType accountType) {
        log.debug("^ trying to compose account with type '{}' and create external account for holder: '{}'", accountType, accountHolder);
        Account account = Account.builder()
                .holder(accountHolder)
                .accountType(accountType)
                .build();
        account.generateGUID();
        AccountExternalInfoDto externalAccountInfo = bankAccountingClientService.createExternalBankAddressForAccount(account);
        if (isNull(externalAccountInfo)) {
            //TODO handle errors with data from microservice
            log.error("!!> Error in createAccountForHolder while creating external account (address) in bank provider client-service. " +
                    "Response from Bank provider was not valid. Transmitted core account data: '{}'.", account);
            return null;
        }
        double amount = nonNull(externalAccountInfo.getBalance()) ? externalAccountInfo.getBalance() : 0.0;
        account.setAmount(amount);
        account.setExternalAddress(externalAccountInfo.getExternalBankAddress());
        account.setExternalBankType(externalAccountInfo.getBankType());
        account.setExternalBankLastUpdate(LocalDateTime.now());
        return account;
    }

    /**
     * Save new account holder
     */
    private AccountHolder saveAccountHolder(AccountHolder accountHolder) {
        log.debug("^ trying to add account holder in DB: '{}'", accountHolder);
        return accountHolderRepository.save(accountHolder);
    }

    private Account editAccount(Account account) {
        if (isNull(account)) {
            log.error("!> requesting editAccount for NULL account. Check evoking clients");
            return null;
        }
        Set<ConstraintViolation<Account>> violations = validator.validate(account);
        if (!violations.isEmpty()) {
            log.error("!> requesting editAccount for account '{}' with constraint violations: '{}'. " +
                    "Check evoking clients", account, violations);
            return null;
        }

        log.debug("^ trying to modify account in DB: '{}'", account);
        return accountsRepository.save(account);
    }

    /**
     * Validate transaction parameters to modify
     */
    private boolean verifyTransaction(AccountTransaction transaction) {
        if (isNull(transaction)) {
            log.error("!> requesting modify transaction with verifyTransaction for NULL transaction. Check evoking clients");
            return false;
        }
        Set<ConstraintViolation<AccountTransaction>> violations = validator.validate(transaction);
        if (!violations.isEmpty()) {
            log.error("!> requesting modify transaction '{}' with verifyTransaction for transaction with ConstraintViolations '{}'. Check evoking clients",
                    transaction.getGUID(), violations);
            return false;
        }
        return true;
    }

    /**
     * Prototype for handle transaction status
     */
    private void handleTransactionStatusChanged(AccountTransaction transaction) {
        log.warn("~ status for transaction GUID '{}' was changed from '{}' to '{}' ",
                transaction.getGUID(), transaction.getPrevStatus(), transaction.getStatus());
        transaction.setPrevStatus(transaction.getStatus());
    }

//    /** Update account balance from external provider */
//    private Account updateAccountBalanceFromExternalBank(Account account){
//        if (isNull(account)) {
//            log.error("!!>  requesting updateAccountBalanceFromExternalBank for NULL account. Check evoking clients");
//            return null;
//        }
//        AccountExternalInfoDto externalAccountInfo = bankAccountingClientService.getAccountBalance(account);
//        if (isNull(externalAccountInfo)) {
//            //TODO handle errors with data from microservice
//            log.error("!!> Error in updateAccountBalanceFromExternalBank while updating balance from external account (address) in bank provider client-service. " +
//                    "Response from Bank provider was not valid. Transmitted core account data: '{}'.", account);
//            return null;
//        }
//        double amount = nonNull(externalAccountInfo.getBalance()) ? externalAccountInfo.getBalance() : 0.0;
//        account.setAmount(amount);
//        return accountsRepository.save(account);
//    }
}

package com.freetonleague.core.service.implementations;

import com.freetonleague.core.domain.dto.CouponInfoDto;
import com.freetonleague.core.domain.dto.finance.AccountInfoDto;
import com.freetonleague.core.domain.dto.finance.AccountTransactionInfoDto;
import com.freetonleague.core.domain.enums.finance.AccountHolderType;
import com.freetonleague.core.domain.enums.finance.AccountTransactionStatusType;
import com.freetonleague.core.domain.enums.finance.AccountTransactionTemplateType;
import com.freetonleague.core.domain.enums.finance.AccountTransactionType;
import com.freetonleague.core.domain.model.User;
import com.freetonleague.core.service.FinancialClientService;
import com.freetonleague.core.service.UserService;
import com.freetonleague.core.service.financeUnit.FinancialCouponService;
import com.freetonleague.core.service.financeUnit.RestFinancialUnitFacade;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.validation.ConstraintViolation;
import javax.validation.Validator;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import static java.util.Objects.isNull;
import static org.apache.commons.lang3.StringUtils.isBlank;

//TODO delete and user directly RestFinancialUnitFacade from RestFinanceFacade
/**
 * Service to interact with Financial unit
 * In future it will call feign-client to interact over api
 */
@Transactional
@Service
@RequiredArgsConstructor
@Slf4j
public class FinancialClientServiceImpl implements FinancialClientService {

    //call rest of Financial unit
    private final RestFinancialUnitFacade restFinancialUnitFacade;
    private final FinancialCouponService financialCouponService;
    private final Validator validator;

    @Lazy
    @Autowired
    private final UserService userService;

    /**
     * Returns account info for requested Holder type and GUID from Finance Unit
     */
    @Override
    public AccountInfoDto getAccountByHolderInfo(UUID holderGUID, AccountHolderType holderType) {
        if (isNull(holderGUID) || isNull(holderType)) {
            log.error("!> requesting getAccountByHolderGUID for Blank holderGUID '{}' or for NULL accountHolderType '{}'. Check evoking clients",
                    holderGUID, holderType);
            return null;
        }
        log.debug("^ trying to get account info by holder id: '{}' and type: '{}'", holderGUID, holderType);
        AccountInfoDto accountInfoDto = restFinancialUnitFacade.findAccountByHolder(holderGUID, holderType);
        //TODO delete until 01/02/2022. It updated user main account address
        if (holderType.isUser()) {
            userService.updateUserAccountInfo(holderGUID, accountInfoDto);
        }
        return accountInfoDto;
    }

    /**
     * Returns account info for requested account GUID from Finance Unit
     */
    @Override
    public AccountInfoDto getAccountByGUID(String GUID) {
        if (isBlank(GUID)) {
            log.error("!> requesting getAccountByGUID for Blank GUID. Check evoking clients");
            return null;
        }
        log.debug("^ trying to get account info by GUID: '{}'", GUID);
        return restFinancialUnitFacade.findAccountByGUID(GUID);
    }

    /**
     * Returns account info by requested external address of account from request to Finance Unit
     */
    @Override
    public AccountInfoDto getAccountByExternalAddress(String externalAddress) {
        if (isBlank(externalAddress)) {
            log.error("!> requesting getAccountByExternalAddress for Blank externalAddress. Check evoking clients");
            return null;
        }
        log.debug("^ trying to get account info by externalAddress: '{}'", externalAddress);
        return restFinancialUnitFacade.findAccountByExternalAddress(externalAddress);
    }

    /**
     * Returns new account info by requested Holder type and GUID from request to Finance Unit
     */
    @Override
    public AccountInfoDto createAccountByHolderInfo(UUID holderGUID, AccountHolderType holderType, String holderName) {
        if (isNull(holderGUID) || isNull(holderType)) {
            log.error("!> requesting getAccountByHolderGUID for Blank holderGUID '{}' or for NULL accountHolderType '{}'. Check evoking clients",
                    holderGUID, holderType);
            return null;
        }
        log.debug("^ trying to create account by holder GUID '{}' and type '{}'", holderGUID, holderType);
        return restFinancialUnitFacade.createAccountForHolder(holderGUID, holderType, holderName);
    }

    /**
     * Returns found transaction by specified GUID
     */
    @Override
    public AccountTransactionInfoDto getTransactionByGUID(String transactionGUID) {
        if (isBlank(transactionGUID)) {
            log.error("!> requesting getTransactionByGUID for Blank transactionGUID. Check evoking clients");
            return null;
        }
        log.debug("^ trying to get transaction by GUID '{}' from finance unit", transactionGUID);
        return restFinancialUnitFacade.findTransactionByGUID(transactionGUID);
    }

    /**
     * Returns found transaction history (list) for specified account and/or status list
     */
    @Override
    public Page<AccountTransactionInfoDto> getTransactionsHistory(Pageable pageable, List<AccountTransactionStatusType> statusList, AccountInfoDto accountInfoDto) {
        if (isNull(pageable)) {
            log.error("!> requesting getTransactionsHistory for NULL pageable params. Check evoking clients");
            return null;
        }
        log.debug("^ trying to get transaction history by statusList '{}' and accountInfoDto '{}'", statusList, accountInfoDto);
        Page<AccountTransactionInfoDto> transactionList = restFinancialUnitFacade.findTransactionListByAccountAndStatusList(pageable, statusList, accountInfoDto);
        log.debug("^ successfully find '{}' transactions history by statusList '{}' and accountInfoDto '{}'", transactionList.stream().count(), statusList, accountInfoDto);
        return transactionList;
    }

    /**
     * Returns info for created transfer transaction from source to target account
     */
    @Override
    public AccountTransactionInfoDto applyPurchaseTransaction(AccountTransactionInfoDto accountTransactionInfoDto) {
        if (!verifyWithdrawTransaction(accountTransactionInfoDto)) {
            log.error("!!> requesting applyPurchaseTransaction for accountTransactionInfoDto with Errors. Check evoking clients");
            return null;
        }
        log.debug("^ trying to create new transaction and send request to Finance Unit '{}'", accountTransactionInfoDto);
        return restFinancialUnitFacade.createTransferTransaction(accountTransactionInfoDto);
    }

    /**
     * Returns info for created withdraw transaction from user to target (external) account
     */
    public AccountTransactionInfoDto applyWithdrawTransaction(AccountTransactionInfoDto accountTransactionInfoDto) {
        if (!verifyWithdrawTransaction(accountTransactionInfoDto)) {
            log.error("!!> requesting applyWithdrawTransaction for accountTransactionInfoDto with Errors. Check evoking clients");
            return null;
        }
        if (isNull(accountTransactionInfoDto.getStatus()) || !accountTransactionInfoDto.getStatus().isFrozen()) {
            log.error("!!> requesting applyWithdrawTransaction for error in transaction status '{}'. Check evoking clients",
                    accountTransactionInfoDto.getStatus());
            return null;
        }
        log.debug("^ trying to create new withdraw transaction and send request to Finance Unit '{}'", accountTransactionInfoDto);
        return restFinancialUnitFacade.createTransferTransaction(accountTransactionInfoDto);
    }

    /**
     * Returns updated info for modified withdraw transaction
     */
    @Override
    public AccountTransactionInfoDto editWithdrawTransaction(AccountTransactionInfoDto accountTransactionInfoDto) {
        if (!verifyWithdrawTransaction(accountTransactionInfoDto)) {
            log.error("!!> requesting editWithdrawTransaction for accountTransactionInfoDto with Errors. Check evoking clients");
            return null;
        }
        log.debug("^ trying to modify withdraw transaction and send request to Finance Unit '{}'", accountTransactionInfoDto);
        return restFinancialUnitFacade.editTransferTransaction(accountTransactionInfoDto);
    }

    /**
     * Returns updated info for aborted transaction
     */
    @Override
    public AccountTransactionInfoDto abortTransaction(AccountTransactionInfoDto accountTransactionInfoDto) {
        if (isNull(accountTransactionInfoDto) || isNull(accountTransactionInfoDto.getGUID())) {
            log.error("!!> requesting abortTransaction for accountTransactionInfoDto '{}' with Errors. Check evoking clients",
                    accountTransactionInfoDto);
            return null;
        }
        log.debug("^ trying to abort transaction.GUID and send request to Finance Unit '{}'", accountTransactionInfoDto.getGUID());
        return restFinancialUnitFacade.abortTransaction(accountTransactionInfoDto.getGUID());
    }

    /**
     * Returns sign if specified user made deposit transaction at least once to his account
     */
    @Override
    public boolean isUserMadeDepositToHisAccount(User user) {
        if (isNull(user)) {
            log.error("!!> requesting isUserMadeDepositToHisAccount for NULL user. Check evoking clients");
            return false;
        }
        log.debug("^ trying to check is user.id '{}' made deposit to his account", user.getLeagueId());
        return restFinancialUnitFacade.isHolderMadeDeposit(user.getLeagueId(), AccountHolderType.USER);
    }

    private boolean verifyWithdrawTransaction(AccountTransactionInfoDto accountTransactionInfoDto) {
        if (isNull(accountTransactionInfoDto)) {
            log.error("!!> requesting modify transaction for NULL accountTransactionInfoDto. Check evoking clients");
            return false;
        }
        Set<ConstraintViolation<AccountTransactionInfoDto>> violations = validator.validate(accountTransactionInfoDto);
        if (!violations.isEmpty()) {
            log.error("!!> requesting modify transaction for accountTransactionInfoDto:'{}' with ConstraintViolations '{}'. Check evoking clients",
                    accountTransactionInfoDto, violations);
            return false;
        }
        if (!this.verifyAccountInfoDto(accountTransactionInfoDto.getSourceAccount())) {
            log.error("!!> requesting modify transaction for sourceAccount '{}' with errors. Check evoking clients",
                    accountTransactionInfoDto);
            return false;
        }
        if (!this.verifyAccountInfoDto(accountTransactionInfoDto.getTargetAccount())) {
            log.error("!!> requesting modify transaction for targetAccount '{}' with errors. Check evoking clients",
                    accountTransactionInfoDto);
            return false;
        }
        return true;
    }

    /**
     * Apply coupon by advertisement company hash for user from session
     */
    @Override
    public AccountInfoDto applyCouponForUser(CouponInfoDto couponInfo, User user) {
        //TODO freeze user account
        AccountInfoDto userAccount = this.getAccountByHolderInfo(user.getLeagueId(), AccountHolderType.USER);
        AccountTransactionInfoDto transferTransaction = this.composeCouponPaymentTransaction(
                couponInfo.getCouponAccount(), userAccount, couponInfo.getCouponAmount());
        AccountTransactionInfoDto savedTransaction = this.applyPurchaseTransaction(transferTransaction);
        return savedTransaction.getTargetAccount();
    }

    /**
     * Verify advertisement company by coupon hash
     */
    @Override
    public CouponInfoDto getVerifiedAdvertisementCompany(String couponHash) {
        if (isBlank(couponHash)) {
            log.error("!!> requesting getVerifiedAdvertisementCompany for BLANK couponHash. Check evoking clients");
            return null;
        }
        return financialCouponService.getVerifiedAdvertisementCompany(couponHash);
    }

    /**
     * transfer transaction from bonusAccount to userAccount
     */
    private AccountTransactionInfoDto composeCouponPaymentTransaction(AccountInfoDto accountSourceDto,
                                                                      AccountInfoDto accountTargetDto,
                                                                      double tournamentFundAmount) {
        return AccountTransactionInfoDto.builder()
                .amount(tournamentFundAmount)
                .sourceAccount(accountSourceDto)
                .targetAccount(accountTargetDto)
                .status(AccountTransactionStatusType.FINISHED)
                .transactionType(AccountTransactionType.PAYMENT)
                .transactionTemplateType(AccountTransactionTemplateType.DOCKET_ENTRANCE_FEE)
                .build();
    }

    private boolean verifyAccountInfoDto(AccountInfoDto accountInfo) {
        if (isNull(accountInfo)) {
            log.error("!!> requesting verifyAccountInfoDto for NULL accountInfo. Check evoking clients");
            return false;
        }
        Set<ConstraintViolation<AccountInfoDto>> violations = validator.validate(accountInfo);
        if (!violations.isEmpty()) {
            log.error("!!> requesting verifyAccountInfoDto for accountInfo:'{}' with ConstraintViolations '{}'. Check evoking clients",
                    accountInfo, violations);
            return false;
        }

        if (!accountInfo.getIsNotTracking()) {
            if (isBlank(accountInfo.getOwnerGUID()) || isNull(accountInfo.getOwnerType())) {
                log.error("!!> requesting verifyAccountInfoDto for accountInfo:'{}' with BLANK ownerGUID or NULL ownerType '{}'. " +
                        "Check evoking clients", accountInfo.getOwnerGUID(), accountInfo.getOwnerType());
                return false;
            }
        }
        return true;
    }
}

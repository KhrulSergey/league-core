package com.freetonleague.core.service.implementations;

import com.freetonleague.core.domain.dto.AccountInfoDto;
import com.freetonleague.core.domain.dto.AccountTransactionInfoDto;
import com.freetonleague.core.domain.dto.CouponInfoDto;
import com.freetonleague.core.domain.enums.AccountHolderType;
import com.freetonleague.core.domain.enums.TransactionTemplateType;
import com.freetonleague.core.domain.enums.TransactionType;
import com.freetonleague.core.domain.model.User;
import com.freetonleague.core.service.FinancialClientService;
import com.freetonleague.core.service.financeUnit.FinancialCouponService;
import com.freetonleague.core.service.financeUnit.RestFinancialUnitFacade;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.validation.ConstraintViolation;
import javax.validation.Validator;
import java.util.Set;
import java.util.UUID;

import static java.util.Objects.isNull;
import static org.apache.commons.lang3.StringUtils.isBlank;

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

    /**
     * Returns account info for requested Holder type and GUID from Finance Unit
     */
    @Override
    public AccountInfoDto getAccountByHolderInfo(UUID holderGUID, AccountHolderType holderType) {
        if (isNull(holderGUID) || isNull(holderType)) {
            log.error("!> requesting getAccountByHolderGUID for Blank holderGUID {} or for NULL accountHolderType {}. Check evoking clients",
                    holderGUID, holderType);
            return null;
        }
        log.debug("^ trying to get account info by holder id: {} and type: {}", holderGUID, holderType);
        return restFinancialUnitFacade.findAccountByHolder(holderGUID, holderType);
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
        log.debug("^ trying to get account info by GUID: {}", GUID);
        return restFinancialUnitFacade.findAccountByGUID(GUID);
    }

    /**
     * Returns new account info by requested Holder type and GUID from request to Finance Unit
     */
    @Override
    public AccountInfoDto createAccountByHolderInfo(UUID holderGUID, AccountHolderType holderType, String holderName) {
        if (isNull(holderGUID) || isNull(holderType)) {
            log.error("!> requesting getAccountByHolderGUID for Blank holderGUID {} or for NULL accountHolderType {}. Check evoking clients",
                    holderGUID, holderType);
            return null;
        }
        log.debug("^ trying to create account by holder GUID {} and type {}", holderGUID, holderType);
        return restFinancialUnitFacade.createAccountForHolder(holderGUID, holderType, holderName);
    }

    /**
     * Returns info for created transaction from source to target holder GUID
     */
    @Override
    public AccountTransactionInfoDto applyTransactionFromSourceToTargetHolder(AccountTransactionInfoDto accountTransactionInfoDto) {
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
        if (!this.verifyAccountInfoDto(accountTransactionInfoDto.getSourceAccount())) {
            log.error("!!> requesting createTransactionFromSourceToTargetHolder for sourceAccount {} with errors. Check evoking clients",
                    accountTransactionInfoDto);
            return null;
        }
        if (!this.verifyAccountInfoDto(accountTransactionInfoDto.getTargetAccount())) {
            log.error("!!> requesting createTransactionFromSourceToTargetHolder for targetAccount {} with errors. Check evoking clients",
                    accountTransactionInfoDto);
            return null;
        }

        log.debug("^ trying to create new transaction and send request to Finance Unit {}", accountTransactionInfoDto);
        return restFinancialUnitFacade.createTransaction(accountTransactionInfoDto);
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
        AccountTransactionInfoDto savedTransaction = this.applyTransactionFromSourceToTargetHolder(transferTransaction);
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
                .transactionType(TransactionType.PAYMENT)
                .transactionTemplateType(TransactionTemplateType.DOCKET_ENTRANCE_FEE)
                .build();
    }

    private boolean verifyAccountInfoDto(AccountInfoDto accountInfo) {
        if (isNull(accountInfo)) {
            log.error("!!> requesting verifyAccountInfoDto for NULL accountInfo. Check evoking clients");
            return false;
        }
        Set<ConstraintViolation<AccountInfoDto>> violations = validator.validate(accountInfo);
        if (!violations.isEmpty()) {
            log.error("!!> requesting verifyAccountInfoDto for accountInfo:{} with ConstraintViolations {}. Check evoking clients",
                    accountInfo, violations);
            return false;
        }
        return true;
    }
}

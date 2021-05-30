package com.freetonleague.core.service.financeUnit.implementations;

import com.freetonleague.core.domain.dto.AccountInfoDto;
import com.freetonleague.core.domain.dto.CouponInfoDto;
import com.freetonleague.core.domain.enums.AccountHolderType;
import com.freetonleague.core.domain.model.User;
import com.freetonleague.core.service.UserService;
import com.freetonleague.core.service.financeUnit.FinancialCouponService;
import com.freetonleague.core.service.financeUnit.RestFinancialUnitFacade;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.time.LocalDateTime;

import static java.util.Objects.isNull;
import static org.apache.commons.lang3.StringUtils.isBlank;

/**
 * Service for provide information about coupons and advertisement companies
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class FinancialCouponServiceImpl implements FinancialCouponService {

    private final UserService userService;
    private final RestFinancialUnitFacade restFinancialUnitFacade;

    /**
     * Prefix for system users
     */
    @Value("${freetonleague.system-user-prefix:SYSTEM_}")
    private String systemUserPrefix;

    /**
     * User login with system bonus account
     */
    @Value("${freetonleague.service.league-finance.system-bonus-user-login:#{null}}")
    private String systemBonusUserLogin;

    @Value("${freetonleague.service.league-finance.first-ton-bonus-name:#{null}}")
    private String firstTonBonusName;

    /**
     * Source of funds for coupon/bonus payments
     */
    private AccountInfoDto bonusAccount;

    @PostConstruct
    private void postConstruct() {
        if (isBlank(systemUserPrefix)) {
            log.error("!!> Project property 'freetonleague.system-bonus-user-login' is not set in config! Coupon service will not work.");
        }
        if (isBlank(systemBonusUserLogin)) {
            log.error("!!> Project property 'freetonleague.system-user-prefix' is not set in config! Coupon service will not work.");
        }
        if (isBlank(firstTonBonusName)) {
            log.error("!!> Project property 'freetonleague.first-ton-bonus-name' is not set in config! Coupon service will not work.");
        }
    }

    /**
     * Verify advertisement company by coupon hash
     */
    @Override
    public CouponInfoDto getVerifiedAdvertisementCompany(String couponHash) {
        if (couponHash.equals(firstTonBonusName)) {
            return this.getFirstTonBonusInfo();
        }
        return null;
    }

    private CouponInfoDto getFirstTonBonusInfo() {
        return CouponInfoDto.builder()
                .couponAccount(this.getBonusAccount())
                .couponAmount(1.0)
                .expirationDate(LocalDateTime.MAX)
                .build();
    }

    private AccountInfoDto getBonusAccount() {
        if (isNull(bonusAccount)) {
            log.debug("^ trying to define bonus account info by system holder id: '{}'", systemBonusUserLogin);
            User systemBonusUser = userService.findByUsername(systemUserPrefix.concat(systemBonusUserLogin));
            bonusAccount = restFinancialUnitFacade.findAccountByHolder(systemBonusUser.getLeagueId(),
                    AccountHolderType.USER);
        }
        return bonusAccount;
    }
}

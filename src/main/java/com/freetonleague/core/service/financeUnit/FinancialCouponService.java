package com.freetonleague.core.service.financeUnit;

import com.freetonleague.core.domain.dto.CouponInfoDto;

/**
 * Service interface for save transactions and accounts in DB
 */
public interface FinancialCouponService {

    /**
     * Verify advertisement company by coupon hash
     *
     * @param couponHash advertisement company hash
     * @return updated Account Balance
     */
    CouponInfoDto getVerifiedAdvertisementCompany(String couponHash);
}

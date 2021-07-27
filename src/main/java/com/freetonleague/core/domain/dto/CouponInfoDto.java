package com.freetonleague.core.domain.dto;

import com.freetonleague.core.domain.dto.finance.AccountInfoDto;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.time.LocalDateTime;

@SuperBuilder
@NoArgsConstructor
@Data
public class CouponInfoDto {

    private AccountInfoDto couponAccount;

    private Double couponAmount;

    private LocalDateTime expirationDate;
}

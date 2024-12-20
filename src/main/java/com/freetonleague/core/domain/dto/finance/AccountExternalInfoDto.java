package com.freetonleague.core.domain.dto.finance;

import com.freetonleague.core.domain.enums.finance.BankProviderType;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@SuperBuilder
@Data
@NoArgsConstructor
public class AccountExternalInfoDto {

    private String externalBankAddress;

    private Double balance;

    private BankProviderType bankType;
}

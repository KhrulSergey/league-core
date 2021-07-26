package com.freetonleague.core.domain.dto.finance;

import com.freetonleague.core.domain.enums.finance.AccountHolderType;
import com.freetonleague.core.domain.enums.finance.BankProviderType;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@SuperBuilder
@Data
@NoArgsConstructor
public class AccountInfoDto {

    private String GUID;

    private AccountHolderType ownerType;

    private String ownerGUID;

    private String ownerExternalGUID;

    private Double amount;

    private String externalAddress;

    private BankProviderType externalBankType;

    private Boolean isNotTracking;
}

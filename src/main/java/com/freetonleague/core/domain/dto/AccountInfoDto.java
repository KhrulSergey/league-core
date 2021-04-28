package com.freetonleague.core.domain.dto;

import com.freetonleague.core.domain.enums.AccountHolderType;
import com.freetonleague.core.domain.enums.BankProviderType;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@SuperBuilder
@Data
@NoArgsConstructor
public class AccountInfoDto {

    private String GUID;

    @NotNull
    private AccountHolderType ownerType;

    @NotBlank
    private String ownerGUID;

    private Double amount;

    private String externalAddress;

    private BankProviderType externalBankType;
}

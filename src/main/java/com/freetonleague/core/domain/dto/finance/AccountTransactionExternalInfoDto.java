package com.freetonleague.core.domain.dto.finance;

import com.freetonleague.core.domain.enums.finance.AccountTransactionStatusType;
import com.freetonleague.core.domain.enums.finance.AccountTransactionTemplateType;
import com.freetonleague.core.domain.enums.finance.AccountTransactionType;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import javax.validation.constraints.NotNull;

@SuperBuilder
@Data
@NoArgsConstructor
public class AccountTransactionExternalInfoDto {

    private String GUID;

    @NotNull
    private Double amount;

    @NotNull
    private AccountInfoDto sourceAccount;

    @NotNull
    private AccountInfoDto targetAccount;

    private AccountTransactionParentInfoDto parentTransaction;

    private String name;

    private AccountTransactionStatusType status;

    @NotNull
    private AccountTransactionType transactionType;

    @Builder.Default
    @NotNull
    private AccountTransactionTemplateType transactionTemplateType = AccountTransactionTemplateType.DEFAULT;
}

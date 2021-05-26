package com.freetonleague.core.domain.dto;

import com.freetonleague.core.domain.enums.AccountTransactionStatusType;
import com.freetonleague.core.domain.enums.TransactionTemplateType;
import com.freetonleague.core.domain.enums.TransactionType;
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
    private TransactionType transactionType;

    @Builder.Default
    @NotNull
    private TransactionTemplateType transactionTemplateType = TransactionTemplateType.DEFAULT;
}

package com.freetonleague.core.domain.enums.finance;

import java.util.List;

public enum AccountTransactionType {
    DEPOSIT,
    WITHDRAW,
    PAYMENT, // payment of fees or products on the platform. Withdraw from source account to some Orgs account
    REFUND, // returned payment for some Orgs account to paid account. Transaction is connected to PAYMENT transaction.
    TRANSFER, // transfer with withdraw from source account to target account between Finance unit users

    ;

    public static final List<AccountTransactionType> withdrawTransactionTypeList = List.of(
            AccountTransactionType.WITHDRAW,
            AccountTransactionType.PAYMENT,
            AccountTransactionType.TRANSFER
    );

    public boolean isDeposit() {
        return this == DEPOSIT;
    }

    public boolean isTransfer() {
        return this == TRANSFER;
    }

    public boolean isPayment() {
        return this == PAYMENT;
    }

    public boolean isWithdraw() {
        return this == WITHDRAW;
    }
}

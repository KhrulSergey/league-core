package com.freetonleague.core.domain.enums;

import java.util.List;

public enum TransactionType {
    DEPOSIT,
    WITHDRAW,
    PAYMENT, // payment of fees or products on the platform. Withdraw from source account to some Orgs account
    REFUND, // returned payment for some Orgs account to paid account. Transaction is connected to PAYMENT transaction.
    TRANSFER, // transfer with withdraw from source account to target account between Finance unit users

    ;

    public static List<TransactionType> withdrawTransactionTypeList = List.of(
            TransactionType.WITHDRAW,
            TransactionType.PAYMENT,
            TransactionType.TRANSFER
    );

    public boolean isTransfer() {
        return this == TRANSFER;
    }

    public boolean isPayment() {
        return this == TRANSFER;
    }
}

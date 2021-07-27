package com.freetonleague.core.domain.enums.finance;

public enum AccountType {
    DEPOSIT, // aka DEPOSIT,  liquid deposits with no limit on the number of transactions per day and withdraws
    CREDIT, // account with fixed credit limit for use
    FIXED_DEPOSIT, // allows you to earn a fixed rate of interest for keeping a certain sum of money locked in for a given time
}

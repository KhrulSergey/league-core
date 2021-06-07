package com.freetonleague.core.repository;

import com.freetonleague.core.domain.model.Account;
import com.freetonleague.core.domain.model.AccountHolder;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.UUID;

public interface AccountRepository extends JpaRepository<Account, Long>,
        JpaSpecificationExecutor<Account> {

    Account findByGUID(UUID GUID);

    Account findByHolder(AccountHolder accountHolder);

    Account findByExternalAddress(String externalAddress);

    @Query(value = "select a.amount from Account a where a = :account ")
    Double getAmountForAccount(@Param("account") Account account);
}

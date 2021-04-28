package com.freetonleague.core.repository;

import com.freetonleague.core.domain.model.Account;
import com.freetonleague.core.domain.model.AccountHolder;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.UUID;

public interface AccountRepository extends JpaRepository<Account, Long>,
        JpaSpecificationExecutor<Account> {

    Account findByGUID(UUID GUID);

    Account findByHolder(AccountHolder accountHolder);
}

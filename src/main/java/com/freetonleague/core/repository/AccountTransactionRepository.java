package com.freetonleague.core.repository;

import com.freetonleague.core.domain.model.AccountTransaction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface AccountTransactionRepository extends JpaRepository<AccountTransaction, Long>,
        JpaSpecificationExecutor<AccountTransaction> {

}

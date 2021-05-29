package com.freetonleague.core.repository;

import com.freetonleague.core.domain.model.Account;
import com.freetonleague.core.domain.model.AccountTransaction;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.UUID;

public interface AccountTransactionRepository extends JpaRepository<AccountTransaction, Long>,
        JpaSpecificationExecutor<AccountTransaction> {

    AccountTransaction findByGUID(UUID GUID);

    @Query(value = "select t from AccountTransaction t where t.sourceAccount = :account or t.targetAccount = :account")
    Page<AccountTransaction> findAllByAccount(Pageable pageable, @Param("account") Account account);

    boolean existsByGUID(UUID GUID);

    @Query(value = "select case when count(a)> 0 then true else false end from " +
            "AccountTransaction a where a.GUID = :GUID and a.status = com.freetonleague.core.domain.enums.AccountTransactionStatusType.ABORTED")
    boolean isAbortedByGUID(@Param("GUID") UUID GUID);
}

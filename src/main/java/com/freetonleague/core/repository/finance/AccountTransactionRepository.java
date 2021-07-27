package com.freetonleague.core.repository.finance;

import com.freetonleague.core.domain.enums.finance.AccountTransactionStatusType;
import com.freetonleague.core.domain.model.finance.Account;
import com.freetonleague.core.domain.model.finance.AccountTransaction;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.UUID;

public interface AccountTransactionRepository extends JpaRepository<AccountTransaction, Long>,
        JpaSpecificationExecutor<AccountTransaction> {

    AccountTransaction findByGUID(UUID GUID);

    @Query(value = "select t from AccountTransaction t where (t.status in :statusList) and (t.sourceAccount = :account or t.targetAccount = :account)")
    Page<AccountTransaction> findAllByAccount(Pageable pageable,
                                              @Param("account") Account account,
                                              @Param("statusList") List<AccountTransactionStatusType> statusList);


    Page<AccountTransaction> findAllByStatusIn(Pageable pageable, List<AccountTransactionStatusType> statusList);

    boolean existsByGUID(UUID GUID);

    @Query(value = "select case when count(a)> 0 then true else false end from AccountTransaction a where a.GUID = :GUID " +
            "and a.status = com.freetonleague.core.domain.enums.finance.AccountTransactionStatusType.ABORTED")
    boolean isAbortedByGUID(@Param("GUID") UUID GUID);

    @Query(value = "select case when count(t)> 0 then true else false end from " +
            "AccountTransaction t where t.targetAccount = :account " +
            "and t.status = com.freetonleague.core.domain.enums.finance.AccountTransactionStatusType.FINISHED " +
            "and t.transactionTemplateType = com.freetonleague.core.domain.enums.finance.AccountTransactionTemplateType.EXTERNAL_PROVIDER " +
            "and t.transactionType = com.freetonleague.core.domain.enums.finance.AccountTransactionType.DEPOSIT")
    boolean isExistFinishedDepositTransaction(@Param("account") Account account);
}

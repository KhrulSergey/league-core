package com.freetonleague.core.repository.finance;

import com.freetonleague.core.domain.enums.AccountHolderType;
import com.freetonleague.core.domain.model.finance.AccountHolder;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.UUID;

public interface AccountHolderRepository extends JpaRepository<AccountHolder, Long>,
        JpaSpecificationExecutor<AccountHolder> {

    AccountHolder findByHolderExternalGUIDAndHolderType(UUID holderExternalGUID, AccountHolderType holderType);
}

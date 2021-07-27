package com.freetonleague.core.repository.finance;

import com.freetonleague.core.domain.model.finance.ExchangeOrder;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.UUID;

public interface ExchangeOrderRepository extends JpaRepository<ExchangeOrder, Long>,
        JpaSpecificationExecutor<ExchangeOrder> {

    ExchangeOrder findByGUID(UUID guid);

    boolean existsByGUID(UUID guid);
}

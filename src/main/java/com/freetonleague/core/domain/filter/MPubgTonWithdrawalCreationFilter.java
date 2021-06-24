package com.freetonleague.core.domain.filter;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class MPubgTonWithdrawalCreationFilter {

    private Double amount;
    private String pubgId;

}

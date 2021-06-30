package com.freetonleague.core.domain.filter;

import lombok.Builder;
import lombok.Data;

import javax.validation.constraints.NotNull;

@Data
@Builder
public class MPubgTonWithdrawalCreationFilter {

    @NotNull
    private Double tonAmount;
    private String pubgId;

}

package com.freetonleague.core.domain.filter;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MPubgTonWithdrawalCreationFilter {

    @NotNull
    private Double tonAmount;
    private String pubgId;

}

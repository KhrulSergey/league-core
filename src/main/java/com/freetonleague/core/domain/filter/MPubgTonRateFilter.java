package com.freetonleague.core.domain.filter;

import lombok.Data;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

@Data
public class MPubgTonRateFilter {

    @Min(0)
    @NotNull
    private final Double tonAmount;


}

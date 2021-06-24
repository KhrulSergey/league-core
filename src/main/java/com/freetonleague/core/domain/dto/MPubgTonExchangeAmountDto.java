package com.freetonleague.core.domain.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class MPubgTonExchangeAmountDto {

    private Double tonAmount;
    private Double ucAmount;

}

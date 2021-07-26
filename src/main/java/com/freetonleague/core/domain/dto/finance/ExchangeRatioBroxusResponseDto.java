package com.freetonleague.core.domain.dto.finance;

import com.freetonleague.core.domain.enums.finance.Currency;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.util.UUID;

@SuperBuilder
@NoArgsConstructor
@Data
public class ExchangeRatioBroxusResponseDto {

    private UUID id;

    private Currency from;

    private Currency to;

    private Double fromValue;

    private Double toValue;

    //TODO    @Expose(serialize = false, deserialize = true)
    private Double rate;

    private transient Long requestTimeRaw;
}

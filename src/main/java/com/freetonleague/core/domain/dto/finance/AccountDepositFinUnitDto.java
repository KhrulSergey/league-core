package com.freetonleague.core.domain.dto.finance;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import javax.validation.constraints.NotNull;

@Data
public class AccountDepositFinUnitDto {

    @JsonProperty(value = "address")
    private String externalAddress;

    @JsonProperty(value = "userId")
    private String accountGUID;

    @NotNull
    private Double amount;
}

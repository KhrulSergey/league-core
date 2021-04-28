package com.freetonleague.core.domain.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@SuperBuilder
@Data
@NoArgsConstructor
public class AccountBroxusDataDto {

    private Double balance;

    private String address;
}

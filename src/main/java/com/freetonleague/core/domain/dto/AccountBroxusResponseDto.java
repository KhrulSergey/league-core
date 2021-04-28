package com.freetonleague.core.domain.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import javax.validation.constraints.NotNull;

@SuperBuilder
@Data
@NoArgsConstructor
public class AccountBroxusResponseDto {

    private boolean success;

    @NotNull
    private AccountBroxusDataDto data;
}

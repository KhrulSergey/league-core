package com.freetonleague.core.domain.dto;

import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.validation.constraints.NotNull;

@EqualsAndHashCode(callSuper = true)
@Data
public class TeamDto extends TeamBaseDto {

    @NotNull
    private Long captainId;

    private ParticipantDto captain;
}

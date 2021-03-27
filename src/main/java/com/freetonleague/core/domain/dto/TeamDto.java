package com.freetonleague.core.domain.dto;

import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.validation.constraints.NotNull;

import static java.util.Objects.nonNull;

@EqualsAndHashCode(callSuper = true)
@Data
public class TeamDto extends TeamBaseDto {

    @NotNull
    private Long captainId;

    private TeamParticipantDto captain;

    public Long getCaptainId() {
        return nonNull(captain) ? captain.getId() : null;
    }
}

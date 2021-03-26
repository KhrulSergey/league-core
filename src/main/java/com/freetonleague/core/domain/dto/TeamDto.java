package com.freetonleague.core.domain.dto;

import com.freetonleague.core.domain.model.Participant;
import lombok.Data;

import javax.validation.constraints.NotNull;
import java.util.Set;

@Data
public class TeamDto extends TeamBaseDto {

    @NotNull
    private Long captainId;

    private ParticipantDto captain;
}

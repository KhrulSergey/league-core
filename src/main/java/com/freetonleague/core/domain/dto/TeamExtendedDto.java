package com.freetonleague.core.domain.dto;

import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.Set;

@EqualsAndHashCode(callSuper = true)
@Data
public class TeamExtendedDto extends TeamDto{
    private Set<ParticipantDto> participantList;
}

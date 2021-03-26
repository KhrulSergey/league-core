package com.freetonleague.core.domain.dto;

import lombok.Data;

import java.util.Set;

@Data
public class TeamExtendedDto extends TeamDto{
    private Set<ParticipantDto> participantList;
}

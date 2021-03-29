package com.freetonleague.core.domain.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
public class TeamDetailedInviteListDto {
    private TeamDto team;

    private List<TeamInviteRequestDto> inviteRequestList;
}

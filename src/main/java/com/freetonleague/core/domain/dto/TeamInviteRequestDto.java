package com.freetonleague.core.domain.dto;

import com.freetonleague.core.domain.enums.TeamInviteRequestStatusType;
import lombok.Data;

import java.time.LocalDateTime;

@Data

public class TeamInviteRequestDto {

    private String inviteToken;

    private Long teamId;

    private Long participantCreatorId;

    private LocalDateTime expiration;

    private TeamInviteRequestStatusType status;
}

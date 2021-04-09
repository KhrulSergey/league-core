package com.freetonleague.core.domain.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.freetonleague.core.domain.enums.TeamInviteRequestStatusType;
import lombok.Data;

import java.time.LocalDateTime;

@JsonInclude(JsonInclude.Include.NON_EMPTY)
@Data
public class TeamInviteRequestDto {

    private String inviteToken;

    private Long teamId;

    private Long participantCreatorId;

    private String invitedUser;

    private TeamParticipantDto participantApplied;

    private LocalDateTime expiration;

    private TeamInviteRequestStatusType status;
}

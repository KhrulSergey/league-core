package com.freetonleague.core.domain.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.freetonleague.core.domain.enums.TeamParticipantStatusType;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class UserTeamParticipateHistoryDto {
    private Long id;

    private TeamDto team;

    private TeamParticipantStatusType status;

    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private LocalDateTime joinAt;

    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private LocalDateTime deletedAt;
}

package com.freetonleague.core.domain.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;

@EqualsAndHashCode(callSuper = true)
@Data
public class UserPublicDto extends UserDto {

    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private List<UserTeamParticipateHistoryDto> userTeamParticipateHistoryList;
}

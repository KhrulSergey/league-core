package com.freetonleague.core.domain.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.freetonleague.core.domain.enums.UserParameterType;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;
import java.util.Map;

@EqualsAndHashCode(callSuper = true)
@Data
public class UserPublicDto extends UserDto {

    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private List<UserTeamParticipateHistoryDto> userTeamParticipateHistoryList;

    private Map<UserParameterType, String> parameters;

}

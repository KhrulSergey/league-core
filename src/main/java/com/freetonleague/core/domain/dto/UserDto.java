package com.freetonleague.core.domain.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.freetonleague.core.domain.enums.UserParameterType;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Data
public class UserDto {

    @NotNull(message = "leagueID must be not null")
    private UUID leagueId;

    @Size(max = 50)
    private String username;

    private String name;

    private String avatarHashKey;

    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    @ApiModelProperty(access = "hidden")
    private String email;

    private String discordId;

    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    @ApiModelProperty(access = "hidden")
    private String utmSource;

    @ApiModelProperty(readOnly = true)
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private List<String> roleList;

    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    @ApiModelProperty(access = "hidden")
    private LocalDateTime createdAt = null;

    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    @ApiModelProperty(access = "hidden")
    private LocalDateTime updatedAt = null;

    private Map<UserParameterType, String> parameters;

}

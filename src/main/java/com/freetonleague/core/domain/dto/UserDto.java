package com.freetonleague.core.domain.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.List;
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
}

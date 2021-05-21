package com.freetonleague.core.domain.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.freetonleague.core.domain.enums.TeamStateType;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

@Data
public class TeamBaseDto {

    private Long id;

    @NotBlank(message = "team name must be not blank")
    @Size(max = 25)
    private String name;

    @EqualsAndHashCode.Exclude
    @ToString.Exclude
    @ApiModelProperty(readOnly = true)
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private String logoHashKey;

    @ApiModelProperty(notes = "write-only value, ignored for client get requests")
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private String logoRawFile;

    private TeamStateType status;
}

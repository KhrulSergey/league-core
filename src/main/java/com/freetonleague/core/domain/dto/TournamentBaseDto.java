package com.freetonleague.core.domain.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.freetonleague.core.domain.enums.TournamentAccessType;
import com.freetonleague.core.domain.enums.TournamentStatusType;
import com.freetonleague.core.domain.enums.TournamentSystemType;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.time.LocalDateTime;
import java.util.List;

@Data
public class TournamentBaseDto {

    private Long id;

    @NotBlank
    @Size(max = 55)
    private String name;

    @NotNull
    private Long gameDisciplineId;

    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    @JsonIgnoreProperties("gameDisciplineSettings")
    private GameDisciplineDto gameDiscipline;

    @NotNull
    @ApiModelProperty(notes = "need to set isForcedFinished and tournamentWinnerList with 'FINISHED' status")
    private TournamentStatusType status;

    @NotNull
    private TournamentAccessType accessType;

    @NotNull
    private TournamentSystemType systemType;

    //Base settings
    @NotNull
    @ApiModelProperty(required = true)
    private String discordChannelId;

    private LocalDateTime signUpStartDate;

    private LocalDateTime signUpEndDate;

    private LocalDateTime startPlannedDate;

    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    @ApiModelProperty(readOnly = true)
    private LocalDateTime finishedDate;

    @NotNull
    private Long gameDisciplineSettingsId;

    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    @ApiModelProperty(readOnly = true)
    private UserDto tournamentCreator;

    private List<TournamentOrganizerDto> tournamentOrganizerList;

    //Detailed settings
    @NotNull
    private TournamentSettingsDto tournamentSettings;
}

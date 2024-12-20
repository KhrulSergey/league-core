package com.freetonleague.core.domain.dto.tournament;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.freetonleague.core.domain.dto.UserDto;
import com.freetonleague.core.domain.enums.AccessType;
import com.freetonleague.core.domain.enums.UserParameterType;
import com.freetonleague.core.domain.enums.tournament.TournamentParticipantType;
import com.freetonleague.core.domain.enums.tournament.TournamentStatusType;
import com.freetonleague.core.domain.enums.tournament.TournamentSystemType;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.time.LocalDateTime;
import java.util.List;

@Data
public class TournamentDto {

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
    private AccessType accessType;

    @NotNull
    private TournamentSystemType systemType;

    @NotNull
    @ApiModelProperty(required = true, notes = "Type of participant that accessible to participate in tournament")
    private TournamentParticipantType participantType = TournamentParticipantType.TEAM;

    //Base settings
    @NotNull
    @ApiModelProperty(required = true)
    private String discordChannelId;

    @EqualsAndHashCode.Exclude
    @ToString.Exclude
    @ApiModelProperty(readOnly = true)
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private String logoHashKey;

    @ApiModelProperty(notes = "write-only value, ignored for client get requests")
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private String logoRawFile;

    private String description;

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

    /**
     * Prototype for ref to Bank-Account entity for current tournament
     */
    @ApiModelProperty(readOnly = true)
    private Long fundAccountId;

    //Detailed settings
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private List<TournamentRoundDto> tournamentRoundList;

    @ApiModelProperty(notes = "need to set at least one element with 'force' finishing tournament")
    private List<TournamentWinnerDto> tournamentWinnerList;

    @ApiModelProperty(notes = "need to set true with 'force' finishing tournament")
    private Boolean isForcedFinished = false;

    private List<UserParameterType> mandatoryUserParameters;
}

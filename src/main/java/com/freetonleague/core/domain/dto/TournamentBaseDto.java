package com.freetonleague.core.domain.dto;

import com.freetonleague.core.domain.enums.TournamentAccessType;
import com.freetonleague.core.domain.enums.TournamentStatusType;
import com.freetonleague.core.domain.enums.TournamentSystemType;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

@Data
public class TournamentBaseDto {

    private Long id;

    @NotBlank
    private String name;

    @NotNull
    private Long gameDisciplineId;

    private TournamentStatusType status;

    @NotNull
    private TournamentAccessType accessType;

    @NotNull
    private TournamentSystemType systemType;


    //Base settings
    private String discordChannelName;

    private LocalDateTime signUpStartDate;

    private LocalDateTime signUpEndDate;

    private LocalDateTime startPlannedDate;
}

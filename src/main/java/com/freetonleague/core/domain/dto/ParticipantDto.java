package com.freetonleague.core.domain.dto;

import lombok.Data;

import javax.validation.constraints.NotNull;
import java.util.UUID;

@Data
public class ParticipantDto {
    @NotNull(message = "leagueId is required")
    private UUID leagueId;
}

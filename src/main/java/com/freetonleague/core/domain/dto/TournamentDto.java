package com.freetonleague.core.domain.dto;

import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.validation.constraints.NotNull;
import java.util.List;

@EqualsAndHashCode(callSuper = true)
@Data
public class TournamentDto extends TournamentBaseDto {

    @NotNull
    private Long gameDisciplineSettingsId;

    private List<TournamentOrganizerDto> tournamentOrganizerList;

    /**
     * Prototype for ref to Bank-Account entity for current tournament
     */
    private Long fundAccountId;

    //Detailed settings
    private TournamentSettingsDto tournamentSettings;
}

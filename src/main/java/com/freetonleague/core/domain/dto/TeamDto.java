package com.freetonleague.core.domain.dto;

import com.freetonleague.core.domain.model.Participant;
import lombok.Data;

import javax.validation.constraints.NotNull;
import java.util.Set;

@Data
public class TeamDto {

    @NotNull(message = "name must be not null")
    private String name;
    private Participant captain;
    private String teamLogoFileName;
    private Set<Participant> participantList;
}

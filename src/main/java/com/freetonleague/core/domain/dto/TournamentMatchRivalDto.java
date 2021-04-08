package com.freetonleague.core.domain.dto;

import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;
import java.util.Set;

@EqualsAndHashCode(callSuper = true)
@Data
public class TournamentMatchRivalDto extends TournamentMatchRivalBaseDto {

    Set<TournamentMatchRivalParticipantDto> rivalParticipants;

    private List<GameDisciplineIndicatorDto> matchIndicator;
}

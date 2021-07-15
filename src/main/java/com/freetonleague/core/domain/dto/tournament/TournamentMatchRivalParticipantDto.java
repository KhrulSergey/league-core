package com.freetonleague.core.domain.dto.tournament;

import com.freetonleague.core.domain.dto.tournament.GameDisciplineIndicatorDto;
import lombok.Data;

import java.util.List;

@Data
public class TournamentMatchRivalParticipantDto {

    private Long id;

    private Long tournamentMatchRivalId;

    private Long tournamentTeamParticipantId;

    private List<GameDisciplineIndicatorDto> participantIndicator;
}


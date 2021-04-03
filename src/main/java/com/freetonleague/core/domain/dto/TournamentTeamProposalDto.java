package com.freetonleague.core.domain.dto;

import com.freetonleague.core.domain.enums.TournamentTeamStateType;
import com.freetonleague.core.domain.enums.TournamentTeamType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.util.List;

@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@Data
public class TournamentTeamProposalDto {

    private Long teamId;

    private TournamentTeamStateType status;

    private TournamentTeamType type;

    private List<TournamentTeamParticipantDto> tournamentTeamParticipantList;
}

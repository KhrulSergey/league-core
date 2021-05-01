package com.freetonleague.core.domain.dto;

import com.freetonleague.core.domain.enums.TournamentTeamStateType;
import com.freetonleague.core.domain.enums.TournamentTeamType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@Data
public class TournamentTeamProposalBaseDto {

    private Integer id;

    private Long tournamentId;

    private TeamBaseDto team;

    private TournamentTeamStateType state;

    private TournamentTeamType type;
}
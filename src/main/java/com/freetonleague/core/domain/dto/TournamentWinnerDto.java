package com.freetonleague.core.domain.dto;

import com.freetonleague.core.domain.enums.TournamentWinnerPlaceType;
import lombok.Data;

@Data
public class TournamentWinnerDto {

    private Long tournamentId;

    private Long teamProposalId;

    private TeamBaseDto team;

    private TournamentWinnerPlaceType winnerPlaceType;
}

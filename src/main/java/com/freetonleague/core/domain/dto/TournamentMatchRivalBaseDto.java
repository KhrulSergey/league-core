package com.freetonleague.core.domain.dto;

import lombok.Data;

@Data
public class TournamentMatchRivalBaseDto {

    private Long id;

    private Long tournamentMatchId;

    private Long teamProposalId;
}

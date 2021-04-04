package com.freetonleague.core.domain.dto;

import com.freetonleague.core.domain.enums.TournamentTeamParticipantStatusType;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@SuperBuilder
@NoArgsConstructor
@Data
public class TournamentTeamParticipantDto {
    private Long tournamentTeamProposalId;

    private Long teamParticipantId;

    private String userLeagueId;

    private TournamentTeamParticipantStatusType statusInProposal;

}

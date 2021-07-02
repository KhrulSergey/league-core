package com.freetonleague.core.domain.dto;

import com.freetonleague.core.domain.enums.ParticipationStateType;
import com.freetonleague.core.domain.enums.TournamentParticipantType;
import com.freetonleague.core.domain.enums.TournamentTeamType;
import io.swagger.annotations.ApiModelProperty;
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

    private Integer id;

    private Long tournamentId;

    private TeamDto team;

    private ParticipationStateType state;

    @ApiModelProperty(notes = "Confirmation of tournament participation from team (check-in)")
    private Boolean confirmed;

    private TournamentTeamType type;

    /**
     * Type of participant that accessible to participate in tournament
     */
    private TournamentParticipantType participantType;

    private List<TournamentTeamParticipantDto> tournamentTeamParticipantList;
}

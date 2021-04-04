package com.freetonleague.core.mapper;

import com.freetonleague.core.domain.dto.TournamentTeamParticipantDto;
import com.freetonleague.core.domain.dto.TournamentTeamProposalDto;
import com.freetonleague.core.domain.model.TeamParticipant;
import com.freetonleague.core.domain.model.TournamentTeamProposal;
import org.mapstruct.InjectionStrategy;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", injectionStrategy = InjectionStrategy.CONSTRUCTOR,
        unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface TournamentTeamMapper {

    TournamentTeamParticipantDto toDto(TeamParticipant dto);

    TournamentTeamProposal fromDto(TournamentTeamProposalDto dto);

}

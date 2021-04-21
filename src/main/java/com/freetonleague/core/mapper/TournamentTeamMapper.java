package com.freetonleague.core.mapper;

import com.freetonleague.core.domain.dto.TournamentTeamParticipantDto;
import com.freetonleague.core.domain.dto.TournamentTeamProposalDto;
import com.freetonleague.core.domain.model.TeamParticipant;
import com.freetonleague.core.domain.model.TournamentTeamProposal;
import org.mapstruct.*;

import java.util.Set;

@Mapper(componentModel = "spring", injectionStrategy = InjectionStrategy.CONSTRUCTOR,
        unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface TournamentTeamMapper {

    TournamentTeamParticipantDto toDto(TeamParticipant dto);

    TournamentTeamProposal fromDto(TournamentTeamProposalDto dto);

    @Named(value = "toDto")
    TournamentTeamProposalDto toDto(TournamentTeamProposal entity);

    @Named(value = "toDtoList")
    @IterableMapping(qualifiedByName = "toDto")
    Set<TournamentTeamProposalDto> toDto(Set<TournamentTeamProposal> entity);
}

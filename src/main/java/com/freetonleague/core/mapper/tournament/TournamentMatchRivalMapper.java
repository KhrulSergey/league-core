package com.freetonleague.core.mapper.tournament;


import com.freetonleague.core.domain.dto.tournament.TournamentMatchRivalDto;
import com.freetonleague.core.domain.dto.tournament.TournamentMatchRivalParticipantDto;
import com.freetonleague.core.domain.model.tournament.TournamentMatchRival;
import com.freetonleague.core.domain.model.tournament.TournamentMatchRivalParticipant;
import org.mapstruct.*;

import java.util.List;
import java.util.Set;

@Mapper(componentModel = "spring", injectionStrategy = InjectionStrategy.CONSTRUCTOR,
        unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface TournamentMatchRivalMapper {

    //region TournamentMatchRival mapping
    @Named(value = "toDto")
    @Mapping(target = "tournamentMatchId", source = "entity.tournamentMatch.id")
    @Mapping(target = "teamProposalId", source = "entity.teamProposal.id")
    @Mapping(target = "rivalParticipantList", source = "entity.rivalParticipantList", qualifiedByName = "toParticipantDtoList")
    @Mapping(target = "teamId", source = "entity.teamProposal.team.id")
    TournamentMatchRivalDto toDto(TournamentMatchRival entity);

    @Named(value = "fromRivalDto")
    TournamentMatchRival fromDto(TournamentMatchRivalDto dto);

    @Named(value = "toDtoList")
    @IterableMapping(qualifiedByName = "toDto")
    List<TournamentMatchRivalDto> toDto(List<TournamentMatchRival> entities);

    @IterableMapping(qualifiedByName = "fromRivalDto")
    List<TournamentMatchRival> fromDto(List<TournamentMatchRivalDto> dtoList);
    //endregion


    //region TournamentMatchRivalParticipant mapping
    TournamentMatchRivalParticipant fromDto(TournamentMatchRivalParticipantDto dto);

    @Named(value = "toDto")
    @Mapping(target = "tournamentMatchRivalId", source = "entity.tournamentMatchRival.id")
    @Mapping(target = "tournamentTeamParticipantId", source = "entity.tournamentTeamParticipant.id")
    TournamentMatchRivalParticipantDto toDto(TournamentMatchRivalParticipant entity);

    @Named(value = "toParticipantDtoList")
    @IterableMapping(qualifiedByName = "toDto")
    Set<TournamentMatchRivalParticipantDto> toParticipantDtoList(Set<TournamentMatchRivalParticipant> participantEntities);

    List<TournamentMatchRivalParticipant> fromDtoParticipants(List<TournamentMatchRivalParticipantDto> dtoList);
    //endregion
}

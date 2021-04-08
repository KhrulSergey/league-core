package com.freetonleague.core.mapper;

import com.freetonleague.core.domain.dto.TournamentMatchRivalBaseDto;
import com.freetonleague.core.domain.dto.TournamentMatchRivalDto;
import com.freetonleague.core.domain.dto.TournamentMatchRivalParticipantDto;
import com.freetonleague.core.domain.model.TournamentMatchRival;
import com.freetonleague.core.domain.model.TournamentMatchRivalParticipant;
import org.mapstruct.*;

import java.util.List;

@Mapper(componentModel = "spring", injectionStrategy = InjectionStrategy.CONSTRUCTOR,
        unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface TournamentMatchRivalMapper {

    //region TournamentMatchRival mapping
    @Mapping(target = "tournamentMatchId", source = "entity.tournamentMatch.id")
    @Mapping(target = "teamProposalId", source = "entity.teamProposal.id")
    TournamentMatchRivalBaseDto toBaseDto(TournamentMatchRival entity);

    @Named(value = "toRivalDto")
    @Mapping(target = "tournamentMatchId", source = "entity.tournamentMatch.id")
    @Mapping(target = "teamProposalId", source = "entity.teamProposal.id")
    TournamentMatchRivalDto toDto(TournamentMatchRival entity);

    @Named(value = "fromRivalDto")
    TournamentMatchRival fromDto(TournamentMatchRivalDto dto);

    @IterableMapping(qualifiedByName = "toRivalDto")
    List<TournamentMatchRivalDto> toDto(List<TournamentMatchRival> entities);

    @IterableMapping(qualifiedByName = "fromRivalDto")
    List<TournamentMatchRival> fromDto(List<TournamentMatchRivalDto> dtoList);
    //endregion


    //region TournamentMatchRivalParticipant mapping
    TournamentMatchRivalParticipant fromDto(TournamentMatchRivalParticipantDto dto);

    @Mapping(target = "tournamentMatchRivalId", source = "entity.tournamentMatchRival.id")
    @Mapping(target = "tournamentTeamParticipantId", source = "entity.tournamentTeamParticipant.id")
    TournamentMatchRivalParticipantDto toDto(TournamentMatchRivalParticipant entity);

    List<TournamentMatchRivalParticipantDto> toDtoParticipants(List<TournamentMatchRivalParticipant> participantEntities);

    List<TournamentMatchRivalParticipant> fromDtoParticipants(List<TournamentMatchRivalParticipantDto> dtoList);
    //endregion
}

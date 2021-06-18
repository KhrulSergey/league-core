package com.freetonleague.core.mapper;

import com.freetonleague.core.domain.dto.TournamentSeriesRivalDto;
import com.freetonleague.core.domain.model.TournamentSeriesRival;
import org.mapstruct.*;

import java.util.List;

@Mapper(componentModel = "spring", injectionStrategy = InjectionStrategy.CONSTRUCTOR,
        unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface TournamentSeriesRivalMapper {

    @Named(value = "toDto")
    @Mapping(target = "tournamentSeriesId", source = "entity.tournamentSeries.id")
    @Mapping(target = "teamProposalId", source = "entity.teamProposal.id")
    @Mapping(target = "parentTournamentSeriesId", source = "entity.parentTournamentSeries.id")
    TournamentSeriesRivalDto toDto(TournamentSeriesRival entity);

    @Named(value = "fromDto")
    TournamentSeriesRival fromDto(TournamentSeriesRivalDto dto);

    @Named(value = "toDtoList")
    @IterableMapping(qualifiedByName = "toDto")
    List<TournamentSeriesRivalDto> toDtoList(List<TournamentSeriesRival> entity);

    @Named(value = "fromDtoList")
    @IterableMapping(qualifiedByName = "fromDto")
    List<TournamentSeriesRival> fromDtoList(List<TournamentSeriesRivalDto> entity);
    //endregion
}

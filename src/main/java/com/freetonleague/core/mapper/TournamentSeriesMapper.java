package com.freetonleague.core.mapper;

import com.freetonleague.core.domain.dto.TournamentSeriesBaseDto;
import com.freetonleague.core.domain.dto.TournamentSeriesDto;
import com.freetonleague.core.domain.model.TournamentSeries;
import org.mapstruct.*;

import java.util.List;

@Mapper(componentModel = "spring", injectionStrategy = InjectionStrategy.CONSTRUCTOR,
        unmappedTargetPolicy = ReportingPolicy.IGNORE, uses = TournamentMatchMapper.class)
public interface TournamentSeriesMapper {
    TournamentSeries fromDto(TournamentSeriesDto dto);

    @Mapping(target = "tournamentRoundId", source = "entity.tournamentRound.id")
    @Mapping(target = "matchList", source = "entity.matchList", qualifiedByName = "toBaseDto")
    @Named(value = "toDto")
    TournamentSeriesDto toDto(TournamentSeries entity);

    @Mapping(target = "tournamentRoundId", source = "entity.tournamentRound.id")
    @Named(value = "toBaseDto")
    TournamentSeriesBaseDto toBaseDto(TournamentSeries entity);

    @Named(value = "toDtoList")
    @IterableMapping(qualifiedByName = "toDto")
    List<TournamentSeriesDto> toDto(List<TournamentSeries> entity);

    @Named(value = "toBaseDtoList")
    @IterableMapping(qualifiedByName = "toBaseDto")
    List<TournamentSeriesBaseDto> toBaseDto(List<TournamentSeries> entity);
}

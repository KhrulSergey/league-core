package com.freetonleague.core.mapper;

import com.freetonleague.core.domain.dto.TournamentSeriesDto;
import com.freetonleague.core.domain.model.TournamentSeries;
import org.mapstruct.*;

import java.util.List;

@Mapper(componentModel = "spring", injectionStrategy = InjectionStrategy.CONSTRUCTOR,
        unmappedTargetPolicy = ReportingPolicy.IGNORE, uses = TournamentMatchMapper.class)
public interface TournamentSeriesMapper {
    TournamentSeries fromDto(TournamentSeriesDto dto);

    @Mapping(target = "tournamentId", source = "entity.tournament.id")
    @Mapping(target = "matchList", source = "entity.matchList", qualifiedByName = "toBaseDto")
    TournamentSeriesDto toDto(TournamentSeries entity);

    @Named(value = "toDto")
    List<TournamentSeriesDto> toDto(List<TournamentSeries> entity);
}

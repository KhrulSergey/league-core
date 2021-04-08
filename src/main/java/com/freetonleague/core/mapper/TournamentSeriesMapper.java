package com.freetonleague.core.mapper;

import com.freetonleague.core.domain.dto.TournamentMatchDto;
import com.freetonleague.core.domain.dto.TournamentSeriesDto;
import com.freetonleague.core.domain.model.TournamentMatch;
import com.freetonleague.core.domain.model.TournamentSeries;
import org.mapstruct.InjectionStrategy;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

import java.util.List;

@Mapper(componentModel = "spring", injectionStrategy = InjectionStrategy.CONSTRUCTOR,
        unmappedTargetPolicy = ReportingPolicy.IGNORE, uses = TournamentMatchMapper.class)
public interface TournamentSeriesMapper {
    TournamentSeries fromDto(TournamentSeriesDto dto);

    @Mapping(target = "tournamentId", source = "entity.tournament.id")
    TournamentSeriesDto toDto(TournamentSeries entity);

    List<TournamentMatchDto> toDto(List<TournamentMatch> entities);
}

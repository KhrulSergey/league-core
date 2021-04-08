package com.freetonleague.core.mapper;

import com.freetonleague.core.domain.dto.TournamentMatchDto;
import com.freetonleague.core.domain.model.TournamentMatch;
import org.mapstruct.InjectionStrategy;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

import java.util.List;

@Mapper(componentModel = "spring", injectionStrategy = InjectionStrategy.CONSTRUCTOR,
        unmappedTargetPolicy = ReportingPolicy.IGNORE, uses = TournamentMatchRivalMapper.class)
public interface TournamentMatchMapper {

    TournamentMatch fromDto(TournamentMatchDto dto);

    @Mapping(target = "tournamentSeriesId", source = "entity.tournamentSeries.id")
    TournamentMatchDto toDto(TournamentMatch entity);

    List<TournamentMatchDto> toDto(List<TournamentMatch> entities);
}

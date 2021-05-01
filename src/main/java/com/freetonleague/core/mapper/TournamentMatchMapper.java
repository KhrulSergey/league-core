package com.freetonleague.core.mapper;

import com.freetonleague.core.domain.dto.TournamentMatchBaseDto;
import com.freetonleague.core.domain.dto.TournamentMatchDto;
import com.freetonleague.core.domain.model.TournamentMatch;
import org.mapstruct.*;

import java.util.List;

@Mapper(componentModel = "spring", injectionStrategy = InjectionStrategy.CONSTRUCTOR,
        unmappedTargetPolicy = ReportingPolicy.IGNORE, uses = TournamentMatchRivalMapper.class)
public interface TournamentMatchMapper {

    TournamentMatch fromDto(TournamentMatchDto dto);

    @Named(value = "toDto")
    @Mapping(target = "tournamentSeriesId", source = "entity.tournamentSeries.id")
    @Mapping(target = "matchWinner", source = "entity.matchWinner", qualifiedByName = "toDto")
    @Mapping(target = "matchRivalList", source = "entity.matchRivalList", qualifiedByName = "toDtoList")
    TournamentMatchDto toDto(TournamentMatch entity);

    @Named(value = "toBaseDto")
    TournamentMatchBaseDto toBaseDto(TournamentMatch entity);

    @Named(value = "toDto")
    @IterableMapping(qualifiedByName = "toDto")
    List<TournamentMatchDto> toDto(List<TournamentMatch> entities);

    @Named(value = "toBaseDto")
    @IterableMapping(qualifiedByName = "toBaseDto")
    List<TournamentMatchBaseDto> toBaseDto(List<TournamentMatch> entities);
}
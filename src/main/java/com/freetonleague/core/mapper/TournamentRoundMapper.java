package com.freetonleague.core.mapper;

import com.freetonleague.core.domain.dto.TournamentRoundDto;
import com.freetonleague.core.domain.model.TournamentRound;
import org.mapstruct.*;

import java.util.List;

@Mapper(componentModel = "spring", injectionStrategy = InjectionStrategy.CONSTRUCTOR,
        unmappedTargetPolicy = ReportingPolicy.IGNORE, uses = TournamentSeriesMapper.class)
public interface TournamentRoundMapper {
    TournamentRound fromDto(TournamentRoundDto dto);

    @Mapping(target = "tournamentId", source = "entity.tournament.id")
    @Mapping(target = "seriesList", source = "entity.seriesList", qualifiedByName = "toBaseDtoList")
    TournamentRoundDto toDto(TournamentRound entity);

    @Named(value = "toDto")
    List<TournamentRoundDto> toDto(List<TournamentRound> entity);
}

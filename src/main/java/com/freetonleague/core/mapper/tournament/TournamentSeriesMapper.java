package com.freetonleague.core.mapper.tournament;

import com.freetonleague.core.domain.dto.tournament.TournamentSeriesDto;
import com.freetonleague.core.domain.dto.tournament.TournamentSeriesParentDto;
import com.freetonleague.core.domain.model.tournament.TournamentSeries;
import org.mapstruct.*;

import java.util.List;

@Mapper(componentModel = "spring", injectionStrategy = InjectionStrategy.CONSTRUCTOR,
        unmappedTargetPolicy = ReportingPolicy.IGNORE, uses = {TournamentMatchMapper.class,
        TournamentProposalMapper.class, TournamentSeriesRivalMapper.class})
public interface TournamentSeriesMapper {

    @Mapping(target = "seriesWinner", ignore = true)
    @Mapping(target = "seriesRivalList", ignore = true)
    TournamentSeries fromDto(TournamentSeriesDto dto);

    @Mapping(target = "tournamentRoundId", source = "entity.tournamentRound.id")
    @Mapping(target = "matchList", source = "entity.matchList", qualifiedByName = "toDto")
    @Mapping(target = "teamProposalList", source = "entity.teamProposalList", qualifiedByName = "toDtoList")
    @Mapping(target = "teamProposalWinner", source = "entity.teamProposalWinner", qualifiedByName = "toDto")
    @Mapping(target = "parentSeriesList", source = "entity.parentSeriesList", qualifiedByName = "toParentDtoSet")
    @Mapping(target = "seriesRivalList", source = "entity.seriesRivalList", qualifiedByName = "toDtoList")
    @Mapping(target = "seriesWinner", source = "entity.seriesWinner", qualifiedByName = "toDto")
    @Named(value = "toDto")
    TournamentSeriesDto toDto(TournamentSeries entity);

    @Mapping(target = "tournamentRoundId", source = "entity.tournamentRound.id")
    @Mapping(target = "teamProposalWinner", source = "entity.teamProposalWinner", qualifiedByName = "toDto")
    @Named(value = "toParentDto")
    TournamentSeriesParentDto toParentDto(TournamentSeries entity);

    @Named(value = "toDtoList")
    @IterableMapping(qualifiedByName = "toDto")
    List<TournamentSeriesDto> toDto(List<TournamentSeries> entity);

    @Named(value = "toParentDtoSet")
    @IterableMapping(qualifiedByName = "toParentDto")
    List<TournamentSeriesParentDto> toParentDtoSet(List<TournamentSeries> entity);
}

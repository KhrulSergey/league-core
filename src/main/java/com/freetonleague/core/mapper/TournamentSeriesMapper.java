package com.freetonleague.core.mapper;

import com.freetonleague.core.domain.dto.TournamentSeriesBaseDto;
import com.freetonleague.core.domain.dto.TournamentSeriesDto;
import com.freetonleague.core.domain.dto.TournamentSeriesParentDto;
import com.freetonleague.core.domain.model.TournamentSeries;
import org.mapstruct.*;

import java.util.List;
import java.util.Set;

@Mapper(componentModel = "spring", injectionStrategy = InjectionStrategy.CONSTRUCTOR,
        unmappedTargetPolicy = ReportingPolicy.IGNORE, uses = {TournamentMatchMapper.class, TournamentTeamMapper.class})
public interface TournamentSeriesMapper {
    TournamentSeries fromDto(TournamentSeriesDto dto);

    @Mapping(target = "tournamentRoundId", source = "entity.tournamentRound.id")
    @Mapping(target = "matchList", source = "entity.matchList", qualifiedByName = "toDto")
    @Mapping(target = "teamProposalList", source = "entity.teamProposalList", qualifiedByName = "toDtoList")
    @Mapping(target = "teamProposalWinner", source = "entity.teamProposalWinner", qualifiedByName = "toBaseDto")
    @Named(value = "toDto")
    TournamentSeriesDto toDto(TournamentSeries entity);

    @Mapping(target = "tournamentRoundId", source = "entity.tournamentRound.id")
    @Mapping(target = "parentSeriesList", source = "entity.parentSeriesList", qualifiedByName = "toParentDtoSet")
    @Named(value = "toBaseDto")
    TournamentSeriesBaseDto toBaseDto(TournamentSeries entity);

    @Mapping(target = "tournamentRoundId", source = "entity.tournamentRound.id")
    @Mapping(target = "teamProposalWinner", source = "entity.teamProposalWinner", qualifiedByName = "toBaseDto")
    @Named(value = "toParentDto")
    TournamentSeriesParentDto toParentDto(TournamentSeries entity);

    @Named(value = "toDtoList")
    @IterableMapping(qualifiedByName = "toDto")
    List<TournamentSeriesDto> toDto(List<TournamentSeries> entity);

    @Named(value = "toBaseDtoList")
    @IterableMapping(qualifiedByName = "toBaseDto")
    List<TournamentSeriesBaseDto> toBaseDto(List<TournamentSeries> entity);

    @Named(value = "toParentDtoSet")
    @IterableMapping(qualifiedByName = "toParentDto")
    Set<TournamentSeriesParentDto> toParentDtoSet(Set<TournamentSeries> entity);
}

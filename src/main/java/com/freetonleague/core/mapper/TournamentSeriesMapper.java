package com.freetonleague.core.mapper;

import com.freetonleague.core.domain.dto.TournamentSeriesBaseDto;
import com.freetonleague.core.domain.dto.TournamentSeriesDto;
import com.freetonleague.core.domain.dto.TournamentSeriesParentDto;
import com.freetonleague.core.domain.dto.TournamentSeriesRivalDto;
import com.freetonleague.core.domain.model.TournamentSeries;
import com.freetonleague.core.domain.model.TournamentSeriesRival;
import org.mapstruct.*;

import java.util.List;

@Mapper(componentModel = "spring", injectionStrategy = InjectionStrategy.CONSTRUCTOR,
        unmappedTargetPolicy = ReportingPolicy.IGNORE, uses = {TournamentMatchMapper.class, TournamentProposalMapper.class})
public interface TournamentSeriesMapper {

    //    @Mapping(target = "seriesRivalList", source = "dto.seriesRivalList", qualifiedByName = "fromDtoList")
    TournamentSeries fromDto(TournamentSeriesDto dto);

    @Mapping(target = "tournamentRoundId", source = "entity.tournamentRound.id")
    @Mapping(target = "matchList", source = "entity.matchList", qualifiedByName = "toDto")
    @Mapping(target = "teamProposalList", source = "entity.teamProposalList", qualifiedByName = "toDtoList")
    @Mapping(target = "teamProposalWinner", source = "entity.teamProposalWinner", qualifiedByName = "toBaseDto")
    @Mapping(target = "parentSeriesList", source = "entity.parentSeriesList", qualifiedByName = "toParentDtoSet")
    @Mapping(target = "seriesRivalList", source = "entity.seriesRivalList", qualifiedByName = "toDtoList")
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
    List<TournamentSeriesParentDto> toParentDtoSet(List<TournamentSeries> entity);

    //region TournamentSeriesRival mapping
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

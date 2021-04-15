package com.freetonleague.core.mapper;

import com.freetonleague.core.domain.dto.*;
import com.freetonleague.core.domain.model.Tournament;
import com.freetonleague.core.domain.model.TournamentOrganizer;
import com.freetonleague.core.domain.model.TournamentSettings;
import com.freetonleague.core.domain.model.TournamentWinner;
import org.mapstruct.*;

import java.util.List;

@Mapper(componentModel = "spring", injectionStrategy = InjectionStrategy.CONSTRUCTOR,
        unmappedTargetPolicy = ReportingPolicy.IGNORE, uses = {GameDisciplineMapper.class, UserMapper.class, TournamentSeriesMapper.class})
public interface TournamentMapper {

    //region Tournaments
    Tournament fromDto(TournamentDto dto);

    @Mapping(target = "gameDisciplineId", source = "entity.gameDiscipline.id")
    @Mapping(target = "gameDisciplineSettingsId", source = "entity.gameDisciplineSettings.id")
    @Mapping(target = "tournamentCreator", source = "entity.createdBy", qualifiedByName = "toDto")
    @Mapping(target = "tournamentSeriesList", source = "entity.tournamentSeriesList", qualifiedByName = "toDto")
    @Named(value = "toDto")
    TournamentDto toDto(Tournament entity);

    @Mapping(target = "gameDisciplineId", source = "entity.gameDiscipline.id")
    @Mapping(target = "gameDisciplineSettingsId", source = "entity.gameDisciplineSettings.id")
    @Mapping(target = "tournamentCreator", source = "entity.createdBy", qualifiedByName = "toDto")
    @Named(value = "toBaseDto")
    TournamentBaseDto toBaseDto(Tournament entity);

    @IterableMapping(qualifiedByName = "toDto")
    List<TournamentDto> toDto(List<Tournament> entities);

    @IterableMapping(qualifiedByName = "toBaseDto")
    List<TournamentBaseDto> toBaseDto(List<Tournament> entities);
    //endregion


    //region Tournament Settings
    TournamentSettings fromDto(TournamentSettingsDto dto);

    @Mapping(target = "tournamentId", source = "entity.tournament.id")
    TournamentSettingsDto toDto(TournamentSettings entity);
    //endregion


    //region Tournament Organizers
    TournamentOrganizer fromDto(TournamentOrganizerDto dto);

    @Mapping(target = "tournamentId", source = "entity.tournament.id")
    @Mapping(target = "userLeagueId", expression = "java(entity.getUser().getLeagueId().toString())")
    @Mapping(target = "user", source = "entity.user", qualifiedByName = "toDto")
    @Named(value = "toDto")
    TournamentOrganizerDto toDto(TournamentOrganizer entity);

    List<TournamentOrganizer> fromOrganizerListDto(List<TournamentOrganizerDto> dto);

    @IterableMapping(qualifiedByName = "toDto")
    List<TournamentOrganizerDto> toListOrganizersDto(List<TournamentOrganizer> entities);
    //endregion

    //region Tournament Winners
    TournamentWinner fromDto(TournamentWinnerDto dto);

    @Named(value = "toWinnerDto")
    TournamentWinnerDto toDto(TournamentWinner entity);

    @IterableMapping(qualifiedByName = "toWinnerDto")
    List<TournamentWinnerDto> toListWinnerDto(List<TournamentWinner> entities);
    //endregion
}

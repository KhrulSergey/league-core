package com.freetonleague.core.mapper;

import com.freetonleague.core.domain.dto.TournamentBaseDto;
import com.freetonleague.core.domain.dto.TournamentDto;
import com.freetonleague.core.domain.dto.TournamentOrganizerDto;
import com.freetonleague.core.domain.dto.TournamentSettingsDto;
import com.freetonleague.core.domain.model.Tournament;
import com.freetonleague.core.domain.model.TournamentOrganizer;
import com.freetonleague.core.domain.model.TournamentSettings;
import org.mapstruct.*;

import java.util.List;

@Mapper(componentModel = "spring", injectionStrategy = InjectionStrategy.CONSTRUCTOR,
        unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface TournamentMapper {

    //region Tournaments
    Tournament fromDto(TournamentDto dto);

    @Mapping(target = "gameDisciplineId", source = "entity.gameDiscipline.id")
    @Mapping(target = "gameDisciplineSettingsId", source = "entity.gameDisciplineSettings.id")
    @Named(value = "toDto")
    TournamentDto toDto(Tournament entity);

    @Mapping(target = "gameDisciplineId", source = "entity.gameDiscipline.id")
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
    @Named(value = "toDto")
    TournamentOrganizerDto toDto(TournamentOrganizer entity);

    List<TournamentOrganizer> fromListDto(List<TournamentOrganizerDto> dto);

    @IterableMapping(qualifiedByName = "toDto")
    List<TournamentOrganizerDto> toListDto(List<TournamentOrganizer> entities);
    //endregion

}

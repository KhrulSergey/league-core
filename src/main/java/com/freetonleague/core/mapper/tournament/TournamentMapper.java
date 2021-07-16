package com.freetonleague.core.mapper.tournament;

import com.freetonleague.core.domain.dto.tournament.TournamentDto;
import com.freetonleague.core.domain.dto.tournament.TournamentOrganizerDto;
import com.freetonleague.core.domain.dto.tournament.TournamentSettingsDto;
import com.freetonleague.core.domain.dto.tournament.TournamentWinnerDto;
import com.freetonleague.core.domain.model.tournament.Tournament;
import com.freetonleague.core.domain.model.tournament.TournamentOrganizer;
import com.freetonleague.core.domain.model.tournament.TournamentSettings;
import com.freetonleague.core.domain.model.tournament.TournamentWinner;
import com.freetonleague.core.mapper.TeamMapper;
import com.freetonleague.core.mapper.UserMapper;
import org.mapstruct.*;

import java.util.List;

@Mapper(componentModel = "spring", injectionStrategy = InjectionStrategy.CONSTRUCTOR,
        unmappedTargetPolicy = ReportingPolicy.IGNORE, uses = {GameDisciplineMapper.class, UserMapper.class,
        TournamentRoundMapper.class, TeamMapper.class})
public interface TournamentMapper {

    //region Tournaments
    Tournament fromDto(TournamentDto dto);

    @Mapping(target = "gameDisciplineId", source = "entity.gameDiscipline.id")
    @Mapping(target = "gameDisciplineSettingsId", source = "entity.gameDisciplineSettings.id")
    @Mapping(target = "tournamentCreator", source = "entity.createdBy", qualifiedByName = "toDto")
    @Mapping(target = "tournamentRoundList", source = "entity.tournamentRoundList", qualifiedByName = "toDto")
    @Mapping(target = "tournamentWinnerList", source = "entity.tournamentWinnerList", qualifiedByName = "toWinnerDtoList")
    @Named(value = "toDto")
    TournamentDto toDto(Tournament entity);

    @IterableMapping(qualifiedByName = "toDto")
    List<TournamentDto> toDto(List<Tournament> entities);
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
    @Mapping(target = "team", source = "teamProposal.team", qualifiedByName = "toDto")
    @Mapping(target = "tournamentId", source = "tournament.id")
    @Mapping(target = "teamProposalId", source = "teamProposal.id")
    TournamentWinnerDto toDto(TournamentWinner entity);

    @Named(value = "toWinnerDtoList")
    @IterableMapping(qualifiedByName = "toWinnerDto")
    List<TournamentWinnerDto> toListWinnerDto(List<TournamentWinner> entities);
    //endregion
}

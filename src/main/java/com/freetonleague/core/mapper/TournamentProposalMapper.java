package com.freetonleague.core.mapper;

import com.freetonleague.core.domain.dto.TournamentTeamParticipantDto;
import com.freetonleague.core.domain.dto.TournamentTeamProposalDto;
import com.freetonleague.core.domain.model.TeamParticipant;
import com.freetonleague.core.domain.model.TournamentTeamParticipant;
import com.freetonleague.core.domain.model.TournamentTeamProposal;
import org.mapstruct.*;

import java.util.List;

@Mapper(componentModel = "spring", injectionStrategy = InjectionStrategy.CONSTRUCTOR,
        unmappedTargetPolicy = ReportingPolicy.IGNORE, uses = {TeamParticipantMapper.class, TeamMapper.class})
public interface TournamentProposalMapper {

    @Named(value = "toDto")
    @Mapping(target = "tournamentId", source = "entity.tournament.id")
    @Mapping(target = "team", source = "entity.team", qualifiedByName = "toDto")
    @Mapping(target = "tournamentTeamParticipantList", source = "entity.tournamentTeamParticipantList", qualifiedByName = "toDtoList")
    TournamentTeamProposalDto toDto(TournamentTeamProposal entity);

    TournamentTeamProposal fromDto(TournamentTeamProposalDto dto);

    TournamentTeamParticipantDto toDto(TeamParticipant entity);

    @Mapping(target = "tournamentTeamProposalId", source = "entity.tournamentTeamProposal.id")
    @Mapping(target = "teamParticipantId", source = "entity.teamParticipant.id")
    @Mapping(target = "userLeagueId", expression = "java(entity.getUser().getLeagueId().toString())")
    @Mapping(target = "statusInProposal", source = "entity.status")
    @Named(value = "toDto")
    TournamentTeamParticipantDto toDto(TournamentTeamParticipant entity);

    @Named(value = "toDtoList")
    @IterableMapping(qualifiedByName = "toDto")
    List<TournamentTeamParticipantDto> toDtoList(List<TournamentTeamParticipant> entity);

    @Named(value = "toDtoList")
    @IterableMapping(qualifiedByName = "toDto")
    List<TournamentTeamProposalDto> toDto(List<TournamentTeamProposal> entity);
}

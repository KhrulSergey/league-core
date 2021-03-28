package com.freetonleague.core.mapper;

import com.freetonleague.core.domain.dto.TeamParticipantDto;
import com.freetonleague.core.domain.model.TeamParticipant;
import org.mapstruct.InjectionStrategy;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

import java.util.List;

@Mapper(componentModel = "spring", injectionStrategy = InjectionStrategy.CONSTRUCTOR,
        unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface TeamParticipantMapper {

    TeamParticipant fromDto(TeamParticipantDto dto);

    @Mapping(target = "teamId", source = "entity.team.id")
    @Mapping(target = "userLeagueId", expression = "java(entity.getUser().getLeagueId().toString())")
    TeamParticipantDto toDto(TeamParticipant entity);

    List<TeamParticipant> fromDto(List<TeamParticipantDto> dtoList);

    List<TeamParticipantDto> toDto(List<TeamParticipant> entities);
}

package com.freetonleague.core.mapper;

import com.freetonleague.core.domain.dto.TeamDto;
import com.freetonleague.core.domain.dto.TeamExtendedDto;
import com.freetonleague.core.domain.model.Team;
import org.mapstruct.*;

import java.util.List;

@Mapper(componentModel = "spring", injectionStrategy = InjectionStrategy.CONSTRUCTOR,
        unmappedTargetPolicy = ReportingPolicy.IGNORE, uses = TeamParticipantMapper.class)
public interface TeamMapper {

    Team fromDto(TeamDto dto);

    Team fromDto(TeamExtendedDto dto);

    @Named(value = "toDto")
    @Mapping(target = "captain", source = "entity.captain", qualifiedByName = "toDto")
    TeamDto toDto(Team entity);

    @Named(value = "toExtendedDto")
    @Mapping(target = "participantList", source = "entity.participantList", qualifiedByName = "toDtoSet")
    TeamExtendedDto toExtendedDto(Team entity);

    @IterableMapping(qualifiedByName = "toDto")
    List<TeamDto> toDto(List<Team> entities);

    @IterableMapping(qualifiedByName = "toExtendedDto")
    List<TeamExtendedDto> toExtendedDto(List<Team> entities);
}

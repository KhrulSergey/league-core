package com.freetonleague.core.mapper;

import com.freetonleague.core.domain.dto.TeamParticipantDto;
import com.freetonleague.core.domain.model.TeamParticipant;
import org.mapstruct.*;

import java.util.List;
import java.util.Set;

@Mapper(componentModel = "spring", injectionStrategy = InjectionStrategy.CONSTRUCTOR,
        unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface TeamParticipantMapper {

    TeamParticipant fromDto(TeamParticipantDto dto);

    @Mapping(target = "teamId", source = "entity.team.id")
    TeamParticipantDto toDto(TeamParticipant entity);

    List<TeamParticipant> fromDto(List<TeamParticipantDto> dtoList);

    List<TeamParticipantDto> toDto(List<TeamParticipant> entities);

    @Named(value = "toDtoList")
    Set<TeamParticipantDto> toDto(Set<TeamParticipant> entities);
}

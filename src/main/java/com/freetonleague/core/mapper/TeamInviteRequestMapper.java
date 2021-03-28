package com.freetonleague.core.mapper;

import com.freetonleague.core.domain.dto.TeamInviteRequestDto;
import com.freetonleague.core.domain.model.TeamInviteRequest;
import org.mapstruct.InjectionStrategy;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

import java.util.List;

@Mapper(componentModel = "spring", injectionStrategy = InjectionStrategy.CONSTRUCTOR,
        unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface TeamInviteRequestMapper {

    TeamInviteRequest fromDto(TeamInviteRequestDto dto);

    @Mapping(target = "teamId", source = "entity.team.id")
    @Mapping(target = "participantCreatorId", source = "entity.participantCreator.id")
    TeamInviteRequestDto toDto(TeamInviteRequest entity);

    List<TeamInviteRequestDto> toDto(List<TeamInviteRequest> entities);
}

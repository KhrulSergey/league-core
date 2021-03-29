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

    @Mapping(target = "invitedUser",
            expression = "java(java.util.Objects.nonNull(entity.getInvitedUser())?entity.getInvitedUser().getLeagueId().toString() : null)")
    @Mapping(target = "teamId", source = "entity.team.id")
    @Mapping(target = "participantCreatorId", source = "entity.participantCreator.id")
    @Mapping(target = "participantAppliedId", source = "entity.participantApplied.id")
    TeamInviteRequestDto toDto(TeamInviteRequest entity);

    List<TeamInviteRequestDto> toDto(List<TeamInviteRequest> entities);
}

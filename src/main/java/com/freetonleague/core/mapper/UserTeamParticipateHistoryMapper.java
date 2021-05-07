package com.freetonleague.core.mapper;

import com.freetonleague.core.domain.dto.UserTeamParticipateHistoryDto;
import com.freetonleague.core.domain.model.TeamParticipant;
import com.freetonleague.core.service.TeamParticipantService;
import org.mapstruct.*;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;
import java.util.stream.Collectors;

@Mapper(componentModel = "spring", injectionStrategy = InjectionStrategy.CONSTRUCTOR,
        unmappedTargetPolicy = ReportingPolicy.IGNORE, uses = TeamMapper.class)
public abstract class UserTeamParticipateHistoryMapper {

    @Autowired
    private TeamParticipantService teamParticipantService;

    @Mapping(target = "team", source = "entity.team", qualifiedByName = "toBaseDto")
    public abstract UserTeamParticipateHistoryDto fromParticipant(TeamParticipant entity);

    @Named(value = "toUserTeamParticipateHistoryDto")
    public List<UserTeamParticipateHistoryDto> toUserTeamParticipateHistoryDto(List<TeamParticipant> entityList) {
        return teamParticipantService.filterTeamParticipantFoPublic(entityList).parallelStream()
                .map(this::fromParticipant).collect(Collectors.toList());
    }
}

package com.freetonleague.core.mapper;

import com.freetonleague.core.domain.dto.UserTeamParticipateHistoryDto;
import com.freetonleague.core.domain.model.TeamParticipant;
import org.mapstruct.InjectionStrategy;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

import java.util.List;

@Mapper(componentModel = "spring", injectionStrategy = InjectionStrategy.CONSTRUCTOR,
        unmappedTargetPolicy = ReportingPolicy.IGNORE, uses = TeamMapper.class)
public interface UserTeamParticipateHistoryMapper {

    @Mapping(target = "team", source = "entity.team", qualifiedByName = "toBaseDto")
    UserTeamParticipateHistoryDto fromParticipant(TeamParticipant entity);

    List<UserTeamParticipateHistoryDto> fromParticipant(List<TeamParticipant> dtoList);

}

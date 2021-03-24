package com.freetonleague.core.mapper;

import com.freetonleague.core.domain.dto.TeamDto;
import com.freetonleague.core.domain.model.Team;
import org.mapstruct.InjectionStrategy;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

import java.util.List;

@Mapper(componentModel = "spring", injectionStrategy = InjectionStrategy.CONSTRUCTOR,
        unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface TeamMapper {

    Team fromDto(TeamDto dto);

    TeamDto toDto(Team entity);

    List<Team> fromDto(List<TeamDto> dtoList);

    List<TeamDto> toDto(List<Team> entities);
}

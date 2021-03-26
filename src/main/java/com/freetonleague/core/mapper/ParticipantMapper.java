package com.freetonleague.core.mapper;

import com.freetonleague.core.domain.dto.ParticipantDto;
import com.freetonleague.core.domain.model.Participant;
import org.mapstruct.InjectionStrategy;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

import java.util.List;

@Mapper(componentModel = "spring", injectionStrategy = InjectionStrategy.CONSTRUCTOR,
        unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface ParticipantMapper {

    Participant fromDto(ParticipantDto dto);

    @Mapping(target = "teamId", source = "entity.team.id")
    ParticipantDto toDto(Participant entity);

    List<Participant> fromDto(List<ParticipantDto> dtoList);

    List<ParticipantDto> toDto(List<Participant> entities);
}

package com.freetonleague.core.mapper;


import com.freetonleague.core.domain.dto.SessionDto;
import com.freetonleague.core.domain.model.Session;
import org.mapstruct.InjectionStrategy;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", injectionStrategy = InjectionStrategy.CONSTRUCTOR,
        unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface SessionMapper {

    Session fromDto(SessionDto dto);
}

package com.freetonleague.core.mapper;

import com.freetonleague.core.domain.dto.GameDisciplineDto;
import com.freetonleague.core.domain.model.GameDiscipline;
import org.mapstruct.InjectionStrategy;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

import java.util.List;

@Mapper(componentModel = "spring", injectionStrategy = InjectionStrategy.CONSTRUCTOR,
        unmappedTargetPolicy = ReportingPolicy.IGNORE, uses = GameDisciplineSettingsMapper.class)
public interface GameDisciplineMapper {

    GameDiscipline fromDto(GameDisciplineDto dto);

    GameDisciplineDto toDto(GameDiscipline entity);

    List<GameDisciplineDto> toDto(List<GameDiscipline> entities);
}

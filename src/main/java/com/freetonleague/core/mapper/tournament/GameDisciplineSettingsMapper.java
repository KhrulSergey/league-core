package com.freetonleague.core.mapper.tournament;

import com.freetonleague.core.domain.dto.tournament.GameDisciplineSettingsDto;
import com.freetonleague.core.domain.model.tournament.GameDisciplineSettings;
import org.mapstruct.InjectionStrategy;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

import java.util.List;

@Mapper(componentModel = "spring", injectionStrategy = InjectionStrategy.CONSTRUCTOR,
        unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface GameDisciplineSettingsMapper {

    @Mapping(target = "isPrimary", source = "dto.primary")
    GameDisciplineSettings fromDto(GameDisciplineSettingsDto dto);

    @Mapping(target = "gameDisciplineId", source = "entity.gameDiscipline.id")
    @Mapping(target = "primary", source = "entity.isPrimary")
    GameDisciplineSettingsDto toDto(GameDisciplineSettings entity);

    List<GameDisciplineSettingsDto> toDto(List<GameDisciplineSettings> entities);
}

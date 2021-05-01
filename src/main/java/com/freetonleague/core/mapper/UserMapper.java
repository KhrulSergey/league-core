package com.freetonleague.core.mapper;

import com.freetonleague.core.domain.dto.UserDto;
import com.freetonleague.core.domain.dto.UserPublicDto;
import com.freetonleague.core.domain.model.User;
import org.mapstruct.*;

import java.util.List;

@Mapper(componentModel = "spring", injectionStrategy = InjectionStrategy.CONSTRUCTOR,
        unmappedTargetPolicy = ReportingPolicy.IGNORE, uses = UserTeamParticipateHistoryMapper.class)
public interface UserMapper {

    @Mapping(target = "roleList", ignore = true)
    User fromDto(UserDto dto);

    @Named(value = "toDto")
    UserDto toDto(User entity);

    @Named(value = "toPubicDto")
    @Mapping(target = "userTeamParticipateHistoryList", source = "entity.userTeamParticipantList")
    UserPublicDto toPubicDto(User entity);

    List<User> fromDto(List<UserDto> dtoList);

    @IterableMapping(qualifiedByName = "toDto")
    List<UserDto> toDto(List<User> entities);

}


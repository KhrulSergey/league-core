package com.freetonleague.core.mapper;

import com.freetonleague.core.domain.dto.UserBonusDto;
import com.freetonleague.core.domain.dto.UserDto;
import com.freetonleague.core.domain.dto.UserPublicDto;
import com.freetonleague.core.domain.filter.UserInfoFilter;
import com.freetonleague.core.domain.model.User;
import org.mapstruct.*;

import java.util.List;

@Mapper(componentModel = "spring", injectionStrategy = InjectionStrategy.CONSTRUCTOR,
        unmappedTargetPolicy = ReportingPolicy.IGNORE, uses = UserTeamParticipateHistoryMapper.class)
public interface UserMapper {

    @Mapping(target = "roleList", ignore = true)
    User fromDto(UserDto dto);

    @Named(value = "toDto")
    @Mapping(target = "email", ignore = true)
    @Mapping(target = "utmSource", ignore = true)
    UserDto toDto(User entity);

    @Named(value = "toPubicDto")
    @Mapping(target = "userTeamParticipateHistoryList", source = "entity.userTeamParticipantList",
            qualifiedByName = "toUserTeamParticipateHistoryDto")
    @Mapping(target = "email", ignore = true)
    @Mapping(target = "utmSource", ignore = true)
    UserPublicDto toPubicDto(User entity);

    @Named(value = "toBonusDto")
    UserBonusDto toBonusDto(User entity);

    List<User> fromDto(List<UserDto> dtoList);

    @IterableMapping(qualifiedByName = "toDto")
    List<UserDto> toDto(List<User> entities);

    void applyChanges(@MappingTarget User user, UserInfoFilter filter);

}


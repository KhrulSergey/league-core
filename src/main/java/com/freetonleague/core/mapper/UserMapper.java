package com.freetonleague.core.mapper;

import com.freetonleague.core.domain.dto.UserDto;
import com.freetonleague.core.domain.model.User;
import org.mapstruct.InjectionStrategy;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

import java.util.List;

@Mapper(componentModel = "spring", injectionStrategy = InjectionStrategy.CONSTRUCTOR,
        unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface UserMapper {

    User fromDto(UserDto dto);

    UserDto toDto(User entity);

    List<User> fromDto(List<UserDto> dtoList);

    List<UserDto> toDto(List<User> entities);

}


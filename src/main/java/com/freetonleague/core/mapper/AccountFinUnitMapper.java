package com.freetonleague.core.mapper;

import com.freetonleague.core.domain.dto.AccountInfoDto;
import com.freetonleague.core.domain.model.Account;
import org.mapstruct.*;

@Mapper(componentModel = "spring", injectionStrategy = InjectionStrategy.CONSTRUCTOR,
        unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface AccountFinUnitMapper {

    @Named(value = "toDto")
    @Mapping(target = "GUID", expression = "java(entity.getGUID().toString())")
    @Mapping(target = "ownerType", source = "entity.holder.holderType")
    @Mapping(target = "ownerGUID", expression = "java(entity.getHolder().getGUID().toString())")
    @Mapping(target = "ownerExternalGUID", expression = "java(entity.getHolder().getHolderExternalGUID().toString())")
    AccountInfoDto toDto(Account entity);
}


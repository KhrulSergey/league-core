package com.freetonleague.core.mapper;

import com.freetonleague.core.domain.dto.AccountTransactionInfoDto;
import com.freetonleague.core.domain.dto.AccountTransactionParentInfoDto;
import com.freetonleague.core.domain.model.AccountTransaction;
import org.mapstruct.*;

@Mapper(componentModel = "spring", injectionStrategy = InjectionStrategy.CONSTRUCTOR,
        unmappedTargetPolicy = ReportingPolicy.IGNORE, uses = AccountFinUnitMapper.class)
public interface AccountTransactionFinUnitMapper {

    @Named(value = "toDto")
    @Mapping(target = "GUID", expression = "java(entity.getGUID().toString())")
    @Mapping(target = "sourceAccount", source = "entity.sourceAccount", qualifiedByName = "toDto")
    @Mapping(target = "targetAccount", source = "entity.targetAccount", qualifiedByName = "toDto")
    @Mapping(target = "parentTransaction", source = "entity.parentTransaction", qualifiedByName = "toParentDto")
    AccountTransactionInfoDto toDto(AccountTransaction entity);

    @Named(value = "toParentDto")
    @Mapping(target = "GUID", expression = "java(entity.getGUID().toString())")
    @Mapping(target = "sourceAccountGUID", expression = "java(entity.getSourceAccount().getGUID().toString())")
    @Mapping(target = "targetAccountGUID", expression = "java(entity.getTargetAccount().getGUID().toString())")
    AccountTransactionParentInfoDto toParentDto(AccountTransaction entity);

    @Mapping(target = "GUID", ignore = true)
    @Mapping(target = "sourceAccount", ignore = true)
    @Mapping(target = "targetAccount", ignore = true)
    @Mapping(target = "parentTransaction", ignore = true)
    AccountTransaction fromDto(AccountTransactionInfoDto dto);
}

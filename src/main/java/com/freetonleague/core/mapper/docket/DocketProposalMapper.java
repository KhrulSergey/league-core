package com.freetonleague.core.mapper.docket;

import com.freetonleague.core.domain.dto.docket.DocketUserProposalBonusDto;
import com.freetonleague.core.domain.dto.docket.DocketUserProposalDto;
import com.freetonleague.core.domain.enums.AccountHolderType;
import com.freetonleague.core.domain.model.docket.DocketUserProposal;
import com.freetonleague.core.mapper.UserMapper;
import com.freetonleague.core.service.FinancialClientService;
import org.mapstruct.*;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

@Mapper(componentModel = "spring", injectionStrategy = InjectionStrategy.CONSTRUCTOR,
        unmappedTargetPolicy = ReportingPolicy.IGNORE, uses = {UserMapper.class},
        imports = AccountHolderType.class)
public abstract class DocketProposalMapper {

    @Autowired
    protected FinancialClientService financialClientService;

    @Named(value = "toDto")
    @Mapping(target = "docketId", source = "entity.docket.id")
    @Mapping(target = "leagueId", expression = "java(entity.getUser().getLeagueId().toString())")
    public abstract DocketUserProposalDto toDto(DocketUserProposal entity);

    @Named(value = "toBonusDto")
    @Mapping(target = "docketId", source = "entity.docket.id")
    @Mapping(target = "user", source = "entity.user", qualifiedByName = "toBonusDto")
    @Mapping(target = "userAccount",
            expression = "java(financialClientService.getAccountByHolderInfo(entity.getUser().getLeagueId(), " +
                    "AccountHolderType.USER))")
    public abstract DocketUserProposalBonusDto toBonusDto(DocketUserProposal entity);

    public abstract DocketUserProposal fromDto(DocketUserProposalDto dto);

    @Named(value = "toDtoList")
    @IterableMapping(qualifiedByName = "toDto")
    public abstract List<DocketUserProposalDto> toDto(List<DocketUserProposal> entity);

    @Named(value = "toDtoList")
    @IterableMapping(qualifiedByName = "toBonusDto")
    public abstract List<DocketUserProposalBonusDto> toBonusDto(List<DocketUserProposal> entity);
}

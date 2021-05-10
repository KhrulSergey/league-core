package com.freetonleague.core.mapper;

import com.freetonleague.core.domain.dto.DocketUserProposalDto;
import com.freetonleague.core.domain.model.DocketUserProposal;
import org.mapstruct.*;

import java.util.List;

@Mapper(componentModel = "spring", injectionStrategy = InjectionStrategy.CONSTRUCTOR,
        unmappedTargetPolicy = ReportingPolicy.IGNORE, uses = {UserMapper.class})
public interface DocketProposalMapper {

    @Named(value = "toDto")
    @Mapping(target = "docketId", source = "entity.docket.id")
    DocketUserProposalDto toDto(DocketUserProposal entity);

    DocketUserProposal fromDto(DocketUserProposalDto dto);

    @Named(value = "toDtoList")
    @IterableMapping(qualifiedByName = "toDto")
    List<DocketUserProposalDto> toDto(List<DocketUserProposal> entity);
}

package com.freetonleague.core.mapper;

import com.freetonleague.core.domain.dto.NewsDto;
import com.freetonleague.core.domain.model.News;
import com.freetonleague.core.mapper.docket.DocketProposalMapper;
import org.mapstruct.*;

import java.util.List;

@Mapper(componentModel = "spring", injectionStrategy = InjectionStrategy.CONSTRUCTOR,
        unmappedTargetPolicy = ReportingPolicy.IGNORE, uses = {DocketProposalMapper.class})
public interface NewsMapper {

    @Named(value = "toDto")
    NewsDto toDto(News entity);

    News fromDto(NewsDto dto);

    @IterableMapping(qualifiedByName = "toDto")
    List<NewsDto> toDto(List<News> entities);
}

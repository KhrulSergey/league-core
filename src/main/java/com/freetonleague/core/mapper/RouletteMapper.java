package com.freetonleague.core.mapper;

import com.freetonleague.core.domain.dto.MatchHistoryItemDto;
import com.freetonleague.core.domain.dto.RouletteBetDto;
import com.freetonleague.core.domain.entity.RouletteBetEntity;
import com.freetonleague.core.domain.entity.RouletteMatchEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper
public interface RouletteMapper {

    @Mapping(target = "winnerUserId", source = "winnerBet.user.id")
    MatchHistoryItemDto toMatchHistoryItem(RouletteMatchEntity rouletteMatchEntity);

    List<MatchHistoryItemDto> toMatchHistoryList(List<RouletteMatchEntity> allByFinishedTrueOrderByCreatedAt);

    @Mapping(target = "userId", source = "user.id")
    RouletteBetDto toBetDto(RouletteBetEntity rouletteBetEntity, Double chance);

}

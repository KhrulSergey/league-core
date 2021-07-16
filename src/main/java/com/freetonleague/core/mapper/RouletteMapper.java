package com.freetonleague.core.mapper;

import com.freetonleague.core.domain.dto.RouletteMatchHistoryItemDto;
import com.freetonleague.core.domain.dto.RouletteBetDto;
import com.freetonleague.core.domain.entity.RouletteBetEntity;
import com.freetonleague.core.domain.entity.RouletteMatchEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper
public interface RouletteMapper {

    @Mapping(target = "winnerUserLeagueId", source = "winnerBet.user.leagueId")
    RouletteMatchHistoryItemDto toMatchHistoryItem(RouletteMatchEntity rouletteMatchEntity);

    List<RouletteMatchHistoryItemDto> toMatchHistoryList(List<RouletteMatchEntity> allByFinishedTrueOrderByCreatedAt);

    @Mapping(target = "userLeagueId", source = "rouletteBetEntity.user.leagueId")
    RouletteBetDto toBetDto(RouletteBetEntity rouletteBetEntity, Double chance);

}

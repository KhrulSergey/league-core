package com.freetonleague.core.mapper;

import com.freetonleague.core.domain.dto.RouletteMatchHistoryItemDto;
import com.freetonleague.core.domain.dto.RouletteBetDto;
import com.freetonleague.core.domain.entity.RouletteBetEntity;
import com.freetonleague.core.domain.entity.RouletteMatchEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.springframework.data.domain.Page;

import java.util.List;

@Mapper
public interface RouletteMapper {

    @Mapping(target = "winnerTicketNumberFrom", source = "match.winnerBet.ticketNumberFrom")
    @Mapping(target = "winnerTicketNumberTo", source = "match.winnerBet.ticketNumberTo")
    @Mapping(target = "winnerBetAmount", source = "match.winnerBet.tonAmount")
    @Mapping(target = "winnerUserLeagueId", source = "match.winnerBet.user.leagueId")
    RouletteMatchHistoryItemDto toMatchHistoryItem(RouletteMatchEntity match, Double winnerChance);

    default Page<RouletteMatchHistoryItemDto> toMatchHistoryPage(Page<RouletteMatchEntity> allByFinishedTrueOrderByCreatedAt) {
        return allByFinishedTrueOrderByCreatedAt.map(match -> {
                    long sum = match.getBets().stream()
                            .mapToLong(RouletteBetEntity::getTonAmount)
                            .sum();

                    return toMatchHistoryItem(match, match.getWinnerBet().getTonAmount() * 100D / sum);
                }
        );
    }

    @Mapping(target = "userLeagueId", source = "rouletteBetEntity.user.leagueId")
    RouletteBetDto toBetDto(RouletteBetEntity rouletteBetEntity, Double chance);

}

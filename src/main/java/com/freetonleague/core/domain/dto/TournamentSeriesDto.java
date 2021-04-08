package com.freetonleague.core.domain.dto;

import com.freetonleague.core.domain.enums.TournamentSeriesType;
import com.freetonleague.core.domain.enums.TournamentStatusType;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
public class TournamentSeriesDto {

    private String name;

    private Long tournamentId;

    private List<TournamentMatchDto> matchesDtoList;

    private Integer seriesSequencePosition;

    private TournamentStatusType status;

    private TournamentSeriesType type;

    private LocalDateTime startPlannedDate;

    private LocalDateTime startDate;

    private LocalDateTime finishedDate;
}

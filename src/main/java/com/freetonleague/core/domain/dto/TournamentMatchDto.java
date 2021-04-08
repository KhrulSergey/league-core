package com.freetonleague.core.domain.dto;

import com.freetonleague.core.domain.enums.TournamentStatusType;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.Set;

@Data
public class TournamentMatchDto {

    private String name;

    private Long tournamentSeriesId;

    private Set<TournamentMatchRivalDto> rivals;

    private TournamentStatusType status;

    private LocalDateTime startPlannedDate;

    private LocalDateTime startDate;

    private LocalDateTime finishedDate;
}


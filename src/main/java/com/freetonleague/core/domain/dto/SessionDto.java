package com.freetonleague.core.domain.dto;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class SessionDto {

    private String token;

    private String authProvider;

    private String userLeagueId;

    private LocalDateTime expiration;
}

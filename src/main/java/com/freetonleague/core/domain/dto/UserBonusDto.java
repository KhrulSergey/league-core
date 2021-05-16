package com.freetonleague.core.domain.dto;

import lombok.Data;

import java.util.UUID;

@Data
public class UserBonusDto {

    private UUID leagueId;

    private String username;

    private String name;

    private String utmSource;
}

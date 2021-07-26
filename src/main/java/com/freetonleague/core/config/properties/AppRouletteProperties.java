package com.freetonleague.core.config.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.validation.annotation.Validated;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;

@Data
@Validated
@Configuration
@ConfigurationProperties("app.roulette")
public class AppRouletteProperties {

    private Long minBetAmount;

    private Long maxBetAmount;

    private Long startBetAmount;

    private Long startDelaySeconds;

    private Integer minPlayersCount;

    private Integer maxPlayersCount;

    @Min(0)
    @Max(1)
    private Double commissionFactor;

    private String randomOrgApiKey;

}

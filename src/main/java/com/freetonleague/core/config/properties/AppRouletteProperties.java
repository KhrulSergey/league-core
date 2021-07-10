package com.freetonleague.core.config.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.validation.annotation.Validated;

@Data
@Validated
@Configuration
@ConfigurationProperties("app.roulette")
public class AppRouletteProperties {

    private Double minBetAmount;

    private Double maxBetAmount;

    private Double startBetAmount;

    private Long startDelaySeconds;

}

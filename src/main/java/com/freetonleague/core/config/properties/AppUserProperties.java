package com.freetonleague.core.config.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.validation.annotation.Validated;

import javax.validation.constraints.NotNull;
import java.util.Map;

@Data
@Validated
@Configuration
@ConfigurationProperties("app.user")
public class AppUserProperties {

    @NotNull
    private Map<@NotNull String, @NotNull Double> utmSourceRegisterBonusMap;

}

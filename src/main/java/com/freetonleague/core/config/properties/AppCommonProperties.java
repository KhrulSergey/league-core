package com.freetonleague.core.config.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.validation.annotation.Validated;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

@Data
@Validated
@Configuration
@ConfigurationProperties("app.common")
public class AppCommonProperties {

    @NotNull
    @Min(1000)
    private Long settingsRefreshRate = 1000L;

}

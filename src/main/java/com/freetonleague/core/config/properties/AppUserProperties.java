package com.freetonleague.core.config.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import javax.validation.constraints.NotNull;

@Data
@Configuration
@ConfigurationProperties("app.user")
public class AppUserProperties {

    @NotNull
    private RegisterBonus registerBonus;

    @Data
    public static class RegisterBonus {

        @NotNull
        private String utmSource;

        @NotNull
        private Double amount;

    }


}

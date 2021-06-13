package com.freetonleague.core.config.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Data
@Configuration
@ConfigurationProperties("config.kafka")
public class KafkaProperties {

    private String bootstrapAddress;
    private Notifications notifications;

    @Data
    public static class Notifications {

        private String topicName;
        private Boolean startDebug = false;

    }

}

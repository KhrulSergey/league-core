package com.freetonleague.core;

import com.freetonleague.core.domain.enums.EventProducerModelType;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.Bean;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.web.servlet.LocaleResolver;
import org.springframework.web.servlet.i18n.SessionLocaleResolver;

import java.util.Arrays;
import java.util.Locale;

@SpringBootApplication
@EnableScheduling
@EnableFeignClients
@EnableJpaAuditing(auditorAwareRef = "auditorProvider")
public class LeagueCoreApplication {

    public static void main(String[] args) {
        SpringApplication.run(LeagueCoreApplication.class, args);
    }

    //Define locale to constant english
    @Bean
    public LocaleResolver localeResolver() {
        SessionLocaleResolver slr = new SessionLocaleResolver();
        slr.setDefaultLocale(Locale.US);
        return slr;
    }

    @Bean
    public String[] kafkaTopicList() {
        return Arrays.stream(EventProducerModelType.values())
                .map(EventProducerModelType::getTopicName).toArray(String[]::new);
    }

    @Bean
    public Gson gsonSerializer() {
        return new GsonBuilder().serializeNulls().enableComplexMapKeySerialization().create();
    }
}

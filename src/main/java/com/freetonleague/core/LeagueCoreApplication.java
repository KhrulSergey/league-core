package com.freetonleague.core;

import com.freetonleague.core.config.SecurityAuditorAwareImpl;
import com.freetonleague.core.domain.model.User;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.Bean;
import org.springframework.data.domain.AuditorAware;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.web.servlet.LocaleResolver;
import org.springframework.web.servlet.i18n.SessionLocaleResolver;

import java.util.Locale;

@SpringBootApplication
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
    AuditorAware<User> auditorProvider() {
        return new SecurityAuditorAwareImpl();
    }
}

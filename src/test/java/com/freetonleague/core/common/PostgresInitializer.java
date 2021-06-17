package com.freetonleague.core.common;

import org.springframework.boot.test.util.TestPropertyValues;
import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.ConfigurableApplicationContext;
import org.testcontainers.containers.JdbcDatabaseContainer;
import org.testcontainers.containers.PostgreSQLContainerProvider;

public class PostgresInitializer implements ApplicationContextInitializer<ConfigurableApplicationContext> {

    private final JdbcDatabaseContainer<?> POSTGRESQL_CONTAINER = new PostgreSQLContainerProvider().newInstance("11.5-alpine");

    public PostgresInitializer() {
        POSTGRESQL_CONTAINER.withDatabaseName("league_core");
        POSTGRESQL_CONTAINER.start();
    }

    @Override
    public void initialize(ConfigurableApplicationContext configurableApplicationContext) {
        TestPropertyValues.of(
                "spring.datasource.url=" + POSTGRESQL_CONTAINER.getJdbcUrl(),
                "spring.datasource.username=" + POSTGRESQL_CONTAINER.getUsername(),
                "spring.datasource.password=" + POSTGRESQL_CONTAINER.getPassword(),
                "spring.jpa.show-sql=" + true
        ).applyTo(configurableApplicationContext.getEnvironment());
    }

}

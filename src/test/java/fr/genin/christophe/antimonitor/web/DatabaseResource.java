package fr.genin.christophe.antimonitor.web;

import io.quarkus.test.common.QuarkusTestResourceLifecycleManager;
import org.testcontainers.containers.PostgreSQLContainer;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class DatabaseResource implements QuarkusTestResourceLifecycleManager {

    public static final PostgreSQLContainer DATABASE = new PostgreSQLContainer<>("postgres:10.5")
            .withNetworkAliases("postgres")
            .withDatabaseName("antimonitor")
            .withUsername("antimonitor")
            .withPassword("antimonitor")
            .withExposedPorts(5430);

    @Override
    public Map<String, String> start() {
        DATABASE.start();
        final HashMap<String, String> map = new HashMap<>();
        map.put("quarkus.datasource.reactive.url", DATABASE.getJdbcUrl().replace("jdbc:", ""));
        map.put("quarkus.datasource.jdbc.url", DATABASE.getJdbcUrl());
        return map;
    }

    @Override
    public void stop() {
        DATABASE.stop();
    }
}

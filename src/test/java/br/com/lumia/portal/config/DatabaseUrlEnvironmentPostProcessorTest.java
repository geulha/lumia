package br.com.lumia.portal.config;

import org.junit.jupiter.api.Test;
import org.springframework.mock.env.MockEnvironment;

import static org.junit.jupiter.api.Assertions.*;

class DatabaseUrlEnvironmentPostProcessorTest {

    @Test
    void shouldConvertCloudPostgresUrlToJdbcUrl() {
        DatabaseUrlEnvironmentPostProcessor processor = new DatabaseUrlEnvironmentPostProcessor();
        MockEnvironment env = new MockEnvironment();
        env.setProperty("SPRING_DATASOURCE_URL", "postgresql://lumia_user:so5c5SldPPX0sodzTaX3CxwxXbReT0UJ@dpg-dbohn1nf3r2c73b6pvbg-a/lumia_78ix");

        processor.postProcessEnvironment(env, null);

        assertEquals("jdbc:postgresql://dpg-dbohn1nf3r2c73b6pvbg-a:5432/lumia_78ix", env.getProperty("spring.datasource.url"));
        assertEquals("lumia_user", env.getProperty("spring.datasource.username"));
        assertEquals("so5c5SldPPX0sodzTaX3CxwxXbReT0UJ", env.getProperty("spring.datasource.password"));
    }

    @Test
    void shouldConvertPostgresProtocolWithoutExplicitPort() {
        DatabaseUrlEnvironmentPostProcessor processor = new DatabaseUrlEnvironmentPostProcessor();
        MockEnvironment env = new MockEnvironment();
        env.setProperty("DATABASE_URL", "postgres://admin:secret123@myhost.render.com/mydb");

        processor.postProcessEnvironment(env, null);

        assertEquals("jdbc:postgresql://myhost.render.com:5432/mydb", env.getProperty("spring.datasource.url"));
        assertEquals("admin", env.getProperty("spring.datasource.username"));
        assertEquals("secret123", env.getProperty("spring.datasource.password"));
    }

    @Test
    void shouldIgnoreStandardJdbcUrl() {
        DatabaseUrlEnvironmentPostProcessor processor = new DatabaseUrlEnvironmentPostProcessor();
        MockEnvironment env = new MockEnvironment();
        env.setProperty("spring.datasource.url", "jdbc:postgresql://localhost:5432/lumia_db");

        processor.postProcessEnvironment(env, null);

        assertEquals("jdbc:postgresql://localhost:5432/lumia_db", env.getProperty("spring.datasource.url"));
    }
}

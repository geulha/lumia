package br.com.lumia.portal.config;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.env.EnvironmentPostProcessor;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.MapPropertySource;

import java.net.URI;
import java.util.HashMap;
import java.util.Map;

/**
 * Normaliza automaticamente URLs de banco fornecidas no formato nativo de plataformas cloud
 * como Render, Heroku ou Railway (ex: postgresql://user:pass@host:5432/db ou postgres://...)
 * convertendo-as para a sintaxe exigida pelo JDBC (jdbc:postgresql://host:5432/db).
 */
@Order(Ordered.HIGHEST_PRECEDENCE)
public class DatabaseUrlEnvironmentPostProcessor implements EnvironmentPostProcessor {

    @Override
    public void postProcessEnvironment(ConfigurableEnvironment environment, SpringApplication application) {
        String rawUrl = environment.getProperty("SPRING_DATASOURCE_URL");
        if (rawUrl == null || rawUrl.isBlank()) {
            rawUrl = environment.getProperty("DATABASE_URL");
        }
        if (rawUrl == null || rawUrl.isBlank()) {
            rawUrl = environment.getProperty("spring.datasource.url");
        }

        if (rawUrl != null && (rawUrl.startsWith("postgres://") || rawUrl.startsWith("postgresql://"))) {
            try {
                // Substitui prefixo para validar via URI
                String cleanUrl = rawUrl.replaceFirst("^postgres(ql)?://", "http://");
                URI uri = URI.create(cleanUrl);

                String host = uri.getHost();
                int port = uri.getPort() > 0 ? uri.getPort() : 5432;
                String path = uri.getPath();
                String dbName = (path != null && path.length() > 1) ? path.substring(1) : "";

                String jdbcUrl = "jdbc:postgresql://" + host + ":" + port + "/" + dbName;

                Map<String, Object> props = new HashMap<>();
                props.put("spring.datasource.url", jdbcUrl);

                String userInfo = uri.getUserInfo();
                if (userInfo != null && !userInfo.isBlank()) {
                    String[] parts = userInfo.split(":", 2);
                    props.put("spring.datasource.username", parts[0]);
                    if (parts.length > 1) {
                        props.put("spring.datasource.password", parts[1]);
                    }
                }

                environment.getPropertySources().addFirst(new MapPropertySource("renderDbUrlOverridden", props));
            } catch (Exception ignored) {
                // Se houver falha de parse, deixa o Spring Boot seguir com a URL original
            }
        }
    }
}

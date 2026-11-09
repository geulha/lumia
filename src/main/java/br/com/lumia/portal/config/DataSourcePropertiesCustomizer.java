package br.com.lumia.portal.config;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.boot.autoconfigure.jdbc.DataSourceProperties;
import org.springframework.stereotype.Component;

import java.net.URI;

/**
 * Garantia em tempo de inicialização de beans: se a URL em DataSourceProperties
 * estiver no formato cloud (postgresql://... ou postgres://...), converte para JDBC.
 */
@Component
public class DataSourcePropertiesCustomizer implements BeanPostProcessor {

    @Override
    public Object postProcessBeforeInitialization(Object bean, String beanName) throws BeansException {
        if (bean instanceof DataSourceProperties props) {
            String url = props.getUrl();
            if (url != null && (url.startsWith("postgres://") || url.startsWith("postgresql://"))) {
                try {
                    String cleanUrl = url.replaceFirst("^postgres(ql)?://", "http://");
                    URI uri = URI.create(cleanUrl);

                    String host = uri.getHost();
                    int port = uri.getPort() > 0 ? uri.getPort() : 5432;
                    String path = uri.getPath();
                    String dbName = (path != null && path.length() > 1) ? path.substring(1) : "";

                    String jdbcUrl = "jdbc:postgresql://" + host + ":" + port + "/" + dbName;
                    if (uri.getQuery() != null && !uri.getQuery().isBlank()) {
                        jdbcUrl += "?" + uri.getQuery();
                    }

                    props.setUrl(jdbcUrl);

                    String userInfo = uri.getUserInfo();
                    if (userInfo != null && !userInfo.isBlank()) {
                        String[] parts = userInfo.split(":", 2);
                        props.setUsername(parts[0]);
                        if (parts.length > 1) {
                            props.setPassword(parts[1]);
                        }
                    }
                    System.out.println("[LUMIA DataSource] DataSourceProperties ajustado para JDBC: " + jdbcUrl);
                } catch (Exception e) {
                    System.err.println("[LUMIA DataSource] Erro ao ajustar DataSourceProperties: " + e.getMessage());
                }
            }
        }
        return bean;
    }
}

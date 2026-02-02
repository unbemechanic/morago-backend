package morago.configuration.network;

import org.springframework.boot.autoconfigure.orm.jpa.HibernatePropertiesCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class HibernateConfig {
    private final QueryCounterInterceptor inspector;

    public HibernateConfig(QueryCounterInterceptor inspector) {
        this.inspector = inspector;
    }

    @Bean
    public HibernatePropertiesCustomizer hibernatePropertiesCustomizer() {
        return props -> props.put("hibernate.session_factory.statement_inspector", inspector);
    }
}

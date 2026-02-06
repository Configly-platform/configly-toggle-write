package pl.feature.toggle.service.write.infrastructure.out.rest;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import pl.feature.toggle.service.write.application.port.out.ConfigurationClient;

@Configuration
class RestOutboundConfig {

    @Bean
    ConfigurationClient configurationClient() {
        return new RestConfigurationClient();
    }
}

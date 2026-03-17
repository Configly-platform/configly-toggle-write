package pl.feature.toggle.service.write.infrastructure.out.rest;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestClient;
import pl.feature.toggle.service.model.security.correlation.CorrelationProvider;
import pl.feature.toggle.service.write.application.port.out.ConfigurationClient;

@Configuration
class RestOutboundConfig {

    @Bean
    ConfigurationClient configurationClient(RestClient restClient, CorrelationProvider correlationProvider) {
        return new RestConfigurationClient(restClient, correlationProvider);
    }

    @Bean
    RestClient configurationRestClient(@Value("${client.configuration.base-url}") String baseUrl) {
        return RestClient.builder()
                .baseUrl(baseUrl)
                .build();
    }
}

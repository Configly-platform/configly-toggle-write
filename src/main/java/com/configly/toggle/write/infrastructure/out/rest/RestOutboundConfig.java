package com.configly.toggle.write.infrastructure.out.rest;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import com.configly.web.client.InternalRestClient;
import com.configly.toggle.write.application.port.out.ConfigurationClient;

@Configuration
class RestOutboundConfig {

    @Bean
    ConfigurationClient configurationClient(InternalRestClient internalRestClient) {
        return new RestConfigurationClient(internalRestClient);
    }
}

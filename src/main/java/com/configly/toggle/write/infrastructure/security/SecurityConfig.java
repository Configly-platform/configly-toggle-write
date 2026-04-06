package com.configly.toggle.write.infrastructure.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.web.SecurityFilterChain;
import com.configly.web.model.actor.ActorProvider;
import com.configly.web.model.correlation.CorrelationProvider;

@Configuration
class SecurityConfig {

    @Bean
    ActorProvider actorProvider() {
        return new JwtActorProvider();
    }

    @Bean
    CorrelationProvider correlationProvider() {
        return new MDCCorrelationProvider();
    }

    @Bean
    SecurityFilterChain securityFilterChain(HttpSecurity http) {
        return http
                .csrf(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/actuator/**").permitAll()
                        .anyRequest().permitAll()
                )
//                .oauth2ResourceServer(oauth -> oauth.jwt(Customizer.withDefaults()))
                .build();
    }

}

package pl.feature.toggle.service.write.infrastructure.security;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.context.annotation.RequestScope;
import pl.feature.toggle.service.model.security.actor.ActorProvider;
import pl.feature.toggle.service.model.security.correlation.CorrelationProvider;

@Configuration("securityConfig")
class Config {

    @Bean
    ActorProvider actorProvider() {
        return new JwtActorProvider();
    }

    @Bean
    @RequestScope
    CorrelationProvider correlationProvider(HttpServletRequest request) {
        return new HttpCorrelationProvider(request);
    }

}

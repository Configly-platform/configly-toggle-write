package pl.feature.toggle.service.write.application.policy;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import pl.feature.toggle.service.write.application.port.out.FeatureToggleQueryRepository;

@Configuration("featureTogglePolicyConfig")
class Config {

    @Bean
    FeatureTogglePolicyFacade featureTogglePolicyFacade(FeatureToggleQueryRepository featureToggleQueryRepository) {
        return FeatureTogglePolicyFacade.create(featureToggleQueryRepository);
    }
}

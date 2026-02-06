package pl.feature.toggle.service.write.infrastructure.in.internal;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import pl.feature.toggle.service.write.application.port.in.EnvironmentRefConsistency;
import pl.feature.toggle.service.write.application.port.in.ProjectRefConsistency;

@Configuration
class InternalInboundConfig {

    @Bean
    ProjectRefRebuildListener projectRefRebuildListener(ProjectRefConsistency projectRefConsistency) {
        return new ProjectRefRebuildListener(projectRefConsistency);
    }

    @Bean
    EnvironmentRefRebuildListener environmentRefRebuildListener(EnvironmentRefConsistency environmentRefConsistency) {
        return new EnvironmentRefRebuildListener(environmentRefConsistency);
    }
}

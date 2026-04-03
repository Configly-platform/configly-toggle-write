package com.configly.toggle.write.infrastructure.in.internal;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import com.configly.toggle.write.application.port.in.ArchiveFeatureTogglesByEnvironmentUseCase;
import com.configly.toggle.write.application.port.in.EnvironmentRefConsistency;
import com.configly.toggle.write.application.port.in.ProjectRefConsistency;

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

    @Bean
    EnvironmentArchivedCascadeListener environmentArchivedCascadeListener(ArchiveFeatureTogglesByEnvironmentUseCase archiveFeatureTogglesByEnvironmentUseCase) {
        return new EnvironmentArchivedCascadeListener(archiveFeatureTogglesByEnvironmentUseCase);
    }
}

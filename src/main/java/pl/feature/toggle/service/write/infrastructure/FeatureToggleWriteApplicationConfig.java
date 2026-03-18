package pl.feature.toggle.service.write.infrastructure;

import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import pl.feature.toggle.service.event.processing.api.RevisionProjectionApplier;
import pl.feature.toggle.service.outbox.api.OutboxWriter;
import pl.feature.toggle.service.write.application.handler.FeatureToggleHandlerFacade;
import pl.feature.toggle.service.write.application.port.in.*;
import pl.feature.toggle.service.write.application.port.out.*;
import pl.feature.toggle.service.write.application.projection.environment.EnvironmentProjectionFacade;
import pl.feature.toggle.service.write.application.projection.project.ProjectProjectionFacade;

@Configuration
class FeatureToggleWriteApplicationConfig {

    @Bean
    CreateFeatureToggleUseCase createFeatureToggleUseCase(
            FeatureToggleCommandRepository featureToggleCommandRepository,
            ProjectRefConsistency projectRefConsistency,
            EnvironmentRefConsistency environmentRefConsistency,
            OutboxWriter outboxWriter
    ) {
        return FeatureToggleHandlerFacade.createFeatureToggleUseCase(featureToggleCommandRepository,
                projectRefConsistency,
                environmentRefConsistency,
                outboxWriter);
    }

    @Bean
    UpdateFeatureToggleUseCase updateFeatureToggleUseCase(
            FeatureToggleCommandRepository featureToggleCommandRepository,
            FeatureToggleQueryRepository featureToggleQueryRepository,
            ProjectRefConsistency projectRefConsistency,
            EnvironmentRefConsistency environmentRefConsistency,
            OutboxWriter outboxWriter
    ) {
        return FeatureToggleHandlerFacade.updateFeatureToggleUseCase(featureToggleCommandRepository,
                featureToggleQueryRepository,
                projectRefConsistency,
                environmentRefConsistency,
                outboxWriter);
    }

    @Bean
    ChangeFeatureToggleStatusUseCase changeFeatureToggleStatusUseCase(
            FeatureToggleCommandRepository featureToggleCommandRepository,
            FeatureToggleQueryRepository featureToggleQueryRepository,
            ProjectRefConsistency projectRefConsistency,
            EnvironmentRefConsistency environmentRefConsistency,
            OutboxWriter outboxWriter
    ) {
        return FeatureToggleHandlerFacade.changeFeatureToggleStatusUseCase(featureToggleCommandRepository,
                featureToggleQueryRepository,
                projectRefConsistency,
                environmentRefConsistency,
                outboxWriter
        );
    }

    @Bean
    ChangeFeatureToggleValueUseCase changeFeatureToggleValueUseCase(
            FeatureToggleCommandRepository featureToggleCommandRepository,
            FeatureToggleQueryRepository featureToggleQueryRepository,
            ProjectRefConsistency projectRefConsistency,
            EnvironmentRefConsistency environmentRefConsistency,
            OutboxWriter outboxWriter
    ) {
        return FeatureToggleHandlerFacade.changeFeatureToggleValueUseCase(featureToggleCommandRepository,
                featureToggleQueryRepository,
                projectRefConsistency,
                environmentRefConsistency,
                outboxWriter);
    }

    @Bean
    ProjectProjection projectProjection(
            ProjectRefProjectionRepository projectRefProjectionRepository,
            ProjectRefQueryRepository projectRefQueryRepository,
            RevisionProjectionApplier revisionProjectionApplier,
            ApplicationEventPublisher applicationEventPublisher
    ) {
        return ProjectProjectionFacade.projectProjection(projectRefProjectionRepository,
                projectRefQueryRepository,
                applicationEventPublisher,
                revisionProjectionApplier);
    }

    @Bean
    EnvironmentProjection environmentProjection(
            EnvironmentRefProjectionRepository environmentRefProjectionRepository,
            EnvironmentRefQueryRepository environmentRefQueryRepository,
            RevisionProjectionApplier revisionProjectionApplier,
            ApplicationEventPublisher applicationEventPublisher
    ) {
        return EnvironmentProjectionFacade.environmentProjection(environmentRefProjectionRepository,
                environmentRefQueryRepository,
                revisionProjectionApplier,
                applicationEventPublisher);
    }

    @Bean
    EnvironmentRefConsistency environmentRefConsistency(
            ConfigurationClient configurationClient,
            EnvironmentRefProjectionRepository environmentRefProjectionRepository,
            EnvironmentRefQueryRepository environmentRefQueryRepository
    ) {
        return EnvironmentProjectionFacade.environmentRefConsistency(configurationClient,
                environmentRefProjectionRepository,
                environmentRefQueryRepository);
    }

    @Bean
    ProjectRefConsistency projectRefConsistency(
            ConfigurationClient configurationClient,
            ProjectRefProjectionRepository projectRefProjectionRepository,
            ProjectRefQueryRepository projectRefQueryRepository
    ) {
        return ProjectProjectionFacade.projectRefResolver(configurationClient,
                projectRefProjectionRepository,
                projectRefQueryRepository);
    }

    @Bean
    ArchiveFeatureTogglesByEnvironmentUseCase archiveFeatureTogglesByEnvironmentUseCase(
            FeatureToggleCommandRepository featureToggleCommandRepository,
            FeatureToggleQueryRepository featureToggleQueryRepository,
            OutboxWriter outboxWriter
    ) {
        return FeatureToggleHandlerFacade.archiveFeatureTogglesByEnvironmentUseCase(
                featureToggleCommandRepository, featureToggleQueryRepository, outboxWriter
        );
    }

}

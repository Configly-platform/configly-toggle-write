package pl.feature.toggle.service.write.infrastructure;

import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import pl.feature.toggle.service.event.processing.api.RevisionProjectionApplier;
import pl.feature.toggle.service.model.security.actor.ActorProvider;
import pl.feature.toggle.service.model.security.correlation.CorrelationProvider;
import pl.feature.toggle.service.outbox.api.OutboxWriter;
import pl.feature.toggle.service.write.application.handler.FeatureToggleHandlerFacade;
import pl.feature.toggle.service.write.application.policy.FeatureTogglePolicyFacade;
import pl.feature.toggle.service.write.application.port.in.*;
import pl.feature.toggle.service.write.application.port.out.*;
import pl.feature.toggle.service.write.application.projection.environment.EnvironmentProjectionFacade;
import pl.feature.toggle.service.write.application.projection.project.ProjectProjectionFacade;

@Configuration
class FeatureToggleWriteApplicationConfig {

    @Bean
    CreateFeatureToggleUseCase createFeatureToggleUseCase(
            FeatureToggleCommandRepository featureToggleCommandRepository,
            FeatureTogglePolicyFacade featureTogglePolicyFacade,
            ProjectRefConsistency projectRefConsistency,
            EnvironmentRefConsistency environmentRefConsistency,
            OutboxWriter outboxWriter,
            ActorProvider actorProvider,
            CorrelationProvider correlationProvider
    ) {
        return FeatureToggleHandlerFacade.createFeatureToggleUseCase(featureToggleCommandRepository,
                featureTogglePolicyFacade,
                projectRefConsistency,
                environmentRefConsistency,
                outboxWriter,
                actorProvider,
                correlationProvider);
    }

    @Bean
    UpdateFeatureToggleUseCase updateFeatureToggleUseCase(
            FeatureToggleCommandRepository featureToggleCommandRepository,
            FeatureToggleQueryRepository featureToggleQueryRepository,
            FeatureTogglePolicyFacade featureTogglePolicyFacade,
            ProjectRefConsistency projectRefConsistency,
            EnvironmentRefConsistency environmentRefConsistency,
            OutboxWriter outboxWriter,
            ActorProvider actorProvider,
            CorrelationProvider correlationProvider
    ) {
        return FeatureToggleHandlerFacade.updateFeatureToggleUseCase(featureToggleCommandRepository,
                featureToggleQueryRepository,
                featureTogglePolicyFacade,
                projectRefConsistency,
                environmentRefConsistency,
                outboxWriter,
                actorProvider,
                correlationProvider);
    }

    @Bean
    ChangeFeatureToggleStatusUseCase changeFeatureToggleStatusUseCase(
            FeatureToggleCommandRepository featureToggleCommandRepository,
            FeatureToggleQueryRepository featureToggleQueryRepository,
            ProjectRefConsistency projectRefConsistency,
            EnvironmentRefConsistency environmentRefConsistency,
            OutboxWriter outboxWriter,
            ActorProvider actorProvider,
            CorrelationProvider correlationProvider
    ) {
        return FeatureToggleHandlerFacade.changeFeatureToggleStatusUseCase(featureToggleCommandRepository,
                featureToggleQueryRepository,
                projectRefConsistency,
                environmentRefConsistency,
                outboxWriter,
                actorProvider,
                correlationProvider
        );
    }

    @Bean
    ChangeFeatureToggleValueUseCase changeFeatureToggleValueUseCase(
            FeatureToggleCommandRepository featureToggleCommandRepository,
            FeatureToggleQueryRepository featureToggleQueryRepository,
            ProjectRefConsistency projectRefConsistency,
            EnvironmentRefConsistency environmentRefConsistency,
            OutboxWriter outboxWriter,
            ActorProvider actorProvider,
            CorrelationProvider correlationProvider
    ) {
        return FeatureToggleHandlerFacade.changeFeatureToggleValueUseCase(featureToggleCommandRepository,
                featureToggleQueryRepository,
                projectRefConsistency,
                environmentRefConsistency,
                outboxWriter,
                actorProvider,
                correlationProvider);
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

}

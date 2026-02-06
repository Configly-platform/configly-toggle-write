package pl.feature.toggle.service.write.infrastructure;

import org.springframework.context.ApplicationEventPublisher;
import pl.feature.toggle.service.model.security.actor.ActorProvider;
import pl.feature.toggle.service.model.security.correlation.CorrelationProvider;
import pl.feature.toggle.service.write.application.handler.FeatureToggleHandlerFacade;
import pl.feature.toggle.service.write.application.port.out.*;
import pl.feature.toggle.service.write.application.projection.environment.EnvironmentProjectionFacade;
import pl.feature.toggle.service.write.application.projection.project.ProjectProjectionFacade;
import pl.feature.toggle.service.write.application.policy.FeatureTogglePolicyFacade;
import pl.feature.toggle.service.write.application.port.in.*;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import pl.feature.toggle.service.outbox.api.OutboxWriter;

@Configuration
class FeatureToggleWriteApplicationConfig {

    @Bean
    CreateFeatureToggleUseCase createFeatureToggleUseCase(
            FeatureToggleCommandRepository featureToggleCommandRepository,
            FeatureTogglePolicyFacade featureTogglePolicyFacade,
            ProjectRefRepository projectRefRepository,
            EnvironmentRefRepository environmentRefRepository,
            OutboxWriter outboxWriter,
            ActorProvider actorProvider,
            CorrelationProvider correlationProvider
    ) {
        return FeatureToggleHandlerFacade.createFeatureToggleUseCase(featureToggleCommandRepository,
                featureTogglePolicyFacade,
                projectRefRepository,
                environmentRefRepository,
                outboxWriter,
                actorProvider,
                correlationProvider);
    }

    @Bean
    UpdateFeatureToggleUseCase updateFeatureToggleUseCase(
            FeatureToggleCommandRepository featureToggleCommandRepository,
            FeatureToggleQueryRepository featureToggleQueryRepository,
            FeatureTogglePolicyFacade featureTogglePolicyFacade,
            ProjectRefRepository projectRefRepository,
            EnvironmentRefRepository environmentRefRepository,
            OutboxWriter outboxWriter,
            ActorProvider actorProvider,
            CorrelationProvider correlationProvider
    ) {
        return FeatureToggleHandlerFacade.updateFeatureToggleUseCase(featureToggleCommandRepository,
                featureToggleQueryRepository,
                featureTogglePolicyFacade,
                projectRefRepository,
                environmentRefRepository,
                outboxWriter,
                actorProvider,
                correlationProvider);
    }

    @Bean
    ChangeFeatureToggleStatusUseCase changeFeatureToggleStatusUseCase(
            FeatureToggleCommandRepository featureToggleCommandRepository,
            FeatureToggleQueryRepository featureToggleQueryRepository,
            ProjectRefRepository projectRefRepository,
            EnvironmentRefRepository environmentRefRepository,
            OutboxWriter outboxWriter,
            ActorProvider actorProvider,
            CorrelationProvider correlationProvider
    ) {
        return FeatureToggleHandlerFacade.changeFeatureToggleStatusUseCase(featureToggleCommandRepository,
                featureToggleQueryRepository,
                projectRefRepository,
                environmentRefRepository,
                outboxWriter,
                actorProvider,
                correlationProvider
        );
    }

    @Bean
    ChangeFeatureToggleValueUseCase changeFeatureToggleValueUseCase(
            FeatureToggleCommandRepository featureToggleCommandRepository,
            FeatureToggleQueryRepository featureToggleQueryRepository,
            ProjectRefRepository projectRefRepository,
            EnvironmentRefRepository environmentRefRepository,
            OutboxWriter outboxWriter,
            ActorProvider actorProvider,
            CorrelationProvider correlationProvider
    ) {
        return FeatureToggleHandlerFacade.changeFeatureToggleValueUseCase(featureToggleCommandRepository,
                featureToggleQueryRepository,
                projectRefRepository,
                environmentRefRepository,
                outboxWriter,
                actorProvider,
                correlationProvider);
    }

    @Bean
    ProjectProjection projectProjection(
            ProjectRefRepository projectRefRepository,
            ApplicationEventPublisher applicationEventPublisher
    ) {
        return ProjectProjectionFacade.projectProjection(projectRefRepository, applicationEventPublisher);
    }

    @Bean
    EnvironmentProjection environmentProjection(
            EnvironmentRefRepository environmentRefRepository,
            ApplicationEventPublisher applicationEventPublisher
    ) {
        return EnvironmentProjectionFacade.environmentProjection(environmentRefRepository, applicationEventPublisher);
    }

    @Bean
    EnvironmentRefConsistency environmentRefConsistency(
            ConfigurationClient configurationClient,
            EnvironmentRefRepository environmentRefRepository
    ) {
        return EnvironmentProjectionFacade.environmentRefConsistency(configurationClient, environmentRefRepository);
    }

    @Bean
    ProjectRefConsistency projectRefConsistency(
            ConfigurationClient configurationClient,
            ProjectRefRepository projectRefRepository
    ) {
        return ProjectProjectionFacade.projectRefResolver(configurationClient, projectRefRepository);
    }

}

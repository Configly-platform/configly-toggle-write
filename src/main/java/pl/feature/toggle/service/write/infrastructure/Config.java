package pl.feature.toggle.service.write.infrastructure;

import pl.feature.toggle.service.model.security.actor.ActorProvider;
import pl.feature.toggle.service.model.security.correlation.CorrelationProvider;
import pl.feature.toggle.service.write.application.handler.FeatureToggleHandlerFacade;
import pl.feature.toggle.service.write.application.port.in.CreateFeatureToggleUseCase;
import pl.feature.toggle.service.write.application.port.in.ProjectEnvironmentProjection;
import pl.feature.toggle.service.write.application.port.in.UpdateFeatureToggleUseCase;
import pl.feature.toggle.service.write.application.port.out.EnvironmentRefRepository;
import pl.feature.toggle.service.write.application.port.out.FeatureToggleQueryRepository;
import pl.feature.toggle.service.write.application.port.out.ProjectRefRepository;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import pl.feature.toggle.service.outbox.api.OutboxWriter;

@Configuration("featureToggleWriteConfiguration")
class Config {

    @Bean
    CreateFeatureToggleUseCase createFeatureToggleUseCase(
            FeatureToggleQueryRepository featureToggleQueryRepository,
            ProjectRefRepository projectRefRepository,
            EnvironmentRefRepository environmentRefRepository,
            OutboxWriter outboxWriter,
            ActorProvider actorProvider,
            CorrelationProvider correlationProvider
    ) {
        return FeatureToggleHandlerFacade.createFeatureToggleUseCase(featureToggleQueryRepository, projectRefRepository, environmentRefRepository, outboxWriter, actorProvider, correlationProvider);
    }

    @Bean
    DeleteFeatureToggleUseCase deleteFeatureToggleUseCase(
            FeatureToggleQueryRepository featureToggleQueryRepository,
            OutboxWriter outboxWriter,
            ActorProvider actorProvider,
            CorrelationProvider correlationProvider
    ) {
        return FeatureToggleHandlerFacade.deleteFeatureToggleUseCase(featureToggleQueryRepository, outboxWriter, actorProvider, correlationProvider);
    }

    @Bean
    UpdateFeatureToggleUseCase updateFeatureToggleUseCase(
            FeatureToggleQueryRepository featureToggleQueryRepository,
            ProjectRefRepository projectRefRepository,
            EnvironmentRefRepository environmentRefRepository,
            OutboxWriter outboxWriter,
            ActorProvider actorProvider,
            CorrelationProvider correlationProvider
    ) {
        return FeatureToggleHandlerFacade.updateFeatureToggleUseCase(featureToggleQueryRepository, projectRefRepository, environmentRefRepository, outboxWriter, actorProvider, correlationProvider);
    }

    @Bean
    ProjectEnvironmentProjection projectEnvironmentProjectionUseCase(
            ProjectRefRepository projectRefRepository,
            EnvironmentRefRepository environmentRefRepository
    ) {
        return FeatureToggleHandlerFacade.projectEnvironmentProjectionUseCase(projectRefRepository, environmentRefRepository);
    }

}

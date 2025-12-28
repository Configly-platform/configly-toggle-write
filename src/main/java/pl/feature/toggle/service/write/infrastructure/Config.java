package pl.feature.toggle.service.write.infrastructure;

import pl.feature.toggle.service.model.security.actor.ActorProvider;
import pl.feature.toggle.service.model.security.correlation.CorrelationProvider;
import pl.feature.toggle.service.write.application.handler.FeatureToggleHandlerFacade;
import pl.feature.toggle.service.write.application.port.in.CreateFeatureToggleUseCase;
import pl.feature.toggle.service.write.application.port.in.DeleteFeatureToggleUseCase;
import pl.feature.toggle.service.write.application.port.in.ProjectEnvironmentProjectionUseCase;
import pl.feature.toggle.service.write.application.port.in.UpdateFeatureToggleUseCase;
import pl.feature.toggle.service.write.application.port.out.EnvironmentRepository;
import pl.feature.toggle.service.write.application.port.out.FeatureToggleRepository;
import pl.feature.toggle.service.write.application.port.out.ProjectRepository;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import pl.feature.toggle.service.outbox.api.OutboxWriter;

@Configuration("featureToggleWriteConfiguration")
class Config {

    @Bean
    CreateFeatureToggleUseCase createFeatureToggleUseCase(
            FeatureToggleRepository featureToggleRepository,
            ProjectRepository projectRepository,
            EnvironmentRepository environmentRepository,
            OutboxWriter outboxWriter,
            ActorProvider actorProvider,
            CorrelationProvider correlationProvider
    ) {
        return FeatureToggleHandlerFacade.createFeatureToggleUseCase(featureToggleRepository, projectRepository, environmentRepository, outboxWriter, actorProvider, correlationProvider);
    }

    @Bean
    DeleteFeatureToggleUseCase deleteFeatureToggleUseCase(
            FeatureToggleRepository featureToggleRepository,
            OutboxWriter outboxWriter,
            ActorProvider actorProvider,
            CorrelationProvider correlationProvider
    ) {
        return FeatureToggleHandlerFacade.deleteFeatureToggleUseCase(featureToggleRepository, outboxWriter, actorProvider, correlationProvider);
    }

    @Bean
    UpdateFeatureToggleUseCase updateFeatureToggleUseCase(
            FeatureToggleRepository featureToggleRepository,
            ProjectRepository projectRepository,
            EnvironmentRepository environmentRepository,
            OutboxWriter outboxWriter,
            ActorProvider actorProvider,
            CorrelationProvider correlationProvider
    ) {
        return FeatureToggleHandlerFacade.updateFeatureToggleUseCase(featureToggleRepository, projectRepository, environmentRepository, outboxWriter, actorProvider, correlationProvider);
    }

    @Bean
    ProjectEnvironmentProjectionUseCase projectEnvironmentProjectionUseCase(
            ProjectRepository projectRepository,
            EnvironmentRepository environmentRepository
    ) {
        return FeatureToggleHandlerFacade.projectEnvironmentProjectionUseCase(projectRepository, environmentRepository);
    }

}

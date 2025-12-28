package pl.feature.toggle.service.write.application.handler;

import pl.feature.toggle.service.model.security.actor.ActorProvider;
import pl.feature.toggle.service.model.security.correlation.CorrelationProvider;
import pl.feature.toggle.service.write.application.port.in.CreateFeatureToggleUseCase;
import pl.feature.toggle.service.write.application.port.in.DeleteFeatureToggleUseCase;
import pl.feature.toggle.service.write.application.port.in.ProjectEnvironmentProjectionUseCase;
import pl.feature.toggle.service.write.application.port.in.UpdateFeatureToggleUseCase;
import pl.feature.toggle.service.write.application.port.out.EnvironmentRepository;
import pl.feature.toggle.service.write.application.port.out.FeatureToggleRepository;
import pl.feature.toggle.service.write.application.port.out.ProjectRepository;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import pl.feature.toggle.service.outbox.api.OutboxWriter;

@AllArgsConstructor(access = AccessLevel.PRIVATE)
public final class FeatureToggleHandlerFacade {

    public static CreateFeatureToggleUseCase createFeatureToggleUseCase(
            FeatureToggleRepository featureToggleRepository,
            ProjectRepository projectRepository,
            EnvironmentRepository environmentRepository,
            OutboxWriter outboxWriter,
            ActorProvider actorProvider,
            CorrelationProvider correlationProvider
    ) {
        return new CreateFeatureToggleHandler(featureToggleRepository, projectRepository, environmentRepository, outboxWriter, actorProvider, correlationProvider);
    }

    public static UpdateFeatureToggleUseCase updateFeatureToggleUseCase(
            FeatureToggleRepository featureToggleRepository,
            ProjectRepository projectRepository,
            EnvironmentRepository environmentRepository,
            OutboxWriter outboxWriter,
            ActorProvider actorProvider,
            CorrelationProvider correlationProvider
    ) {
        return new UpdateFeatureToggleHandler(featureToggleRepository, projectRepository, environmentRepository, outboxWriter, actorProvider, correlationProvider);
    }

    public static DeleteFeatureToggleUseCase deleteFeatureToggleUseCase(
            FeatureToggleRepository featureToggleRepository,
            OutboxWriter outboxWriter,
            ActorProvider actorProvider,
            CorrelationProvider correlationProvider
    ) {
        return new DeleteFeatureToggleHandler(featureToggleRepository, outboxWriter, actorProvider, correlationProvider);
    }

    public static ProjectEnvironmentProjectionUseCase projectEnvironmentProjectionUseCase(
            ProjectRepository projectRepository,
            EnvironmentRepository environmentRepository
    ) {
        return new ProjectEnvironmentProjectionHandler(projectRepository, environmentRepository);
    }

}

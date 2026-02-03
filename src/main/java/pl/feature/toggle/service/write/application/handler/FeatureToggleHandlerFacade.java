package pl.feature.toggle.service.write.application.handler;

import pl.feature.toggle.service.model.security.actor.ActorProvider;
import pl.feature.toggle.service.model.security.correlation.CorrelationProvider;
import pl.feature.toggle.service.write.application.port.in.CreateFeatureToggleUseCase;
import pl.feature.toggle.service.write.application.port.in.ProjectEnvironmentProjection;
import pl.feature.toggle.service.write.application.port.in.UpdateFeatureToggleUseCase;
import pl.feature.toggle.service.write.application.port.out.EnvironmentRefRepository;
import pl.feature.toggle.service.write.application.port.out.FeatureToggleQueryRepository;
import pl.feature.toggle.service.write.application.port.out.ProjectRefRepository;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import pl.feature.toggle.service.outbox.api.OutboxWriter;

@AllArgsConstructor(access = AccessLevel.PRIVATE)
public final class FeatureToggleHandlerFacade {

    public static CreateFeatureToggleUseCase createFeatureToggleUseCase(
            FeatureToggleQueryRepository featureToggleQueryRepository,
            ProjectRefRepository projectRefRepository,
            EnvironmentRefRepository environmentRefRepository,
            OutboxWriter outboxWriter,
            ActorProvider actorProvider,
            CorrelationProvider correlationProvider
    ) {
        return new CreateFeatureToggleHandler(featureToggleQueryRepository, projectRefRepository, environmentRefRepository, outboxWriter, actorProvider, correlationProvider);
    }

    public static UpdateFeatureToggleUseCase updateFeatureToggleUseCase(
            FeatureToggleQueryRepository featureToggleQueryRepository,
            ProjectRefRepository projectRefRepository,
            EnvironmentRefRepository environmentRefRepository,
            OutboxWriter outboxWriter,
            ActorProvider actorProvider,
            CorrelationProvider correlationProvider
    ) {
        return new UpdateFeatureToggleHandler(featureToggleQueryRepository, projectRefRepository, environmentRefRepository, outboxWriter, actorProvider, correlationProvider);
    }

    public static DeleteFeatureToggleUseCase deleteFeatureToggleUseCase(
            FeatureToggleQueryRepository featureToggleQueryRepository,
            OutboxWriter outboxWriter,
            ActorProvider actorProvider,
            CorrelationProvider correlationProvider
    ) {
        return new DeleteFeatureToggleHandler(featureToggleQueryRepository, outboxWriter, actorProvider, correlationProvider);
    }

    public static ProjectEnvironmentProjection projectEnvironmentProjectionUseCase(
            ProjectRefRepository projectRefRepository,
            EnvironmentRefRepository environmentRefRepository
    ) {
        return new ProjectEnvironmentProjectionHandler(projectRefRepository, environmentRefRepository);
    }

}

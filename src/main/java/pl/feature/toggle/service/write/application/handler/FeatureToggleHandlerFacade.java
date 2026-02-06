package pl.feature.toggle.service.write.application.handler;

import pl.feature.toggle.service.model.security.actor.ActorProvider;
import pl.feature.toggle.service.model.security.correlation.CorrelationProvider;
import pl.feature.toggle.service.write.application.policy.FeatureTogglePolicyFacade;
import pl.feature.toggle.service.write.application.port.in.*;
import pl.feature.toggle.service.write.application.port.out.EnvironmentRefRepository;
import pl.feature.toggle.service.write.application.port.out.FeatureToggleCommandRepository;
import pl.feature.toggle.service.write.application.port.out.FeatureToggleQueryRepository;
import pl.feature.toggle.service.write.application.port.out.ProjectRefRepository;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import pl.feature.toggle.service.outbox.api.OutboxWriter;
import pl.feature.toggle.service.write.application.projection.environment.EnvironmentProjectionHandler;

@AllArgsConstructor(access = AccessLevel.PRIVATE)
public final class FeatureToggleHandlerFacade {

    public static CreateFeatureToggleUseCase createFeatureToggleUseCase(
            FeatureToggleCommandRepository featureToggleCommandRepository,
            FeatureTogglePolicyFacade featureTogglePolicyFacade,
            ProjectRefRepository projectRefRepository,
            EnvironmentRefRepository environmentRefRepository,
            OutboxWriter outboxWriter,
            ActorProvider actorProvider,
            CorrelationProvider correlationProvider
    ) {
        return new CreateFeatureToggleHandler(featureToggleCommandRepository,
                featureTogglePolicyFacade,
                projectRefRepository,
                environmentRefRepository,
                outboxWriter,
                actorProvider,
                correlationProvider);
    }

    public static UpdateFeatureToggleUseCase updateFeatureToggleUseCase(
            FeatureToggleCommandRepository featureToggleCommandRepository,
            FeatureToggleQueryRepository featureToggleQueryRepository,
            FeatureTogglePolicyFacade featureTogglePolicyFacade,
            ProjectRefRepository projectRefRepository,
            EnvironmentRefRepository environmentRefRepository,
            OutboxWriter outboxWriter,
            ActorProvider actorProvider,
            CorrelationProvider correlationProvider
    ) {
        return new UpdateFeatureToggleHandler(featureToggleCommandRepository,
                featureToggleQueryRepository,
                featureTogglePolicyFacade,
                projectRefRepository,
                environmentRefRepository,
                outboxWriter,
                actorProvider,
                correlationProvider);
    }

    public static ChangeFeatureToggleValueUseCase changeFeatureToggleValueUseCase(
            FeatureToggleCommandRepository featureToggleCommandRepository,
            FeatureToggleQueryRepository featureToggleQueryRepository,
            ProjectRefRepository projectRefRepository,
            EnvironmentRefRepository environmentRefRepository,
            OutboxWriter outboxWriter,
            ActorProvider actorProvider,
            CorrelationProvider correlationProvider
    ) {
        return new ChangeFeatureToggleValueHandler(featureToggleCommandRepository,
                featureToggleQueryRepository,
                projectRefRepository,
                environmentRefRepository,
                outboxWriter,
                actorProvider,
                correlationProvider);
    }

    public static ChangeFeatureToggleStatusUseCase changeFeatureToggleStatusUseCase(
            FeatureToggleCommandRepository featureToggleCommandRepository,
            FeatureToggleQueryRepository featureToggleQueryRepository,
            ProjectRefRepository projectRefRepository,
            EnvironmentRefRepository environmentRefRepository,
            OutboxWriter outboxWriter,
            ActorProvider actorProvider,
            CorrelationProvider correlationProvider
    ) {
        return new ChangeFeatureToggleStatusHandler(
                featureToggleCommandRepository,
                featureToggleQueryRepository,
                projectRefRepository,
                environmentRefRepository,
                outboxWriter,
                actorProvider,
                correlationProvider
        );
    }
}

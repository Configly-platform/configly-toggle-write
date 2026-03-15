package pl.feature.toggle.service.write.application.handler;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import pl.feature.toggle.service.model.security.actor.ActorProvider;
import pl.feature.toggle.service.model.security.correlation.CorrelationProvider;
import pl.feature.toggle.service.outbox.api.OutboxWriter;
import pl.feature.toggle.service.write.application.policy.FeatureTogglePolicyFacade;
import pl.feature.toggle.service.write.application.port.in.*;
import pl.feature.toggle.service.write.application.port.out.FeatureToggleCommandRepository;
import pl.feature.toggle.service.write.application.port.out.FeatureToggleQueryRepository;

@AllArgsConstructor(access = AccessLevel.PRIVATE)
public final class FeatureToggleHandlerFacade {

    public static CreateFeatureToggleUseCase createFeatureToggleUseCase(
            FeatureToggleCommandRepository featureToggleCommandRepository,
            FeatureTogglePolicyFacade featureTogglePolicyFacade,
            ProjectRefConsistency projectRefConsistency,
            EnvironmentRefConsistency environmentRefConsistency,
            OutboxWriter outboxWriter
    ) {
        return new CreateFeatureToggleHandler(featureToggleCommandRepository,
                featureTogglePolicyFacade,
                projectRefConsistency,
                environmentRefConsistency,
                outboxWriter);
    }

    public static UpdateFeatureToggleUseCase updateFeatureToggleUseCase(
            FeatureToggleCommandRepository featureToggleCommandRepository,
            FeatureToggleQueryRepository featureToggleQueryRepository,
            FeatureTogglePolicyFacade featureTogglePolicyFacade,
            ProjectRefConsistency projectRefConsistency,
            EnvironmentRefConsistency environmentRefConsistency,
            OutboxWriter outboxWriter
    ) {
        return new UpdateFeatureToggleHandler(featureToggleCommandRepository,
                featureToggleQueryRepository,
                featureTogglePolicyFacade,
                projectRefConsistency,
                environmentRefConsistency,
                outboxWriter);
    }

    public static ChangeFeatureToggleValueUseCase changeFeatureToggleValueUseCase(
            FeatureToggleCommandRepository featureToggleCommandRepository,
            FeatureToggleQueryRepository featureToggleQueryRepository,
            ProjectRefConsistency projectRefConsistency,
            EnvironmentRefConsistency environmentRefConsistency,
            OutboxWriter outboxWriter
    ) {
        return new ChangeFeatureToggleValueHandler(featureToggleCommandRepository,
                featureToggleQueryRepository,
                projectRefConsistency,
                environmentRefConsistency,
                outboxWriter);
    }

    public static ChangeFeatureToggleStatusUseCase changeFeatureToggleStatusUseCase(
            FeatureToggleCommandRepository featureToggleCommandRepository,
            FeatureToggleQueryRepository featureToggleQueryRepository,
            ProjectRefConsistency projectRefConsistency,
            EnvironmentRefConsistency environmentRefConsistency,
            OutboxWriter outboxWriter
    ) {
        return new ChangeFeatureToggleStatusHandler(
                featureToggleCommandRepository,
                featureToggleQueryRepository,
                projectRefConsistency,
                environmentRefConsistency,
                outboxWriter
        );
    }

    public static ArchiveFeatureTogglesByEnvironmentUseCase archiveFeatureTogglesByEnvironmentUseCase(
            FeatureToggleCommandRepository featureToggleCommandRepository,
            FeatureToggleQueryRepository featureToggleQueryRepository,
            OutboxWriter outboxWriter
    ) {
        return new ArchiveFeatureTogglesByEnvironmentHandler(featureToggleQueryRepository, featureToggleCommandRepository, outboxWriter);
    }
}

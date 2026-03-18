package pl.feature.toggle.service.write.application.handler;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import pl.feature.toggle.service.outbox.api.OutboxWriter;
import pl.feature.toggle.service.write.application.port.in.*;
import pl.feature.toggle.service.write.application.port.out.FeatureToggleCommandRepository;
import pl.feature.toggle.service.write.application.port.out.FeatureToggleQueryRepository;

@AllArgsConstructor(access = AccessLevel.PRIVATE)
public final class FeatureToggleHandlerFacade {

    public static CreateFeatureToggleUseCase createFeatureToggleUseCase(
            FeatureToggleCommandRepository featureToggleCommandRepository,
            ProjectRefConsistency projectRefConsistency,
            EnvironmentRefConsistency environmentRefConsistency,
            OutboxWriter outboxWriter
    ) {
        return new CreateFeatureToggleHandler(featureToggleCommandRepository,
                projectRefConsistency,
                environmentRefConsistency,
                outboxWriter);
    }

    public static UpdateFeatureToggleUseCase updateFeatureToggleUseCase(
            FeatureToggleCommandRepository featureToggleCommandRepository,
            FeatureToggleQueryRepository featureToggleQueryRepository,
            ProjectRefConsistency projectRefConsistency,
            EnvironmentRefConsistency environmentRefConsistency,
            OutboxWriter outboxWriter
    ) {
        return new UpdateFeatureToggleHandler(featureToggleCommandRepository,
                featureToggleQueryRepository,
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

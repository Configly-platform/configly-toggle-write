package pl.feature.toggle.service.write.application.handler;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.transaction.annotation.Transactional;
import pl.feature.toggle.service.model.featuretoggle.FeatureToggleStatus;
import pl.feature.toggle.service.outbox.api.OutboxEvent;
import pl.feature.toggle.service.outbox.api.OutboxWriter;
import pl.feature.toggle.service.write.application.port.in.ChangeFeatureToggleStatusUseCase;
import pl.feature.toggle.service.write.application.port.in.EnvironmentRefConsistency;
import pl.feature.toggle.service.write.application.port.in.ProjectRefConsistency;
import pl.feature.toggle.service.write.application.port.in.command.ChangeFeatureToggleStatusCommand;
import pl.feature.toggle.service.write.application.port.out.FeatureToggleCommandRepository;
import pl.feature.toggle.service.write.application.port.out.FeatureToggleQueryRepository;
import pl.feature.toggle.service.write.domain.featuretoggle.FeatureToggle;
import pl.feature.toggle.service.write.domain.featuretoggle.FeatureToggleUpdateResult;

import static pl.feature.toggle.service.contracts.topic.KafkaTopic.FEATURE_TOGGLE;
import static pl.feature.toggle.service.write.application.handler.EventMapper.createFeatureToggleStatusChangedEvent;

@AllArgsConstructor
@Slf4j
class ChangeFeatureToggleStatusHandler implements ChangeFeatureToggleStatusUseCase {

    private final FeatureToggleCommandRepository toggleCommandRepository;
    private final FeatureToggleQueryRepository toggleQueryRepository;
    private final ProjectRefConsistency projectRefConsistency;
    private final EnvironmentRefConsistency environmentRefConsistency;
    private final OutboxWriter outboxWriter;

    @Override
    @Transactional
    public void handle(ChangeFeatureToggleStatusCommand command) {
        var environmentRef = environmentRefConsistency.getTrusted(command.projectId(), command.environmentId());
        environmentRef.assertIsActive();
        var projectRef = projectRefConsistency.getTrusted(command.projectId());
        projectRef.assertIsActive();
        var featureToggle = toggleQueryRepository.getOrThrow(command.featureToggleId());

        var updateResult = changeStatus(featureToggle, command.newStatus());
        if (!updateResult.wasUpdated()) {
            return;
        }

        toggleCommandRepository.update(updateResult);

        var event = createFeatureToggleStatusChangedEvent(
                updateResult,
                command.actor(),
                command.correlationId());

        outboxWriter.write(OutboxEvent.of(projectRef.projectId().idAsString(), event, FEATURE_TOGGLE));
        log.info("Feature-Toggle status changed: id={}, oldStatus={}, newStatus={}", featureToggle.id().uuid(),
                featureToggle.status(), updateResult.featureToggle().status());
    }

    private FeatureToggleUpdateResult changeStatus(FeatureToggle featureToggle, FeatureToggleStatus newFeatureToggleStatus) {
        return switch (newFeatureToggleStatus) {
            case ACTIVE -> featureToggle.restore();
            case ARCHIVED -> featureToggle.archive();
        };
    }
}

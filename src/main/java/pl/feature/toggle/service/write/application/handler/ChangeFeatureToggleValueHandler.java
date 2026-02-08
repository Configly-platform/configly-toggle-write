package pl.feature.toggle.service.write.application.handler;

import lombok.AllArgsConstructor;
import org.springframework.transaction.annotation.Transactional;
import pl.feature.toggle.service.model.security.actor.ActorProvider;
import pl.feature.toggle.service.model.security.correlation.CorrelationProvider;
import pl.feature.toggle.service.outbox.api.OutboxWriter;
import pl.feature.toggle.service.write.application.port.in.ChangeFeatureToggleValueUseCase;
import pl.feature.toggle.service.write.application.port.in.EnvironmentRefConsistency;
import pl.feature.toggle.service.write.application.port.in.ProjectRefConsistency;
import pl.feature.toggle.service.write.application.port.in.command.ChangeFeatureToggleValueCommand;
import pl.feature.toggle.service.write.application.port.out.FeatureToggleCommandRepository;
import pl.feature.toggle.service.write.application.port.out.FeatureToggleQueryRepository;

import static pl.feature.toggle.service.contracts.topic.KafkaTopic.FEATURE_TOGGLE;
import static pl.feature.toggle.service.write.application.handler.EventMapper.createFeatureToggleValueChangedEvent;

@AllArgsConstructor
class ChangeFeatureToggleValueHandler implements ChangeFeatureToggleValueUseCase {

    private final FeatureToggleCommandRepository toggleCommandRepository;
    private final FeatureToggleQueryRepository toggleQueryRepository;
    private final ProjectRefConsistency projectRefConsistency;
    private final EnvironmentRefConsistency environmentRefConsistency;
    private final OutboxWriter outboxWriter;
    private final ActorProvider actorProvider;
    private final CorrelationProvider correlationProvider;

    @Override
    @Transactional
    public void handle(ChangeFeatureToggleValueCommand command) {
        var environmentRef = environmentRefConsistency.getTrusted(command.projectId(), command.environmentId());
        environmentRef.assertIsActive();
        var projectRef = projectRefConsistency.getTrusted(command.projectId());
        projectRef.assertIsActive();
        var featureToggle = toggleQueryRepository.getOrThrow(command.featureToggleId());

        var updateResult = featureToggle.changeValue(command.newValue());
        if (!updateResult.wasUpdated()) {
            return;
        }

        toggleCommandRepository.update(updateResult);

        var event = createFeatureToggleValueChangedEvent(updateResult,
                environmentRef,
                actorProvider.current(),
                correlationProvider.current());

        outboxWriter.write(event, FEATURE_TOGGLE.topic());
    }
}

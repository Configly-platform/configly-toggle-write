package com.configly.toggle.write.application.handler;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.transaction.annotation.Transactional;
import com.configly.outbox.api.OutboxEvent;
import com.configly.outbox.api.OutboxWriter;
import com.configly.toggle.write.application.port.in.EnvironmentRefConsistency;
import com.configly.toggle.write.application.port.in.ProjectRefConsistency;
import com.configly.toggle.write.application.port.in.UpdateFeatureToggleUseCase;
import com.configly.toggle.write.application.port.in.command.UpdateFeatureToggleCommand;
import com.configly.toggle.write.application.port.out.FeatureToggleCommandRepository;
import com.configly.toggle.write.application.port.out.FeatureToggleQueryRepository;

import static com.configly.contracts.topic.KafkaTopic.FEATURE_TOGGLE;
import static com.configly.toggle.write.application.handler.EventMapper.createFeatureToggleUpdatedEvent;

@AllArgsConstructor
@Slf4j
class UpdateFeatureToggleHandler implements UpdateFeatureToggleUseCase {

    private final FeatureToggleCommandRepository toggleCommandRepository;
    private final FeatureToggleQueryRepository toggleQueryRepository;
    private final ProjectRefConsistency projectRefConsistency;
    private final EnvironmentRefConsistency environmentRefConsistency;
    private final OutboxWriter outboxWriter;

    @Override
    @Transactional
    public void execute(UpdateFeatureToggleCommand command) {
        var environmentRef = environmentRefConsistency.getTrusted(command.projectId(), command.environmentId());
        environmentRef.assertIsActive();
        var projectRef = projectRefConsistency.getTrusted(command.projectId());
        projectRef.assertIsActive();

        var featureToggle = toggleQueryRepository.getOrThrow(command.featureToggleId());

        var updateResult = featureToggle.update(command.name(), command.description());
        if (!updateResult.wasUpdated()) {
            return;
        }

        toggleCommandRepository.update(updateResult);

        var event = createFeatureToggleUpdatedEvent(
                updateResult,
                command.actor(),
                command.correlationId()
        );
        outboxWriter.write(OutboxEvent.of(projectRef.projectId().idAsString(), event, FEATURE_TOGGLE));

        log.info("Feature-Toggle updated: id={}, newName={}, newDescription={}", featureToggle.id().uuid(),
                featureToggle.name().value(), featureToggle.description().value());
    }
}

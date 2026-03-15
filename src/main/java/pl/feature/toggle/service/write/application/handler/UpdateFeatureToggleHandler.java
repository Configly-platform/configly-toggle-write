package pl.feature.toggle.service.write.application.handler;

import lombok.extern.slf4j.Slf4j;
import pl.feature.toggle.service.model.security.actor.ActorProvider;
import pl.feature.toggle.service.model.security.correlation.CorrelationProvider;
import pl.feature.toggle.service.write.application.policy.FeatureTogglePolicyFacade;
import pl.feature.toggle.service.write.application.port.in.EnvironmentRefConsistency;
import pl.feature.toggle.service.write.application.port.in.ProjectRefConsistency;
import pl.feature.toggle.service.write.application.port.in.UpdateFeatureToggleUseCase;
import pl.feature.toggle.service.write.application.port.in.command.UpdateFeatureToggleCommand;
import pl.feature.toggle.service.write.application.port.out.FeatureToggleCommandRepository;
import pl.feature.toggle.service.write.application.port.out.FeatureToggleQueryRepository;
import lombok.AllArgsConstructor;
import org.springframework.transaction.annotation.Transactional;
import pl.feature.toggle.service.outbox.api.OutboxWriter;

import static pl.feature.toggle.service.write.application.handler.EventMapper.createFeatureToggleUpdatedEvent;
import static pl.feature.toggle.service.contracts.topic.KafkaTopic.FEATURE_TOGGLE;

@AllArgsConstructor
@Slf4j
class UpdateFeatureToggleHandler implements UpdateFeatureToggleUseCase {

    private final FeatureToggleCommandRepository toggleCommandRepository;
    private final FeatureToggleQueryRepository toggleQueryRepository;
    private final FeatureTogglePolicyFacade togglePolicyFacade;
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
        togglePolicyFacade.ensureUpdateAllowed(featureToggle, command.name());

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
        outboxWriter.write(event, FEATURE_TOGGLE.topic());

        log.info("Feature-Toggle updated: id={}, newName={}, newDescription={}", featureToggle.id().uuid(),
                featureToggle.name().value(), featureToggle.description().value());
    }
}

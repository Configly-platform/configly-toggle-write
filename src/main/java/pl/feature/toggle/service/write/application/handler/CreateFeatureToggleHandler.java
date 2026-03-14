package pl.feature.toggle.service.write.application.handler;

import lombok.AllArgsConstructor;
import org.springframework.transaction.annotation.Transactional;
import pl.feature.toggle.service.model.featuretoggle.FeatureToggleId;
import pl.feature.toggle.service.model.security.actor.ActorProvider;
import pl.feature.toggle.service.model.security.correlation.CorrelationProvider;
import pl.feature.toggle.service.outbox.api.OutboxWriter;
import pl.feature.toggle.service.write.application.policy.FeatureTogglePolicyFacade;
import pl.feature.toggle.service.write.application.port.in.CreateFeatureToggleUseCase;
import pl.feature.toggle.service.write.application.port.in.EnvironmentRefConsistency;
import pl.feature.toggle.service.write.application.port.in.ProjectRefConsistency;
import pl.feature.toggle.service.write.application.port.in.command.CreateFeatureToggleCommand;
import pl.feature.toggle.service.write.application.port.out.FeatureToggleCommandRepository;
import pl.feature.toggle.service.write.domain.featuretoggle.FeatureToggle;

import static pl.feature.toggle.service.contracts.topic.KafkaTopic.FEATURE_TOGGLE;
import static pl.feature.toggle.service.write.application.handler.EventMapper.createFeatureToggleCreatedEvent;

@AllArgsConstructor
class CreateFeatureToggleHandler implements CreateFeatureToggleUseCase {

    private final FeatureToggleCommandRepository toggleCommandRepository;
    private final FeatureTogglePolicyFacade togglePolicyFacade;
    private final ProjectRefConsistency projectRefConsistency;
    private final EnvironmentRefConsistency environmentRefConsistency;
    private final OutboxWriter outboxWriter;
    private final ActorProvider actorProvider;
    private final CorrelationProvider correlationProvider;

    @Override
    @Transactional
    public FeatureToggleId execute(CreateFeatureToggleCommand command) {
        var environmentRef = environmentRefConsistency.getTrusted(command.projectId(), command.environmentId());
        environmentRef.assertIsActive();
        var projectRef = projectRefConsistency.getTrusted(command.projectId());
        projectRef.assertIsActive();

        var featureToggle = FeatureToggle.create(command, environmentRef);
        togglePolicyFacade.ensureCreateAllowed(featureToggle);

        toggleCommandRepository.save(featureToggle);

        var event = createFeatureToggleCreatedEvent(
                featureToggle,
                actorProvider.current(),
                correlationProvider.current());

        outboxWriter.write(event, FEATURE_TOGGLE.topic());

        return featureToggle.id();
    }

}

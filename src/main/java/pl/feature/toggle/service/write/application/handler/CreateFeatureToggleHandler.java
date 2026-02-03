package pl.feature.toggle.service.write.application.handler;

import lombok.AllArgsConstructor;
import org.springframework.transaction.annotation.Transactional;
import pl.feature.toggle.service.model.featuretoggle.FeatureToggleId;
import pl.feature.toggle.service.model.security.actor.ActorProvider;
import pl.feature.toggle.service.model.security.correlation.CorrelationProvider;
import pl.feature.toggle.service.outbox.api.OutboxWriter;
import pl.feature.toggle.service.write.application.port.in.CreateFeatureToggleUseCase;
import pl.feature.toggle.service.write.application.port.in.command.CreateFeatureToggleCommand;
import pl.feature.toggle.service.write.application.port.out.EnvironmentRefRepository;
import pl.feature.toggle.service.write.application.port.out.FeatureToggleQueryRepository;
import pl.feature.toggle.service.write.application.port.out.ProjectRefRepository;
import pl.feature.toggle.service.write.domain.featuretoggle.FeatureToggle;

import static pl.feature.toggle.service.contracts.topic.KafkaTopic.FEATURE_TOGGLE;
import static pl.feature.toggle.service.write.application.handler.EventMapper.createFeatureToggleCreatedEvent;

@AllArgsConstructor
class CreateFeatureToggleHandler implements CreateFeatureToggleUseCase {

    private final FeatureToggleQueryRepository toggleRepository;
    private final ProjectRefRepository projectRefRepository;
    private final EnvironmentRefRepository environmentRefRepository;
    private final OutboxWriter outboxWriter;
    private final ActorProvider actorProvider;
    private final CorrelationProvider correlationProvider;

    @Override
    @Transactional
    public FeatureToggleId execute(final CreateFeatureToggleCommand command) {
        var projectRef = projectRefRepository.getOrThrow(command.projectId());
        var environmentRef = environmentRefRepository.getOrThrow(command.projectId(), command.environmentId());

        var featureToggle = FeatureToggle.create(command, project, environment);
        toggleRepository.save(featureToggle);

        var event = createFeatureToggleCreatedEvent(featureToggle, actorProvider.current(), correlationProvider.current());
        outboxWriter.write(event, FEATURE_TOGGLE.topic());

        return featureToggle.id();
    }

}

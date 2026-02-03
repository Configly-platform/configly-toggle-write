package pl.feature.toggle.service.write.application.handler;

import pl.feature.toggle.service.model.security.actor.ActorProvider;
import pl.feature.toggle.service.model.security.correlation.CorrelationProvider;
import pl.feature.toggle.service.write.application.port.in.UpdateFeatureToggleUseCase;
import pl.feature.toggle.service.write.application.port.in.command.UpdateFeatureToggleCommand;
import pl.feature.toggle.service.write.application.port.out.EnvironmentRefRepository;
import pl.feature.toggle.service.write.application.port.out.FeatureToggleQueryRepository;
import pl.feature.toggle.service.write.application.port.out.ProjectRefRepository;
import pl.feature.toggle.service.write.domain.reference.exception.EnvironmentNotFoundException;
import pl.feature.toggle.service.write.domain.featuretoggle.exception.FeatureToggleNotFoundException;
import pl.feature.toggle.service.write.domain.reference.exception.ProjectNotFoundException;
import pl.feature.toggle.service.write.domain.featuretoggle.FeatureToggle;
import lombok.AllArgsConstructor;
import org.springframework.transaction.annotation.Transactional;
import pl.feature.toggle.service.model.featuretoggle.FeatureToggleId;
import pl.feature.toggle.service.outbox.api.OutboxWriter;

import static pl.feature.toggle.service.write.application.handler.EventMapper.createFeatureToggleUpdatedEvent;
import static pl.feature.toggle.service.contracts.topic.KafkaTopic.FEATURE_TOGGLE;

@AllArgsConstructor
class UpdateFeatureToggleHandler implements UpdateFeatureToggleUseCase {

    private final FeatureToggleQueryRepository toggleRepository;
    private final ProjectRefRepository projectRefRepository;
    private final EnvironmentRefRepository environmentRefRepository;
    private final OutboxWriter outboxWriter;
    private final ActorProvider actorProvider;
    private final CorrelationProvider correlationProvider;

    @Override
    @Transactional
    public void execute(UpdateFeatureToggleCommand command) {
        validate(command);
        var featureToggle = loadFeatureToggle(command.featureToggleId());

        var updateResult = featureToggle.update(
                command.projectId(),
                command.environmentId(),
                command.name(),
                command.description(),
                command.value()
        );
        if (!updateResult.hasChanges()) {
            return;
        }

        toggleRepository.update(updateResult.updated());

        var event = createFeatureToggleUpdatedEvent(updateResult, actorProvider.current(), correlationProvider.current());
        outboxWriter.write(event, FEATURE_TOGGLE.topic());
    }


    private FeatureToggle loadFeatureToggle(FeatureToggleId featureToggleId) {
        return toggleRepository.findById(featureToggleId)
                .orElseThrow(() -> new FeatureToggleNotFoundException(featureToggleId));
    }

    private void loadEnvironment(UpdateFeatureToggleCommand command) {
        environmentRefRepository.findById(command.environmentId())
                .orElseThrow(() -> new EnvironmentNotFoundException(command.environmentId()));
    }

    private void loadProject(UpdateFeatureToggleCommand command) {
        projectRefRepository.findById(command.projectId())
                .orElseThrow(() -> new ProjectNotFoundException(command.projectId()));
    }

    private void validate(UpdateFeatureToggleCommand command) {
        loadEnvironment(command);
        loadProject(command);
    }
}

package pl.feature.toggle.service.write.application.handler;

import lombok.AllArgsConstructor;
import org.springframework.transaction.annotation.Transactional;
import pl.feature.toggle.service.model.security.actor.ActorProvider;
import pl.feature.toggle.service.model.security.correlation.CorrelationProvider;
import pl.feature.toggle.service.write.application.port.in.CreateFeatureToggleUseCase;
import pl.feature.toggle.service.write.application.port.in.command.CreateFeatureToggleCommand;
import pl.feature.toggle.service.write.application.port.out.EnvironmentRepository;
import pl.feature.toggle.service.write.application.port.out.FeatureToggleRepository;
import pl.feature.toggle.service.write.application.port.out.ProjectRepository;
import pl.feature.toggle.service.write.domain.environment.EnvironmentSnapshot;
import pl.feature.toggle.service.write.domain.environment.exception.EnvironmentNotFoundException;
import pl.feature.toggle.service.write.domain.featuretoggle.FeatureToggle;
import pl.feature.toggle.service.write.domain.featuretoggle.exception.FeatureToggleAlreadyExistsException;
import pl.feature.toggle.service.write.domain.project.ProjectSnapshot;
import pl.feature.toggle.service.write.domain.project.exception.ProjectNotFoundException;
import pl.feature.toggle.service.model.environment.EnvironmentId;
import pl.feature.toggle.service.model.featuretoggle.FeatureToggleId;
import pl.feature.toggle.service.model.project.ProjectId;
import pl.feature.toggle.service.outbox.api.OutboxWriter;

import static pl.feature.toggle.service.write.application.handler.FeatureToggleHandlerEventMapper.createFeatureToggleCreatedEvent;
import static pl.feature.toggle.service.contracts.topic.KafkaTopic.FEATURE_TOGGLE;

@AllArgsConstructor
class CreateFeatureToggleHandler implements CreateFeatureToggleUseCase {

    private final FeatureToggleRepository toggleRepository;
    private final ProjectRepository projectRepository;
    private final EnvironmentRepository environmentRepository;
    private final OutboxWriter outboxWriter;
    private final ActorProvider actorProvider;
    private final CorrelationProvider correlationProvider;

    @Override
    @Transactional
    public FeatureToggleId execute(final CreateFeatureToggleCommand command) {
        var project = loadProject(command.projectId());
        var environment = loadEnvironment(command.environmentId());

        validate(command);

        var featureToggle = FeatureToggle.create(command, project, environment);
        toggleRepository.save(featureToggle);

        var event = createFeatureToggleCreatedEvent(featureToggle, actorProvider.current(), correlationProvider.current());
        outboxWriter.write(event, FEATURE_TOGGLE.topic());

        return featureToggle.id();
    }

    private void validate(CreateFeatureToggleCommand command) {
        checkNameIsUnique(command);
    }

    private void checkNameIsUnique(CreateFeatureToggleCommand command) {
        if (toggleRepository.exists(command.name())) {
            throw new FeatureToggleAlreadyExistsException(command.name());
        }
    }

    private ProjectSnapshot loadProject(ProjectId projectId) {
        return projectRepository.findById(projectId)
                .orElseThrow(() -> new ProjectNotFoundException(projectId));
    }

    private EnvironmentSnapshot loadEnvironment(EnvironmentId environmentId) {
        return environmentRepository.findById(environmentId)
                .orElseThrow(() -> new EnvironmentNotFoundException(environmentId));
    }
}

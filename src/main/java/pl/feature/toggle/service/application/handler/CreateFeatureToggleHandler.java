package pl.feature.toggle.service.application.handler;

import com.ftaas.domain.environment.EnvironmentId;
import com.ftaas.domain.featuretoggle.FeatureToggleId;
import com.ftaas.domain.project.ProjectId;
import github.saqie.ftaasoutbox.api.OutboxWriter;
import pl.feature.toggle.service.application.port.in.CreateFeatureToggleUseCase;
import pl.feature.toggle.service.application.port.in.command.CreateFeatureToggleCommand;
import pl.feature.toggle.service.application.port.out.EnvironmentRepository;
import pl.feature.toggle.service.application.port.out.FeatureToggleRepository;
import pl.feature.toggle.service.application.port.out.ProjectRepository;
import pl.feature.toggle.service.domain.environment.EnvironmentSnapshot;
import pl.feature.toggle.service.domain.environment.exception.EnvironmentNotFoundException;
import pl.feature.toggle.service.domain.featuretoggle.exception.FeatureToggleAlreadyExistsException;
import pl.feature.toggle.service.domain.project.ProjectSnapshot;
import pl.feature.toggle.service.domain.project.exception.ProjectNotFoundException;
import pl.feature.toggle.service.domain.featuretoggle.FeatureToggle;
import lombok.AllArgsConstructor;
import org.springframework.transaction.annotation.Transactional;

import static com.ftaas.contracts.topic.KafkaTopic.FEATURE_TOGGLE;
import static pl.feature.toggle.service.application.handler.FeatureToggleHandlerEventMapper.createFeatureToggleCreatedEvent;

@AllArgsConstructor
class CreateFeatureToggleHandler implements CreateFeatureToggleUseCase {

    private final FeatureToggleRepository toggleRepository;
    private final ProjectRepository projectRepository;
    private final EnvironmentRepository environmentRepository;
    private final OutboxWriter outboxWriter;

    @Override
    @Transactional
    public FeatureToggleId execute(final CreateFeatureToggleCommand command) {
        var project = loadProject(command.projectId());
        var environment = loadEnvironment(command.environmentId());

        validate(command);

        var featureToggle = FeatureToggle.create(command, project, environment);
        toggleRepository.save(featureToggle);

        var event = createFeatureToggleCreatedEvent(featureToggle);
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

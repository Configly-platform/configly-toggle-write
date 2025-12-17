package pl.feature.toggle.service.application.handler;

import com.ftaas.domain.featuretoggle.FeatureToggleId;
import github.saqie.ftaasoutbox.api.OutboxWriter;
import pl.feature.toggle.service.application.port.in.UpdateFeatureToggleUseCase;
import pl.feature.toggle.service.application.port.in.command.UpdateFeatureToggleCommand;
import pl.feature.toggle.service.application.port.out.EnvironmentRepository;
import pl.feature.toggle.service.application.port.out.FeatureToggleRepository;
import pl.feature.toggle.service.application.port.out.ProjectRepository;
import pl.feature.toggle.service.domain.environment.exception.EnvironmentNotFoundException;
import pl.feature.toggle.service.domain.featuretoggle.exception.FeatureToggleNotFoundException;
import pl.feature.toggle.service.domain.project.exception.ProjectNotFoundException;
import pl.feature.toggle.service.domain.featuretoggle.FeatureToggle;
import lombok.AllArgsConstructor;
import org.springframework.transaction.annotation.Transactional;

import static com.ftaas.contracts.topic.KafkaTopic.FEATURE_TOGGLE;
import static pl.feature.toggle.service.application.handler.FeatureToggleHandlerEventMapper.createFeatureToggleUpdatedEvent;

@AllArgsConstructor
class UpdateFeatureToggleHandler implements UpdateFeatureToggleUseCase {

    private final FeatureToggleRepository toggleRepository;
    private final ProjectRepository projectRepository;
    private final EnvironmentRepository environmentRepository;
    private final OutboxWriter outboxWriter;

    @Override
    @Transactional
    public void execute(UpdateFeatureToggleCommand command) {
        validate(command);
        var featureToggle = loadFeatureToggle(command.featureToggleId());

        var featureToggleUpdated = featureToggle.update(
                command.projectId(),
                command.environmentId(),
                command.name(),
                command.description(),
                command.type(),
                command.value()
        );
        toggleRepository.update(featureToggleUpdated);

        var event = createFeatureToggleUpdatedEvent(featureToggleUpdated);
        outboxWriter.write(event, FEATURE_TOGGLE.topic());
    }


    private FeatureToggle loadFeatureToggle(FeatureToggleId featureToggleId) {
        return toggleRepository.findById(featureToggleId)
                .orElseThrow(() -> new FeatureToggleNotFoundException(featureToggleId));
    }

    private void loadEnvironment(UpdateFeatureToggleCommand command) {
        environmentRepository.findById(command.environmentId())
                .orElseThrow(() -> new EnvironmentNotFoundException(command.environmentId()));
    }

    private void loadProject(UpdateFeatureToggleCommand command) {
        projectRepository.findById(command.projectId())
                .orElseThrow(() -> new ProjectNotFoundException(command.projectId()));
    }

    private void validate(UpdateFeatureToggleCommand command) {
        loadEnvironment(command);
        loadProject(command);
    }
}

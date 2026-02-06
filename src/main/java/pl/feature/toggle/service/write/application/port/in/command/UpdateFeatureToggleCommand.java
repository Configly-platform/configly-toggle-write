package pl.feature.toggle.service.write.application.port.in.command;

import pl.feature.toggle.service.model.environment.EnvironmentId;
import pl.feature.toggle.service.model.featuretoggle.FeatureToggleDescription;
import pl.feature.toggle.service.model.featuretoggle.FeatureToggleId;
import pl.feature.toggle.service.model.featuretoggle.FeatureToggleName;
import pl.feature.toggle.service.model.project.ProjectId;
import pl.feature.toggle.service.write.infrastructure.in.rest.dto.UpdateFeatureToggleDto;

public record UpdateFeatureToggleCommand(
        FeatureToggleId featureToggleId,
        ProjectId projectId,
        EnvironmentId environmentId,
        FeatureToggleName name,
        FeatureToggleDescription description
) {

    public static UpdateFeatureToggleCommand from(
            String featureToggleId,
            String projectId,
            String environmentId,
            UpdateFeatureToggleDto dto) {
        return new UpdateFeatureToggleCommand(
                FeatureToggleId.create(featureToggleId),
                ProjectId.create(projectId),
                EnvironmentId.create(environmentId),
                FeatureToggleName.create(dto.name()),
                FeatureToggleDescription.create(dto.description()));
    }

}

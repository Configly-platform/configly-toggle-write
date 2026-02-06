package pl.feature.toggle.service.write.application.port.in.command;

import pl.feature.toggle.service.model.environment.EnvironmentId;
import pl.feature.toggle.service.model.featuretoggle.FeatureToggleId;
import pl.feature.toggle.service.model.project.ProjectId;
import pl.feature.toggle.service.value.raw.FeatureToggleRawValue;
import pl.feature.toggle.service.write.infrastructure.in.rest.dto.ChangeFeatureToggleValueDto;

public record ChangeFeatureToggleValueCommand(
        ProjectId projectId,
        EnvironmentId environmentId,
        FeatureToggleId featureToggleId,
        FeatureToggleRawValue newValue
) {

    public static ChangeFeatureToggleValueCommand from(
            String projectId,
            String environmentId,
            String featureToggleId,
            ChangeFeatureToggleValueDto dto
    ) {
        return new ChangeFeatureToggleValueCommand(
                ProjectId.create(projectId),
                EnvironmentId.create(environmentId),
                FeatureToggleId.create(featureToggleId),
                FeatureToggleRawValue.of(dto.value())
        );
    }
}

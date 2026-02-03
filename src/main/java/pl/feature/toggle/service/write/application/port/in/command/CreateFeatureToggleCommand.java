package pl.feature.toggle.service.write.application.port.in.command;

import pl.feature.toggle.service.model.environment.EnvironmentId;
import pl.feature.toggle.service.model.featuretoggle.FeatureToggleDescription;
import pl.feature.toggle.service.model.featuretoggle.FeatureToggleName;
import pl.feature.toggle.service.model.featuretoggle.value.FeatureToggleValue;
import pl.feature.toggle.service.model.featuretoggle.value.FeatureToggleValueBuilder;
import pl.feature.toggle.service.model.project.ProjectId;
import pl.feature.toggle.service.write.infrastructure.in.rest.dto.CreateFeatureToggleDto;

public record CreateFeatureToggleCommand(
        ProjectId projectId,
        EnvironmentId environmentId,
        FeatureToggleName name,
        FeatureToggleDescription description,
        FeatureToggleValue value
) {

    public static CreateFeatureToggleCommand from(CreateFeatureToggleDto dto) {
        return new CreateFeatureToggleCommand(
                ProjectId.create(dto.projectId()),
                EnvironmentId.create(dto.environmentId()),
                FeatureToggleName.create(dto.name()),
                FeatureToggleDescription.create(dto.description()),
                FeatureToggleValueBuilder.from(dto.value())
        );
    }
}

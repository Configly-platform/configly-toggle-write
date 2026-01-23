package pl.feature.toggle.service.write.application.port.in.command;

import pl.feature.toggle.service.model.featuretoggle.value.*;
import pl.feature.toggle.service.write.infrastructure.in.rest.dto.FeatureToggleSnapshotDto;
import pl.feature.toggle.service.model.environment.EnvironmentId;
import pl.feature.toggle.service.model.featuretoggle.*;
import pl.feature.toggle.service.model.project.ProjectId;

import java.util.UUID;

public record CreateFeatureToggleCommand(
        ProjectId projectId,
        EnvironmentId environmentId,
        FeatureToggleName name,
        FeatureToggleDescription description,
        FeatureToggleValue value
) {

    public static CreateFeatureToggleCommand from(FeatureToggleSnapshotDto dto) {
        return new CreateFeatureToggleCommand(
                ProjectId.create(dto.projectId()),
                EnvironmentId.create(dto.environmentId()),
                FeatureToggleName.create(dto.name()),
                FeatureToggleDescription.create(dto.description()),
                FeatureToggleValueBuilder.from(dto.value())
        );
    }

    public static CreateFeatureToggleCommand from(UUID projectId, UUID environmentId, String name, String description, Object value) {
        return new CreateFeatureToggleCommand(
                ProjectId.create(projectId),
                EnvironmentId.create(environmentId),
                FeatureToggleName.create(name),
                FeatureToggleDescription.create(description),
                FeatureToggleValueBuilder.from(value)
        );
    }

}

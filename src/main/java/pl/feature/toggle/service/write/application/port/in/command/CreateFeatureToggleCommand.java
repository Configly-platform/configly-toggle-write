package pl.feature.toggle.service.write.application.port.in.command;

import pl.feature.toggle.service.model.featuretoggle.value.FeatureToggleType;
import pl.feature.toggle.service.model.featuretoggle.value.FeatureToggleValue;
import pl.feature.toggle.service.model.featuretoggle.value.FeatureToggleValueRecognizer;
import pl.feature.toggle.service.model.featuretoggle.value.FeatureToggleValueSpec;
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
        FeatureToggleType type,
        FeatureToggleValue value
) {

    public static CreateFeatureToggleCommand from(FeatureToggleSnapshotDto dto) {
        return new CreateFeatureToggleCommand(
                ProjectId.create(dto.projectId()),
                EnvironmentId.create(dto.environmentId()),
                FeatureToggleName.create(dto.name()),
                FeatureToggleDescription.create(dto.description()),
                dto.type(),
                FeatureToggleValueRecognizer.from(FeatureToggleValueSpec.create(dto.value(), dto.type()))
        );
    }

    public static CreateFeatureToggleCommand from(UUID projectId, UUID environmentId, String name, String description, FeatureToggleType type, String value) {
        return new CreateFeatureToggleCommand(
                ProjectId.create(projectId),
                EnvironmentId.create(environmentId),
                FeatureToggleName.create(name),
                FeatureToggleDescription.create(description),
                type,
                FeatureToggleValueRecognizer.from(FeatureToggleValueSpec.create(value, type))
        );
    }

}

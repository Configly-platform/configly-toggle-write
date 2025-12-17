package pl.feature.toggle.service.application.port.in.command;

import com.ftaas.domain.environment.EnvironmentId;
import com.ftaas.domain.featuretoggle.*;
import com.ftaas.domain.project.ProjectId;
import pl.feature.toggle.service.infrastructure.in.rest.dto.FeatureToggleSnapshotDto;

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
                FeatureToggleValueRecognizer.from(dto.type(), dto.value())
        );
    }

    public static CreateFeatureToggleCommand from(UUID projectId, UUID environmentId, String name, String description, FeatureToggleType type, String value) {
        return new CreateFeatureToggleCommand(
                ProjectId.create(projectId),
                EnvironmentId.create(environmentId),
                FeatureToggleName.create(name),
                FeatureToggleDescription.create(description),
                type,
                FeatureToggleValueRecognizer.from(type, value)
        );
    }

}

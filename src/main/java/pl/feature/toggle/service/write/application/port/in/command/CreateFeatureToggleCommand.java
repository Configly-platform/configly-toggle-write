package pl.feature.toggle.service.write.application.port.in.command;

import pl.feature.toggle.service.model.environment.EnvironmentId;
import pl.feature.toggle.service.model.featuretoggle.FeatureToggleDescription;
import pl.feature.toggle.service.model.featuretoggle.FeatureToggleName;
import pl.feature.toggle.service.model.project.ProjectId;
import pl.feature.toggle.service.web.actor.Actor;
import pl.feature.toggle.service.web.correlation.CorrelationId;
import pl.feature.toggle.service.value.FeatureToggleValueType;
import pl.feature.toggle.service.value.FeatureToggleValueSnapshot;
import pl.feature.toggle.service.write.infrastructure.in.rest.dto.CreateFeatureToggleDto;

public record CreateFeatureToggleCommand(
        ProjectId projectId,
        EnvironmentId environmentId,
        FeatureToggleName name,
        FeatureToggleDescription description,
        FeatureToggleValueSnapshot rawValue,
        FeatureToggleValueType valueType,
        Actor actor,
        CorrelationId correlationId
) {

    public static CreateFeatureToggleCommand from(String projectId, String environmentId, CreateFeatureToggleDto dto,
                                                  Actor actor, CorrelationId correlationId) {
        return new CreateFeatureToggleCommand(
                ProjectId.create(projectId),
                EnvironmentId.create(environmentId),
                FeatureToggleName.create(dto.name()),
                FeatureToggleDescription.create(dto.description()),
                FeatureToggleValueSnapshot.of(dto.value()),
                FeatureToggleValueType.fromString(dto.type()),
                actor,
                correlationId
        );
    }
}

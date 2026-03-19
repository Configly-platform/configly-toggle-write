package pl.feature.toggle.service.write.application.port.in.command;

import pl.feature.toggle.service.model.environment.EnvironmentId;
import pl.feature.toggle.service.model.featuretoggle.FeatureToggleId;
import pl.feature.toggle.service.model.project.ProjectId;
import pl.feature.toggle.service.web.actor.Actor;
import pl.feature.toggle.service.web.correlation.CorrelationId;
import pl.feature.toggle.service.value.FeatureToggleValueSnapshot;
import pl.feature.toggle.service.write.infrastructure.in.rest.dto.ChangeFeatureToggleValueDto;

public record ChangeFeatureToggleValueCommand(
        ProjectId projectId,
        EnvironmentId environmentId,
        FeatureToggleId featureToggleId,
        FeatureToggleValueSnapshot newValue,
        Actor actor,
        CorrelationId correlationId
) {

    public static ChangeFeatureToggleValueCommand from(
            String projectId,
            String environmentId,
            String featureToggleId,
            ChangeFeatureToggleValueDto dto,
            Actor actor,
            CorrelationId correlationId
    ) {
        return new ChangeFeatureToggleValueCommand(
                ProjectId.create(projectId),
                EnvironmentId.create(environmentId),
                FeatureToggleId.create(featureToggleId),
                FeatureToggleValueSnapshot.of(dto.value()),
                actor,
                correlationId
        );
    }
}

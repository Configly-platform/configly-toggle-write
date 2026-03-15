package pl.feature.toggle.service.write.application.port.in.command;

import pl.feature.toggle.service.model.environment.EnvironmentId;
import pl.feature.toggle.service.model.featuretoggle.FeatureToggleId;
import pl.feature.toggle.service.model.featuretoggle.FeatureToggleStatus;
import pl.feature.toggle.service.model.project.ProjectId;
import pl.feature.toggle.service.model.security.actor.Actor;
import pl.feature.toggle.service.model.security.correlation.CorrelationId;

public record ChangeFeatureToggleStatusCommand(
        ProjectId projectId,
        EnvironmentId environmentId,
        FeatureToggleId featureToggleId,
        FeatureToggleStatus newStatus,
        Actor actor,
        CorrelationId correlationId
) {

    public static ChangeFeatureToggleStatusCommand create(
            String projectId,
            String environmentId,
            String featureToggleId,
            String newStatus,
            Actor actor,
            CorrelationId correlationId
    ) {
        return new ChangeFeatureToggleStatusCommand(
                ProjectId.create(projectId),
                EnvironmentId.create(environmentId),
                FeatureToggleId.create(featureToggleId),
                FeatureToggleStatus.valueOf(newStatus),
                actor,
                correlationId
        );
    }
}

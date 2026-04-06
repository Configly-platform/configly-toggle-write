package com.configly.toggle.write.application.port.in.command;

import com.configly.model.environment.EnvironmentId;
import com.configly.model.featuretoggle.FeatureToggleId;
import com.configly.model.featuretoggle.FeatureToggleStatus;
import com.configly.model.project.ProjectId;
import com.configly.web.model.actor.Actor;
import com.configly.web.model.correlation.CorrelationId;

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

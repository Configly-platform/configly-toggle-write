package com.configly.toggle.write.application.port.in.command;

import com.configly.model.environment.EnvironmentId;
import com.configly.model.featuretoggle.FeatureToggleDescription;
import com.configly.model.featuretoggle.FeatureToggleId;
import com.configly.model.featuretoggle.FeatureToggleName;
import com.configly.model.project.ProjectId;
import com.configly.web.model.actor.Actor;
import com.configly.web.model.correlation.CorrelationId;
import com.configly.toggle.write.infrastructure.in.rest.dto.UpdateFeatureToggleDto;

public record UpdateFeatureToggleCommand(
        FeatureToggleId featureToggleId,
        ProjectId projectId,
        EnvironmentId environmentId,
        FeatureToggleName name,
        FeatureToggleDescription description,
        Actor actor,
        CorrelationId correlationId
) {

    public static UpdateFeatureToggleCommand from(
            String featureToggleId,
            String projectId,
            String environmentId,
            UpdateFeatureToggleDto dto,
            Actor actor,
            CorrelationId correlationId
    ) {
        return new UpdateFeatureToggleCommand(
                FeatureToggleId.create(featureToggleId),
                ProjectId.create(projectId),
                EnvironmentId.create(environmentId),
                FeatureToggleName.create(dto.name()),
                FeatureToggleDescription.create(dto.description()),
                actor,
                correlationId
        );
    }

}

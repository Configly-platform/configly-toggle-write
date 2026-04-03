package com.configly.toggle.write.application.port.in.command;

import com.configly.model.environment.EnvironmentId;
import com.configly.model.featuretoggle.FeatureToggleId;
import com.configly.model.project.ProjectId;
import com.configly.web.actor.Actor;
import com.configly.web.correlation.CorrelationId;
import com.configly.value.FeatureToggleValueSnapshot;
import com.configly.toggle.write.infrastructure.in.rest.dto.ChangeFeatureToggleValueDto;

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

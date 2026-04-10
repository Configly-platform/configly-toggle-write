package com.configly.toggle.write.application.port.in.command;

import com.configly.model.environment.EnvironmentId;
import com.configly.model.featuretoggle.FeatureToggleDescription;
import com.configly.model.featuretoggle.FeatureToggleName;
import com.configly.model.project.ProjectId;
import com.configly.web.model.actor.Actor;
import com.configly.web.model.correlation.CorrelationId;
import com.configly.value.toggle.FeatureToggleValueType;
import com.configly.value.toggle.FeatureToggleValueSnapshot;
import com.configly.toggle.write.infrastructure.in.rest.dto.CreateFeatureToggleDto;

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

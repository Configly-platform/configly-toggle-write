package com.configly.toggle.write.application.projection.environment.event;

import com.configly.model.environment.EnvironmentId;
import com.configly.model.project.ProjectId;
import com.configly.web.correlation.CorrelationId;

public record RebuildEnvironmentRefRequested(
        ProjectId projectId,
        EnvironmentId environmentId,
        CorrelationId correlationId
) {
}

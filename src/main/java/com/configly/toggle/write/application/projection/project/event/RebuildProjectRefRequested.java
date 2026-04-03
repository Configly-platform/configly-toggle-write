package com.configly.toggle.write.application.projection.project.event;

import com.configly.model.project.ProjectId;
import com.configly.web.correlation.CorrelationId;

public record RebuildProjectRefRequested(
        ProjectId projectId,
        CorrelationId correlationId
) {
}

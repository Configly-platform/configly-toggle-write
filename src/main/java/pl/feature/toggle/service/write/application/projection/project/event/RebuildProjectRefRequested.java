package pl.feature.toggle.service.write.application.projection.project.event;

import pl.feature.toggle.service.model.project.ProjectId;
import pl.feature.toggle.service.model.security.correlation.CorrelationId;

public record RebuildProjectRefRequested(
        ProjectId projectId,
        CorrelationId correlationId
) {
}

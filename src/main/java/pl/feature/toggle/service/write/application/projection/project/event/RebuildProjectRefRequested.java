package pl.feature.toggle.service.write.application.projection.project.event;

import pl.feature.toggle.service.model.project.ProjectId;
import pl.feature.toggle.service.web.correlation.CorrelationId;

public record RebuildProjectRefRequested(
        ProjectId projectId,
        CorrelationId correlationId
) {
}

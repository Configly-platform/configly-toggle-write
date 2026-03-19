package pl.feature.toggle.service.write.application.projection.environment.event;

import pl.feature.toggle.service.model.environment.EnvironmentId;
import pl.feature.toggle.service.model.project.ProjectId;
import pl.feature.toggle.service.web.correlation.CorrelationId;

public record RebuildEnvironmentRefRequested(
        ProjectId projectId,
        EnvironmentId environmentId,
        CorrelationId correlationId
) {
}

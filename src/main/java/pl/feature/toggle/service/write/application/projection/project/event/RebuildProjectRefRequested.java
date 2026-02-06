package pl.feature.toggle.service.write.application.projection.project.event;

import pl.feature.toggle.service.model.project.ProjectId;

public record RebuildProjectRefRequested(
        ProjectId projectId
) {
}

package pl.feature.toggle.service.domain.environment;

import pl.feature.toggle.service.contracts.event.projects.EnvironmentCreated;
import pl.feature.toggle.service.domain.environment.exception.EnvironmentNotAssignedToProjectException;
import pl.feature.toggle.service.model.environment.EnvironmentId;
import pl.feature.toggle.service.model.project.ProjectId;

public record EnvironmentSnapshot(
        EnvironmentId id,
        ProjectId projectId
) {

    public static EnvironmentSnapshot from(EnvironmentCreated event) {
        return new EnvironmentSnapshot(EnvironmentId.create(event.environmentId()), ProjectId.create(event.projectId()));
    }

    public static EnvironmentSnapshot create(ProjectId projectId) {
        return new EnvironmentSnapshot(EnvironmentId.create(), projectId);
    }

    public void mustBelongTo(ProjectId projectId) {
        if (!projectId.equals(this.projectId)) {
            throw new EnvironmentNotAssignedToProjectException(id, projectId);
        }
    }
}

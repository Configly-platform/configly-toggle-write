package pl.feature.toggle.service.domain.environment;

import com.ftaas.contracts.event.projects.EnvironmentCreated;
import com.ftaas.domain.environment.EnvironmentId;
import com.ftaas.domain.project.ProjectId;
import pl.feature.toggle.service.domain.environment.exception.EnvironmentNotAssignedToProjectException;

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

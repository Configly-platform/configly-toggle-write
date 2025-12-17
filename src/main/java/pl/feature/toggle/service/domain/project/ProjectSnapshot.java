package pl.feature.toggle.service.domain.project;

import com.ftaas.contracts.event.projects.ProjectCreated;
import com.ftaas.domain.project.ProjectId;

public record ProjectSnapshot(
        ProjectId id
) {

    public static ProjectSnapshot from(ProjectCreated projectCreated) {
        return new ProjectSnapshot(ProjectId.create(projectCreated.projectId()));
    }

    public static ProjectSnapshot create() {
        return new ProjectSnapshot(ProjectId.create());
    }

}

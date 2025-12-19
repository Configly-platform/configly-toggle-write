package pl.feature.toggle.service.write.domain.project;


import pl.feature.toggle.service.contracts.event.projects.ProjectCreated;
import pl.feature.toggle.service.model.project.ProjectId;

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

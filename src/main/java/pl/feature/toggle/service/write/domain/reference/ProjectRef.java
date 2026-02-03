package pl.feature.toggle.service.write.domain.reference;

import pl.feature.toggle.service.contracts.event.project.ProjectCreated;
import pl.feature.toggle.service.model.project.ProjectId;

public record ProjectRef(
        ProjectId projectId,
        ProjectStatus status
) {
    public static ProjectRef from(ProjectCreated event) {
        return new ProjectRef(ProjectId.create(event.projectId()), ProjectStatus.valueOf(event.status()));
    }
}

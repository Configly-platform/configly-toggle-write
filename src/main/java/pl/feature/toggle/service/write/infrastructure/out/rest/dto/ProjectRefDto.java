package pl.feature.toggle.service.write.infrastructure.out.rest.dto;

import pl.feature.toggle.service.model.Revision;
import pl.feature.toggle.service.model.project.ProjectId;
import pl.feature.toggle.service.model.project.ProjectStatus;
import pl.feature.toggle.service.write.domain.reference.ProjectRef;

public record ProjectRefDto(
        String projectId,
        String status,
        Long revision
) {

    public ProjectRef toReference() {
        return new ProjectRef(
                ProjectId.create(projectId),
                ProjectStatus.valueOf(status),
                Revision.from(revision),
                true
        );
    }
}

package pl.feature.toggle.service.write.infrastructure.out.rest.dto;

import pl.feature.toggle.service.model.Revision;
import pl.feature.toggle.service.model.environment.EnvironmentId;
import pl.feature.toggle.service.model.environment.EnvironmentStatus;
import pl.feature.toggle.service.model.project.ProjectId;
import pl.feature.toggle.service.write.domain.reference.EnvironmentRef;

public record EnvironmentRefDto(
        String environmentId,
        String projectId,
        String status,
        Long lastRevision
) {

    public EnvironmentRef toReference() {
        return new EnvironmentRef(
                EnvironmentId.create(environmentId),
                ProjectId.create(projectId),
                EnvironmentStatus.valueOf(status),
                Revision.from(lastRevision),
                true
        );
    }
}

package com.configly.toggle.write.infrastructure.out.rest.dto;

import com.configly.model.Revision;
import com.configly.model.environment.EnvironmentId;
import com.configly.model.environment.EnvironmentStatus;
import com.configly.model.project.ProjectId;
import com.configly.toggle.write.domain.reference.EnvironmentRef;

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

package com.configly.toggle.write.infrastructure.out.rest.dto;

import com.configly.model.Revision;
import com.configly.model.project.ProjectId;
import com.configly.model.project.ProjectStatus;
import com.configly.toggle.write.domain.reference.ProjectRef;

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

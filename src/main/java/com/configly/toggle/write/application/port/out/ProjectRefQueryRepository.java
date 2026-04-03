package com.configly.toggle.write.application.port.out;

import com.configly.model.project.ProjectId;
import com.configly.toggle.write.domain.reference.ProjectRef;

import java.util.Optional;

public interface ProjectRefQueryRepository {

    Optional<ProjectRef> find(ProjectId projectId);

    Optional<ProjectRef> findConsistent(ProjectId projectId);
}

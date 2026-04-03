package com.configly.toggle.write.application.port.out;

import com.configly.model.environment.EnvironmentId;
import com.configly.model.project.ProjectId;
import com.configly.toggle.write.domain.reference.EnvironmentRef;

import java.util.Optional;

public interface EnvironmentRefQueryRepository {

    Optional<EnvironmentRef> find(ProjectId projectId, EnvironmentId environmentId);

    Optional<EnvironmentRef> findConsistent(ProjectId projectId, EnvironmentId environmentId);

}

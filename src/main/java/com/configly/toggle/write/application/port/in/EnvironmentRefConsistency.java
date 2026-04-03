package com.configly.toggle.write.application.port.in;

import com.configly.model.environment.EnvironmentId;
import com.configly.model.project.ProjectId;
import com.configly.toggle.write.domain.reference.EnvironmentRef;

public interface EnvironmentRefConsistency {
    EnvironmentRef getTrusted(ProjectId projectId, EnvironmentId environmentId);

    void rebuild(ProjectId projectId, EnvironmentId environmentId);
}

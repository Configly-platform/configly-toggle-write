package com.configly.toggle.write.application.port.out;

import com.configly.model.environment.EnvironmentId;
import com.configly.model.project.ProjectId;
import com.configly.toggle.write.domain.reference.EnvironmentRef;
import com.configly.toggle.write.domain.reference.ProjectRef;

public interface ConfigurationClient {

    ProjectRef fetchProject(ProjectId projectId);

    EnvironmentRef fetchEnvironment(ProjectId projectId, EnvironmentId environmentId);

}

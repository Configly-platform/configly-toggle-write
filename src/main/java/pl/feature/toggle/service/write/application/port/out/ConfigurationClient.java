package pl.feature.toggle.service.write.application.port.out;

import pl.feature.toggle.service.model.environment.EnvironmentId;
import pl.feature.toggle.service.model.project.ProjectId;
import pl.feature.toggle.service.write.domain.reference.EnvironmentRef;
import pl.feature.toggle.service.write.domain.reference.ProjectRef;

public interface ConfigurationClient {

    ProjectRef fetchProject(ProjectId projectId);

    EnvironmentRef fetchEnvironment(ProjectId projectId, EnvironmentId environmentId);

}

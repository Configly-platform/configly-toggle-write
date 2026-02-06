package pl.feature.toggle.service.write.infrastructure.out.rest;

import lombok.AllArgsConstructor;
import pl.feature.toggle.service.model.environment.EnvironmentId;
import pl.feature.toggle.service.model.project.ProjectId;
import pl.feature.toggle.service.write.application.port.out.ConfigurationClient;
import pl.feature.toggle.service.write.domain.reference.EnvironmentRef;
import pl.feature.toggle.service.write.domain.reference.ProjectRef;

@AllArgsConstructor
class RestConfigurationClient implements ConfigurationClient {

    @Override
    public ProjectRef fetchProject(ProjectId projectId) {
        return null;
    }

    @Override
    public EnvironmentRef fetchEnvironment(ProjectId projectId, EnvironmentId environmentId) {
        return null;
    }
}

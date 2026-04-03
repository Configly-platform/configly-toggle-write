package com.configly.toggle.write.infrastructure.out.rest;

import lombok.AllArgsConstructor;
import com.configly.model.environment.EnvironmentId;
import com.configly.model.project.ProjectId;
import com.configly.web.client.InternalRestClient;
import com.configly.web.client.ServiceId;
import com.configly.toggle.write.application.port.out.ConfigurationClient;
import com.configly.toggle.write.domain.reference.EnvironmentRef;
import com.configly.toggle.write.domain.reference.ProjectRef;
import com.configly.toggle.write.infrastructure.out.rest.dto.EnvironmentRefDto;
import com.configly.toggle.write.infrastructure.out.rest.dto.ProjectRefDto;

@AllArgsConstructor
class RestConfigurationClient implements ConfigurationClient {

    private final InternalRestClient internalRestClient;

    @Override
    public ProjectRef fetchProject(ProjectId projectId) {
        return internalRestClient.get(
                        ServiceId.CONFIGURATION,
                        "/internal/projects/{projectId}/reference",
                        ProjectRefDto.class,
                        projectId.idAsString()
                )
                .toReference();
    }

    @Override
    public EnvironmentRef fetchEnvironment(ProjectId projectId, EnvironmentId environmentId) {
        return internalRestClient.get(
                        ServiceId.CONFIGURATION,
                        "/internal/projects/{projectId}/environments/{environmentId}/reference",
                        EnvironmentRefDto.class,
                        projectId.idAsString(),
                        environmentId.idAsString()
                )
                .toReference();
    }

}

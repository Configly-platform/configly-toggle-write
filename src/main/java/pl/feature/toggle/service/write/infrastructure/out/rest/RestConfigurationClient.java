package pl.feature.toggle.service.write.infrastructure.out.rest;

import lombok.AllArgsConstructor;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;
import pl.feature.toggle.service.model.environment.EnvironmentId;
import pl.feature.toggle.service.model.project.ProjectId;
import pl.feature.toggle.service.web.client.InternalRestClient;
import pl.feature.toggle.service.web.client.ServiceId;
import pl.feature.toggle.service.web.correlation.CorrelationId;
import pl.feature.toggle.service.web.correlation.CorrelationProvider;
import pl.feature.toggle.service.write.application.port.out.ConfigurationClient;
import pl.feature.toggle.service.write.domain.reference.EnvironmentRef;
import pl.feature.toggle.service.write.domain.reference.ProjectRef;
import pl.feature.toggle.service.write.infrastructure.out.rest.dto.EnvironmentRefDto;
import pl.feature.toggle.service.write.infrastructure.out.rest.dto.ProjectRefDto;
import pl.feature.toggle.service.write.infrastructure.out.rest.exception.ConfigurationServiceResponseException;

import java.util.Map;

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

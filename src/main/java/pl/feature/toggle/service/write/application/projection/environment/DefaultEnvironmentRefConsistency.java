package pl.feature.toggle.service.write.application.projection.environment;

import lombok.AllArgsConstructor;
import pl.feature.toggle.service.model.environment.EnvironmentId;
import pl.feature.toggle.service.model.project.ProjectId;
import pl.feature.toggle.service.write.application.port.in.EnvironmentRefConsistency;
import pl.feature.toggle.service.write.application.port.out.ConfigurationClient;
import pl.feature.toggle.service.write.application.port.out.EnvironmentRefRepository;
import pl.feature.toggle.service.write.domain.reference.EnvironmentRef;

@AllArgsConstructor
class DefaultEnvironmentRefConsistency implements EnvironmentRefConsistency {

    private final ConfigurationClient configurationClient;
    private final EnvironmentRefRepository repository;


    @Override
    public EnvironmentRef getTrusted(ProjectId projectId, EnvironmentId environmentId) {
        return repository.findConsistent(projectId, environmentId)
                .orElseGet(() -> fetchAndSaveEnvironmentRef(projectId, environmentId));
    }

    @Override
    public void rebuild(ProjectId projectId, EnvironmentId environmentId) {
        fetchAndSaveEnvironmentRef(projectId, environmentId);
    }

    private EnvironmentRef fetchAndSaveEnvironmentRef(ProjectId projectId, EnvironmentId environmentId) {
        var environmentRef = configurationClient.fetchEnvironment(projectId, environmentId);
        repository.upsert(environmentRef);
        return environmentRef;
    }
}

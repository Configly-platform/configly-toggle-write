package pl.feature.toggle.service.write.application.projection.project;

import lombok.AllArgsConstructor;
import org.springframework.transaction.annotation.Transactional;
import pl.feature.toggle.service.model.project.ProjectId;
import pl.feature.toggle.service.write.application.port.in.ProjectRefConsistency;
import pl.feature.toggle.service.write.application.port.out.ConfigurationClient;
import pl.feature.toggle.service.write.application.port.out.ProjectRefRepository;
import pl.feature.toggle.service.write.domain.reference.ProjectRef;

@AllArgsConstructor
class DefaultProjectRefConsistency implements ProjectRefConsistency {

    private final ConfigurationClient configurationClient;
    private final ProjectRefRepository repository;

    @Override
    public ProjectRef getTrusted(ProjectId projectId) {
        return repository.findConsistent(projectId)
                .orElseGet(() -> fetchAndSaveProjectRef(projectId));
    }

    @Override
    @Transactional
    public void rebuild(ProjectId projectId) {
        fetchAndSaveProjectRef(projectId);
    }

    private ProjectRef fetchAndSaveProjectRef(ProjectId projectId) {
        var projectRef = configurationClient.fetchProject(projectId);
        repository.upsert(projectRef);
        return projectRef;
    }


}

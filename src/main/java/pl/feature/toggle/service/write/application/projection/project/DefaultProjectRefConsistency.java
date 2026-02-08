package pl.feature.toggle.service.write.application.projection.project;

import lombok.AllArgsConstructor;
import pl.feature.toggle.service.model.project.ProjectId;
import pl.feature.toggle.service.write.application.port.in.ProjectRefConsistency;
import pl.feature.toggle.service.write.application.port.out.ConfigurationClient;
import pl.feature.toggle.service.write.application.port.out.ProjectRefProjectionRepository;
import pl.feature.toggle.service.write.application.port.out.ProjectRefQueryRepository;
import pl.feature.toggle.service.write.domain.reference.ProjectRef;

@AllArgsConstructor
class DefaultProjectRefConsistency implements ProjectRefConsistency {

    private final ConfigurationClient configurationClient;
    private final ProjectRefProjectionRepository projectionRepository;
    private final ProjectRefQueryRepository queryRepository;

    @Override
    public ProjectRef getTrusted(ProjectId projectId) {
        return queryRepository.findConsistent(projectId)
                .orElseGet(() -> fetchAndSaveProjectRef(projectId));
    }

    @Override
    public void rebuild(ProjectId projectId) {
        fetchAndSaveProjectRef(projectId);
    }

    private ProjectRef fetchAndSaveProjectRef(ProjectId projectId) {
        var projectRef = configurationClient.fetchProject(projectId);
        projectionRepository.upsert(projectRef);
        return projectRef;
    }


}

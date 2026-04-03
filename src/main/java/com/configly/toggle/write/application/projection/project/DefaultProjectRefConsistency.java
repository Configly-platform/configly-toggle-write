package com.configly.toggle.write.application.projection.project;

import lombok.AllArgsConstructor;
import com.configly.model.project.ProjectId;
import com.configly.toggle.write.application.port.in.ProjectRefConsistency;
import com.configly.toggle.write.application.port.out.ConfigurationClient;
import com.configly.toggle.write.application.port.out.ProjectRefProjectionRepository;
import com.configly.toggle.write.application.port.out.ProjectRefQueryRepository;
import com.configly.toggle.write.domain.reference.ProjectRef;

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

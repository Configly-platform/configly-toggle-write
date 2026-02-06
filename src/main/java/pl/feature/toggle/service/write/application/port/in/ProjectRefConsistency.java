package pl.feature.toggle.service.write.application.port.in;

import pl.feature.toggle.service.model.project.ProjectId;
import pl.feature.toggle.service.write.domain.reference.ProjectRef;

public interface ProjectRefConsistency {
    ProjectRef getTrusted(ProjectId projectId);

    void rebuild(ProjectId projectId);
}

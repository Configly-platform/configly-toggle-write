package pl.feature.toggle.service.write.application.port.in;

import pl.feature.toggle.service.model.environment.EnvironmentId;
import pl.feature.toggle.service.model.project.ProjectId;
import pl.feature.toggle.service.write.domain.reference.EnvironmentRef;

public interface EnvironmentRefConsistency {
    EnvironmentRef getTrusted(ProjectId projectId, EnvironmentId environmentId);

    void rebuild(ProjectId projectId, EnvironmentId environmentId);
}

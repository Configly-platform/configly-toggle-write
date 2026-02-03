package pl.feature.toggle.service.write.application.port.out;

import pl.feature.toggle.service.model.environment.EnvironmentId;
import pl.feature.toggle.service.model.project.ProjectId;
import pl.feature.toggle.service.write.domain.reference.EnvironmentRef;

public interface EnvironmentRefRepository {

    EnvironmentRef getOrThrow(ProjectId projectId, EnvironmentId environmentId);

    void upsert(EnvironmentRef environmentRef);

}

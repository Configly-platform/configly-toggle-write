package pl.feature.toggle.service.write.application.port.out;

import pl.feature.toggle.service.model.environment.EnvironmentId;
import pl.feature.toggle.service.model.project.ProjectId;
import pl.feature.toggle.service.write.domain.reference.EnvironmentRef;

import java.util.Optional;

public interface EnvironmentRefQueryRepository {

    Optional<EnvironmentRef> find(ProjectId projectId, EnvironmentId environmentId);

    Optional<EnvironmentRef> findConsistent(ProjectId projectId, EnvironmentId environmentId);

}

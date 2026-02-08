package pl.feature.toggle.service.write.application.port.out;

import pl.feature.toggle.service.model.project.ProjectId;
import pl.feature.toggle.service.write.domain.reference.ProjectRef;

import java.util.Optional;

public interface ProjectRefQueryRepository {

    Optional<ProjectRef> find(ProjectId projectId);

    Optional<ProjectRef> findConsistent(ProjectId projectId);
}

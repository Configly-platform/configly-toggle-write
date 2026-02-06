package pl.feature.toggle.service.write.application.port.out;

import pl.feature.toggle.service.model.project.ProjectId;
import pl.feature.toggle.service.write.domain.reference.ProjectRef;

import java.util.Optional;

public interface ProjectRefRepository {

    Optional<ProjectRef> find(ProjectId projectId);

    Optional<ProjectRef> findConsistent(ProjectId projectId);

    void insert(ProjectRef ref);

    void update(ProjectRef ref);

    void upsert(ProjectRef ref);

    boolean markInconsistentIfNotMarked(ProjectId projectId);

}

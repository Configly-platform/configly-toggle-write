package pl.feature.toggle.service.write.application.port.out;

import pl.feature.toggle.service.model.project.ProjectId;
import pl.feature.toggle.service.write.domain.reference.ProjectRef;

public interface ProjectRefProjectionRepository {

    void insert(ProjectRef ref);

    void update(ProjectRef ref);

    void upsert(ProjectRef ref);

    boolean markInconsistentIfNotMarked(ProjectId projectId);

}

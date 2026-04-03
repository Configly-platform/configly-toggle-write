package com.configly.toggle.write.application.port.out;

import com.configly.model.project.ProjectId;
import com.configly.toggle.write.domain.reference.ProjectRef;

public interface ProjectRefProjectionRepository {

    void insert(ProjectRef ref);

    void update(ProjectRef ref);

    void upsert(ProjectRef ref);

    boolean markInconsistentIfNotMarked(ProjectId projectId);

}

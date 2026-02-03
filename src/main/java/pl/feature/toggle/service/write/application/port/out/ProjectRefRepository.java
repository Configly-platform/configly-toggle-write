package pl.feature.toggle.service.write.application.port.out;

import pl.feature.toggle.service.model.project.ProjectId;

public interface ProjectRefRepository {

    ProjectRef getOrThrow(ProjectId projectId);

    void upsert(ProjectRef projectRef);

}

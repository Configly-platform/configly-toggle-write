package pl.feature.toggle.service.write.application.port.out;

import pl.feature.toggle.service.write.domain.project.ProjectSnapshot;
import pl.feature.toggle.service.model.project.ProjectId;

import java.util.Optional;

public interface ProjectRepository {

    Optional<ProjectSnapshot> findById(ProjectId projectId);

    void save(ProjectSnapshot projectSnapshot);

}

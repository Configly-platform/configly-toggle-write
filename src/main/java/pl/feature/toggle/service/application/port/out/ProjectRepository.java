package pl.feature.toggle.service.application.port.out;

import pl.feature.toggle.service.domain.project.ProjectSnapshot;
import pl.feature.toggle.service.model.project.ProjectId;

import java.util.Optional;

public interface ProjectRepository {

    Optional<ProjectSnapshot> findById(ProjectId projectId);

    void save(ProjectSnapshot projectSnapshot);

}

package pl.feature.toggle.service.application.port.out;

import com.ftaas.domain.project.ProjectId;
import pl.feature.toggle.service.domain.project.ProjectSnapshot;

import java.util.Optional;

public interface ProjectRepository {

    Optional<ProjectSnapshot> findById(ProjectId projectId);

    void save(ProjectSnapshot projectSnapshot);

}

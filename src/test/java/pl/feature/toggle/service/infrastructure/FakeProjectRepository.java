package pl.feature.toggle.service.infrastructure;

import com.ftaas.domain.project.ProjectId;
import pl.feature.toggle.service.application.port.out.ProjectRepository;
import pl.feature.toggle.service.domain.project.ProjectSnapshot;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class FakeProjectRepository implements ProjectRepository {

    private final Map<ProjectId, ProjectSnapshot> projects = new HashMap<>();

    @Override
    public Optional<ProjectSnapshot> findById(ProjectId projectId) {
        return Optional.ofNullable(projects.get(projectId));
    }

    @Override
    public void save(ProjectSnapshot projectSnapshot) {
        projects.put(projectSnapshot.id(), projectSnapshot);
    }

    public void clear() {
        projects.clear();
    }
}

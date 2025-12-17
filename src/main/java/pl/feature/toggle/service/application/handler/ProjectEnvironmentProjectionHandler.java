package pl.feature.toggle.service.application.handler;

import com.ftaas.contracts.event.projects.EnvironmentCreated;
import com.ftaas.contracts.event.projects.ProjectCreated;
import pl.feature.toggle.service.application.port.in.ProjectEnvironmentProjectionUseCase;
import pl.feature.toggle.service.application.port.out.EnvironmentRepository;
import pl.feature.toggle.service.application.port.out.ProjectRepository;
import pl.feature.toggle.service.domain.environment.EnvironmentSnapshot;
import pl.feature.toggle.service.domain.project.ProjectSnapshot;
import lombok.AllArgsConstructor;
import org.springframework.transaction.annotation.Transactional;

@AllArgsConstructor
class ProjectEnvironmentProjectionHandler implements ProjectEnvironmentProjectionUseCase {

    private final ProjectRepository projectRepository;
    private final EnvironmentRepository environmentRepository;

    @Transactional
    @Override
    public void handle(ProjectCreated event) {
        var projectView = ProjectSnapshot.from(event);
        projectRepository.save(projectView);
    }

    @Transactional
    @Override
    public void handle(EnvironmentCreated event) {
        var environmentView = EnvironmentSnapshot.from(event);
        environmentRepository.save(environmentView);
    }

}

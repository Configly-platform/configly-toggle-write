package pl.feature.toggle.service.write.application.handler;

import pl.feature.toggle.service.write.application.port.in.ProjectEnvironmentProjectionUseCase;
import pl.feature.toggle.service.write.application.port.out.EnvironmentRepository;
import pl.feature.toggle.service.write.application.port.out.ProjectRepository;
import pl.feature.toggle.service.contracts.event.projects.EnvironmentCreated;
import pl.feature.toggle.service.contracts.event.projects.ProjectCreated;
import pl.feature.toggle.service.write.domain.environment.EnvironmentSnapshot;
import pl.feature.toggle.service.write.domain.project.ProjectSnapshot;
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

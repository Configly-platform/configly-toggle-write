package pl.feature.toggle.service.write.application.handler;

import pl.feature.toggle.service.write.application.port.in.ProjectEnvironmentProjection;
import pl.feature.toggle.service.write.application.port.out.EnvironmentRefRepository;
import pl.feature.toggle.service.write.application.port.out.ProjectRefRepository;
import pl.feature.toggle.service.contracts.event.environment.EnvironmentCreated;
import pl.feature.toggle.service.contracts.event.project.ProjectCreated;
import lombok.AllArgsConstructor;
import org.springframework.transaction.annotation.Transactional;
import pl.feature.toggle.service.write.domain.reference.EnvironmentRef;

@AllArgsConstructor
class ProjectEnvironmentProjectionHandler implements ProjectEnvironmentProjection {

    private final ProjectRefRepository projectRefRepository;
    private final EnvironmentRefRepository environmentRefRepository;

    @Transactional
    @Override
    public void handle(ProjectCreated event) {
        var projectRef = ProjectRef.from(event);
        projectRefRepository.upsert(projectRef);
    }

    @Transactional
    @Override
    public void handle(EnvironmentCreated event) {
        var environmentView = EnvironmentRef.from(event);
        environmentRefRepository.upsert(environmentView);
    }

}

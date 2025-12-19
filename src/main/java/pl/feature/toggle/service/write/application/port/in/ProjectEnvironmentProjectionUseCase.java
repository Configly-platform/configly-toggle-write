package pl.feature.toggle.service.write.application.port.in;


import pl.feature.toggle.service.contracts.event.projects.EnvironmentCreated;
import pl.feature.toggle.service.contracts.event.projects.ProjectCreated;

public interface ProjectEnvironmentProjectionUseCase {

    void handle(ProjectCreated event);

    void handle(EnvironmentCreated event);

}

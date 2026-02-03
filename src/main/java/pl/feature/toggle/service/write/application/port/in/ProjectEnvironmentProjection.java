package pl.feature.toggle.service.write.application.port.in;


import pl.feature.toggle.service.contracts.event.environment.EnvironmentCreated;
import pl.feature.toggle.service.contracts.event.project.ProjectCreated;

public interface ProjectEnvironmentProjection {

    void handle(ProjectCreated event);

    void handle(EnvironmentCreated event);

}
